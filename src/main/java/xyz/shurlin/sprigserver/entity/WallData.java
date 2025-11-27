package xyz.shurlin.sprigserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import xyz.shurlin.sprigserver.dto.WallFetchResponse;

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
@Getter
@Setter
@ToString
@TableName("wall_data")
public class WallData implements Serializable {
    private Long id;

    private String title;

    private String content;

    @TableField("createdAt")
    private LocalDateTime createdAt;

    @TableField(value = "createdBy")
    @Nullable
    private String createdBy; // username

    private Long likes;

    public WallData(Long id, String title, String content, LocalDateTime createdAt, @Nullable String createdBy, Long likes) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.likes = likes;
    }

    public static WallFetchResponse toResponse(WallData data) {
        return new WallFetchResponse(data.id, data.title, data.content, data.createdAt, data.likes);
    }

}
