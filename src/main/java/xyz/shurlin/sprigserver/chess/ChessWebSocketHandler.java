package xyz.shurlin.sprigserver.chess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.shurlin.sprigserver.chess.dto.ChessStateDto;
import xyz.shurlin.sprigserver.chess.dto.MovePayload;
import xyz.shurlin.sprigserver.chess.dto.MoveRequest;

import java.net.URI;
import java.util.Map;

@Component
public class ChessWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final GameSessionManager sessionManager;
    private final InMemoryGameStore store;
    private final Logger logger = LoggerFactory.getLogger(ChessWebSocketHandler.class);

    public ChessWebSocketHandler(GameSessionManager sessionManager, InMemoryGameStore gameStore) {
        this.sessionManager = sessionManager;
        this.store = gameStore;
        logger.info("GameWebSocketHandler ctor, this=" + System.identityHashCode(this)
                + ", store=" + System.identityHashCode(store));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 尝试从 query string 里解析 gameId
        Long id = extractGameId(session.getUri());
        if (id != null) {
            long gameId = id;
            session.getAttributes().put("gameId", gameId);
            sessionManager.addToRoom(gameId, session);

            // 发送当前棋盘状态给新加入客户端
            ChessStateDto state = new ChessStateDto();
            InMemoryGameStore.GameState gameState = store.getOrCreateGame(gameId);
            state.gameId = gameId;
            state.board = InMemoryGameStore.copyBoard(gameState);
            state.lastMoveIndex = gameState.lastIndex.get();
            state.turn = gameState.isRed ? "red" : "black";
            String json = mapper.writeValueAsString(state);
            session.sendMessage(new TextMessage(json));
            logger.info("New WebSocket connection established for gameId: {}", gameId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String json = message.getPayload();
        MoveRequest req;
//        logger.info("Received message: {}", json);
        try {
            req = mapper.readValue(json, MoveRequest.class);
        } catch (Exception ex) {
            return;
        }
        if (!"MOVE".equalsIgnoreCase(req.type) || req.gameId == null) return;

//        logger.info("Processing MOVE for gameId: {}", req.gameId);

        // 应用 move
        int idx = store.applyMove(req.gameId, req);

        // 广播 move 给所有客户端
        MovePayload payload = new MovePayload(req.gameId, req.from, req.to, store.getGame(req.gameId).isRed ? "red" : "black");

        sessionManager.broadcast(req.gameId, mapper.writeValueAsString(payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attrs = session.getAttributes();
        Object o = attrs.get("gameId");
        if (o instanceof Long) {
            sessionManager.removeFromRoom((Long) o, session);
        }
    }

    private Long extractGameId(URI uri) {
        if (uri == null) return null;
        String q = uri.getQuery();
        if (q == null) return null;
        for (String kv : q.split("&")) {
            if (kv.startsWith("gameId=")) {
                try { return Long.parseLong(kv.substring(7)); } catch (Exception ignored) {}
            }
        }
        return null;
    }

}
