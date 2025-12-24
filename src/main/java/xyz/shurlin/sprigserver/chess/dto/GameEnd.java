package xyz.shurlin.sprigserver.chess.dto;

public class GameEnd {
    public String type;
    public Long gameId;
    public String winner;

    public GameEnd(Long gameId, String winner) {
        this.type = "GAME_END";
        this.gameId = gameId;
        this.winner = winner;
    }
}
