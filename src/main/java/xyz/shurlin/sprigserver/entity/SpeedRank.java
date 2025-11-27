package xyz.shurlin.sprigserver.entity;

public class SpeedRank {
    private Long id;
    private String username;
    private int score;

    public SpeedRank(Long id, String username, int score) {
        this.id = id;
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}
