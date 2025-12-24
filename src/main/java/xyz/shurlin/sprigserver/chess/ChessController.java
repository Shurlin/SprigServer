package xyz.shurlin.sprigserver.chess;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.shurlin.sprigserver.chess.dto.ChessStateDto;
import xyz.shurlin.sprigserver.entity.ChessHistory;
import xyz.shurlin.sprigserver.service.IChessHistoryService;

import java.util.List;

@RestController
@RequestMapping("/game/chess")
public class ChessController {
    private final InMemoryGameStore store;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ChessController.class);
    private final IChessHistoryService service;

    public ChessController(InMemoryGameStore store, IChessHistoryService service) {
        this.store = store;
        this.service = service;
    }

    @GetMapping("/listRooms")
    public ResponseEntity<List<Long>> listRooms() {
        return ResponseEntity.ok(store.listRooms());
    }

    @GetMapping("/{gameId}/board")
    public ResponseEntity<ChessStateDto> getBoard(@PathVariable("gameId") long gameId) {
        ChessStateDto state = new ChessStateDto();
        InMemoryGameStore.GameState gameState = store.getGame(gameId);
        state.gameId = gameId;
        state.playerRed = gameState.userRed;
        state.playerBlack = gameState.userBlack;
        state.board = InMemoryGameStore.copyBoard(gameState);
        state.lastMoveIndex = gameState.lastIndex.get();
        state.turn = gameState.isRed ? "red" : "black";
//        logger.info("Fetching board for gameId: {}", gameId);
        return ResponseEntity.ok(state);
    }

    @GetMapping("/listHistory")
    public ResponseEntity<List<ChessHistory >> listHistory(@RequestParam("username") String username) {
        return ResponseEntity.ok(service.listHistory(username));
    }
}
