package xyz.shurlin.sprigserver.chess.dto;

import xyz.shurlin.sprigserver.chess.InMemoryGameStore;

public class ChessStateDto {
    public String type = "CHESS_STATE";;
    public Long gameId;
    public String playerRed;
    public String playerBlack;
    public String[][] board; // [9][10]
    public int lastMoveIndex; // -1 表示无步
    public String turn; // "red" or "black"

    public ChessStateDto() {

    }
}
