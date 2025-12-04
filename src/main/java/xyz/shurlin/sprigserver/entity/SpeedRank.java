package xyz.shurlin.sprigserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("speed_rank")
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
