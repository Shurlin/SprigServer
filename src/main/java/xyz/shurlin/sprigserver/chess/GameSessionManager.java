package xyz.shurlin.sprigserver.chess;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class GameSessionManager {
    private final ConcurrentHashMap<Long, CopyOnWriteArraySet<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(GameSessionManager.class);

    public void addToRoom(long gameId, WebSocketSession session) {
        rooms.computeIfAbsent(gameId, k -> new CopyOnWriteArraySet<>()).add(session);
    }

    public void removeFromRoom(long gameId, WebSocketSession session) {
        Set<WebSocketSession> set = rooms.get(gameId);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) {
                rooms.remove(gameId);
                logger.info("Room for gameId {} is empty and removed.", gameId);
            }
        }
    }

    public void broadcast(long gameId, String payload) {
        Set<WebSocketSession> set = rooms.get(gameId);
        if (set == null) return;
        for (WebSocketSession s : set) {
            try {
                if (s.isOpen()) s.sendMessage(new TextMessage(payload));
            } catch (Exception ignored) {
            }
        }
    }
}
