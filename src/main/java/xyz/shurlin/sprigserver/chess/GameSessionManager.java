package xyz.shurlin.sprigserver.chess;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import xyz.shurlin.sprigserver.chess.voice.UdpVoiceRelay;
import xyz.shurlin.sprigserver.entity.ChessHistory;
import xyz.shurlin.sprigserver.mapper.ChessHistoryMapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class GameSessionManager {
    private final ConcurrentHashMap<Long, CopyOnWriteArraySet<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(GameSessionManager.class);
    private final InMemoryGameStore store;
    @Autowired
    private final ChessHistoryMapper historyMapper;

    public GameSessionManager(InMemoryGameStore store, ChessHistoryMapper historyMapper) {
        this.store = store;
        this.historyMapper = historyMapper;
    }

    public void addToRoom(long gameId, WebSocketSession session) {
        rooms.computeIfAbsent(gameId, k -> new CopyOnWriteArraySet<>()).add(session);
    }

    public void addVoiceToRoom(long gameId, String username, DatagramPacket packet) {
        Set<WebSocketSession> set = rooms.get(gameId);
        if (set == null) return;
        for (WebSocketSession s : set) {
            try {
                if (s.isOpen() && s.getAttributes().get("username").equals(username)) {
                    s.getAttributes().put("address", packet.getAddress());
                    s.getAttributes().put("port", packet.getPort());
                    logger.info("Updated voice address for user: {} to {}:{}", username, packet.getAddress(), packet.getPort());
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void removeFromRoom(long gameId, WebSocketSession session) {
        Set<WebSocketSession> set = rooms.get(gameId);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) {
                InMemoryGameStore.GameState gameState = store.getGame(gameId);
                logger.info("Room for gameId {} is empty and removed.", gameId);
                if (gameState.userRed != null && gameState.userBlack != null) {
                    historyMapper.insert(new ChessHistory(gameState.userRed, gameState.userBlack, "DISCONNECT", LocalDateTime.now()));
                    historyMapper.insert(new ChessHistory(gameState.userBlack, gameState.userRed, "DISCONNECT", LocalDateTime.now()));
                }
                rooms.remove(gameId);
                store.removeGame(gameId);
            }
        }
    }

    public void broadcast(long gameId, String message) {
        Set<WebSocketSession> set = rooms.get(gameId);
        if (set == null) return;
        for (WebSocketSession s : set) {
            try {
                if (s.isOpen()) s.sendMessage(new TextMessage(message));
            } catch (Exception ignored) {
            }
        }
    }

    public void broadcast(DatagramSocket socket, long gameId, byte[] data, int length, String except) {
//        logger.info("Broadcasting voice packet in gameId: {} excluding user: {}", gameId, except);
        Set<WebSocketSession> set = rooms.get(gameId);
        if (set == null) return;
        for (WebSocketSession s : set) {
            try {
//                logger.info("Checking session for user: {}", s.getAttributes().get("username"));
                if (s.isOpen() && !s.getAttributes().get("username").equals(except)) {
//                    logger.info("Preparing to send voice packet to user: {}", s.getAttributes().get("username"));
                    DatagramPacket packet = new DatagramPacket(
                            data,
                            length,
                            (java.net.InetAddress) s.getAttributes().get("address"),
                            (int) s.getAttributes().get("port"));
                    socket.send(packet);
//                    logger.info("voice from {} sent to {}", except, s.getAttributes().get("username"));
                }
            } catch (Exception ignored) {
            }
        }
    }
}
