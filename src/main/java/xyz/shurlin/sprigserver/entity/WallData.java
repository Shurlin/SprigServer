package xyz.shurlin.sprigserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    private static final long serialVersionUID = 1L;

    private LocalDateTime time;

    private String content;

    private Integer id;
}
