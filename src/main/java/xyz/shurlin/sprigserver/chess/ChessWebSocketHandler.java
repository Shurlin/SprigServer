package xyz.shurlin.sprigserver.chess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.shurlin.sprigserver.chess.dto.*;
import xyz.shurlin.sprigserver.entity.ChessHistory;
import xyz.shurlin.sprigserver.mapper.ChessHistoryMapper;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ChessWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final GameSessionManager sessionManager;
    private final InMemoryGameStore store;
    @Autowired
    private final ChessHistoryMapper historyMapper;
    private final Logger logger = LoggerFactory.getLogger(ChessWebSocketHandler.class);
    private final Gson gson = new Gson();

    public ChessWebSocketHandler(GameSessionManager sessionManager, InMemoryGameStore gameStore, ChessHistoryMapper historyMapper) {
        this.sessionManager = sessionManager;
        this.store = gameStore;
//        logger.info("GameWebSocketHandler ctor, this=" + System.identityHashCode(this)
//                + ", store=" + System.identityHashCode(store));
        this.historyMapper = historyMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 尝试从 query string 里解析 gameId
        Long id = extractGameId(session.getUri());
        String username = extractUsername(session.getUri());
//        logger.info("New connection from user: {} with URI: {}", username, session.getUri());
        if (id != null && username != null && !username.isEmpty()) {
            long gameId = id;
            session.getAttributes().put("gameId", gameId);
            session.getAttributes().put("username", username);
            sessionManager.addToRoom(gameId, session);

//            logger.info("New WebSocket connection established for gameId: {}", gameId);
            logger.info("Player {} joined game {}", username, gameId);

            //匹配对局
            InMemoryGameStore.GameState gameState = store.getOrCreateGame(gameId, username);
            if (gameState.userBlack != null) {
                logger.info("Game {} matched: Red={}, Black={}", gameId, gameState.userRed, gameState.userBlack);

                sendChessState(gameId, gameState);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String json = message.getPayload();
        JsonObject jsonObj = gson.fromJson(json, JsonObject.class);
        String type = jsonObj.get("type").getAsString();
        long gameId = jsonObj.get("gameId").getAsLong();

        switch (type) {
            case "MOVE":
                MoveRequest req;
                try {
                    req = mapper.readValue(json, MoveRequest.class);
                } catch (Exception ex) {
                    logger.error("Failed to parse message: {}", json, ex);
                    return;
                }
                handleMoveRequest(req);
                break;
            case "GAME_GIVE_UP":
                GameGiveUp giveUp;;
                try {
                    giveUp = mapper.readValue(json, GameGiveUp.class);
                } catch (Exception ex) {
                    logger.error("Failed to parse message: {}", json, ex);
                    return;
                }
                endGame(gameId, giveUp.loser.equals(store.getGame(gameId).userBlack));
                break;
            case "GAME_RESTART":
                InMemoryGameStore.GameState gameState = store.getGame(gameId);
                if (gameState != null && !gameState.playing) {
                    store.resetGame(gameState);
                    sendChessState(gameId, gameState);
                }
                break;
            case "VOICE_SDP":
            case "VOICE_ICE":
                // 语音消息直接广播
                sessionManager.broadcast(gameId, json);
                break;
            default:
                logger.warn("Unknown message type: {}", type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attrs = session.getAttributes();
        Object o = attrs.get("gameId");
        if (o instanceof Long) {
            logger.info("User {} disconnected from game {}", attrs.get("username"), o);
            sessionManager.removeFromRoom((Long) o, session);
        }
    }

    private void handleMoveRequest(MoveRequest req) throws JsonProcessingException {
        //非本方回合不处理
        InMemoryGameStore.GameState gameState = store.getGame(req.gameId);
//        logger.info("req username: {}, red player: {}, black player: {}", req.username,
//                gameState.userRed, gameState.userBlack);
        if (gameState == null || req.username == null) return;
        if (gameState.isRed && !req.username.equals(gameState.userRed) ||
                !gameState.isRed && !req.username.equals(gameState.userBlack)) {
            return;
        }

        // 应用 move
        int idx = store.applyMove(req.gameId, req);

        // 广播 move 给所有客户端
        MovePayload payload = new MovePayload(req.gameId, req.from, req.to, gameState.isRed ? "red" : "black");
        sessionManager.broadcast(req.gameId, mapper.writeValueAsString(payload));

        // 将死
        if (idx < 0) {
            endGame(req.gameId, idx == -1);
        }
    }

    private void sendChessState(long gameId, InMemoryGameStore.GameState gameState) throws JsonProcessingException {
        // 发送当前棋盘状态给新加入客户端
        ChessStateDto state = new ChessStateDto();
        state.gameId = gameId;
        state.playerRed = gameState.userRed;
        state.playerBlack = gameState.userBlack;
        state.board = InMemoryGameStore.copyBoard(gameState);
        state.lastMoveIndex = gameState.lastIndex.get();
        state.turn = gameState.isRed ? "red" : "black";
        String json = mapper.writeValueAsString(state);

        // 广播给房间内所有客户端
        sessionManager.broadcast(gameId, json);
    }

    private void endGame(long gameId, boolean winRed) throws JsonProcessingException {
        InMemoryGameStore.GameState gameState = store.getGame(gameId);
        String winner = winRed ? gameState.userRed : gameState.userBlack;
        logger.info("Game {} ended. Winner: {}", gameId, winner);
        gameState.playing = false;

        //广播结束消息
        GameEnd endMsg = new GameEnd(gameId, winner);
        sessionManager.broadcast(gameId, mapper.writeValueAsString(endMsg));

        //记录历史
        historyMapper.insert(new ChessHistory(gameState.userRed, gameState.userBlack, winRed ? "WIN" : "LOSE", LocalDateTime.now()));
        historyMapper.insert(new ChessHistory(gameState.userBlack, gameState.userRed, winRed ? "LOSE" : "WIN", LocalDateTime.now()));
    }

    private Long extractGameId(URI uri) {
        if (uri == null) return null;
        String q = uri.getQuery();
        if (q == null) return null;
        for (String kv : q.split("&")) {
            if (kv.startsWith("gameId=")) {
                try {
                    return Long.parseLong(kv.substring(7));
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    private String extractUsername(URI uri) {
        if (uri == null) return null;
        String q = uri.getQuery();
        if (q == null) return null;
        for (String kv : q.split("&")) {
            if (kv.startsWith("username=")) {
                return kv.substring(9);
            }
        }
        return null;
    }

}
