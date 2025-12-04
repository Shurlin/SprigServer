package xyz.shurlin.sprigserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.shurlin.sprigserver.entity.WallData;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@Mapper
public interface WallDataMapper extends BaseMapper<WallData> {

    @Select("SELECT id FROM wall_data ORDER BY id DESC LIMIT 1")
    Long selectLatestId();
}
