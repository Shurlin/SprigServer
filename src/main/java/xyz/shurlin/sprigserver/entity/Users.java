package xyz.shurlin.sprigserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.ToString;
import xyz.shurlin.sprigserver.dto.RegisterRequest;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@ToString
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    @TableField("displayName")
    private String displayName;

    private String email;

    @TableField("createdAt")
    private LocalDateTime createdAt;


    public Users(Long id, String username, String password, String displayName, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public static Users fromReg(Long id, RegisterRequest req) {
        return new Users(id, req.getUsername(), req.getPassword(), req.getDisplayName(), req.getEmail(), LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
