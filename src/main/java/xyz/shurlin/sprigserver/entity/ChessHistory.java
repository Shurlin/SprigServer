package xyz.shurlin.sprigserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("chess_history")
public class ChessHistory {
    private String user1;
    private String user2;
    private String state;
    private LocalDateTime time;

    public ChessHistory(String user1, String user2, String state, LocalDateTime time) {
        this.user1 = user1;
        this.user2 = user2;
        this.state = state;
        this.time = time;
    }

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    public String getState() {
        return state;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
