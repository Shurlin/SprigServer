package xyz.shurlin.sprigserver.chess.dto;

public class MovePayload {
    public String type;
    public Long gameId;
    public Position from;
    public Position to;
    public String next;

    public MovePayload(Long gameId, Position from, Position to, String next) {
        this.type = "MOVE_APPLIED";
        this.gameId = gameId;
        this.from = from;
        this.to = to;
        this.next = next;
    }
}
