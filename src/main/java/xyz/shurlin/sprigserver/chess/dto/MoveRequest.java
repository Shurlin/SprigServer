package xyz.shurlin.sprigserver.chess.dto;

public class MoveRequest {
    public String type;
    public Long gameId;
    public Position from;
    public Position to;
}

