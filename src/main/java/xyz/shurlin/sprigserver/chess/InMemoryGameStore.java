package xyz.shurlin.sprigserver.chess;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import xyz.shurlin.sprigserver.chess.dto.MoveRequest;
import xyz.shurlin.sprigserver.chess.dto.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryGameStore {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(InMemoryGameStore.class);

    public static class GameState {
        public final String[][] board = new String[9][10];
        public final AtomicInteger lastIndex = new AtomicInteger(-1);
        public boolean isRed = true;
    }

    public InMemoryGameStore() {
        logger.info("InMemoryGameStore::<ctor> this={} classloader={} thread={}",
                System.identityHashCode(this),
                System.identityHashCode(this.getClass().getClassLoader()),
                Thread.currentThread().getName());
        // 打印创建栈，便于定位哪个位置 new 了它
        Exception e = new Exception("stack-trace-for-store-ctor");
        logger.info("store ctor stack: ", e);
    }

    private final ConcurrentHashMap<Long, GameState> games = new ConcurrentHashMap<>();

    public GameState getOrCreateGame(long gameId) {
//        synchronized (this) {
//            logger.info("InMemoryGameStore instance: {}", System.identityHashCode(this));
//            StringBuilder sb = new StringBuilder();
//            for(Long id: games.keySet()) {
//                sb.append(id).append(", ");
//            }
//            logger.info("Existing games: {}, Game id: {}", sb, gameId);
//            if (games.containsKey(gameId)) {
//                return games.get(gameId);
//            } else {
//                GameState s = new GameState();
//                initStandardBoard(s.board);
//                games.put(gameId, s);
//                logger.info("Created new game with gameId: {}", gameId);
//                return s;
//            }
//        }
        logger.info("InMemoryGameStore instance: {}", System.identityHashCode(this));
        return games.computeIfAbsent(gameId, id -> {
            GameState s = new GameState();
            initStandardBoard(s.board);
            logger.info("Created new game with gameId: {}", gameId);
            return s;
        });
    }

    public GameState getGame(long gameId) {
        return games.get(gameId);
    }

    public synchronized int applyMove(long gameId, MoveRequest move) {
        GameState s = getOrCreateGame(gameId);
        Position to = move.to;
        Position from = move.from;
        s.board[to.x][to.y] = s.board[from.x][from.y];
        s.board[from.x][from.y] = null;
        s.isRed = !s.isRed;
        return s.lastIndex.incrementAndGet();
    }

    public static String[][] copyBoard(GameState s) {
        String[][] copy = new String[9][10];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 10; j++)
                copy[i][j] = s.board[i][j];
        return copy;
    }

    public int getLastIndex(long gameId) {
        GameState s = games.get(gameId);
        return s == null ? -1 : s.lastIndex.get();
    }

    private void initStandardBoard(String[][] b) {
        for (int i = 0; i < 9; i++) for (int j = 0; j < 10; j++) b[i][j] = null;
        b[0][0] = "CHE_B";
        b[8][0] = "CHE_B";
        b[1][0] = "MA_B";
        b[7][0] = "MA_B";
        b[2][0] = "XIANG_B";
        b[6][0] = "XIANG_B";
        b[3][0] = "SHI_B";
        b[5][0] = "SHI_B";
        b[4][0] = "JIANG_B";
        b[1][2] = "PAO_B";
        b[7][2] = "PAO_B";
        b[0][3] = "BING_B";
        b[2][3] = "BING_B";
        b[4][3] = "BING_B";
        b[6][3] = "BING_B";
        b[8][3] = "BING_B";

        // red (bottom)
        b[0][9] = "CHE_R";
        b[8][9] = "CHE_R";
        b[1][9] = "MA_R";
        b[7][9] = "MA_R";
        b[2][9] = "XIANG_R";
        b[6][9] = "XIANG_R";
        b[3][9] = "SHI_R";
        b[5][9] = "SHI_R";
        b[4][9] = "JIANG_R";
        b[1][7] = "PAO_R";
        b[7][7] = "PAO_R";
        b[0][6] = "BING_R";
        b[2][6] = "BING_R";
        b[4][6] = "BING_R";
        b[6][6] = "BING_R";
        b[8][6] = "BING_R";
    }

    public List<Long> listRooms() {
        return new ArrayList<>(games.keySet());
    }
}
