package xyz.shurlin.sprigserver.chess;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.shurlin.sprigserver.chess.dto.ChessStateDto;

import java.util.List;

@RestController
@RequestMapping("/game/chess")
public class ChessController {
    private final InMemoryGameStore store;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ChessController.class);

    public ChessController(InMemoryGameStore store) {
        this.store = store;
    }

    @GetMapping("/listRooms")
    public ResponseEntity<List<Long>> listRooms() {
        return ResponseEntity.ok(store.listRooms());
    }

    @GetMapping("/{gameId}/board")
    public ResponseEntity<ChessStateDto> getBoard(@PathVariable("gameId") long gameId) {
        ChessStateDto state = new ChessStateDto();
        InMemoryGameStore.GameState gameState = store.getOrCreateGame(gameId);
        state.gameId = gameId;
        state.board = InMemoryGameStore.copyBoard(gameState);
        state.lastMoveIndex = gameState.lastIndex.get();
        state.turn = gameState.isRed ? "red" : "black";
//        logger.info("Fetching board for gameId: {}", gameId);
        return ResponseEntity.ok(state);
    }
}
