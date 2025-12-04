package xyz.shurlin.sprigserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.shurlin.sprigserver.entity.SpeedRank;


@Mapper
public interface SpeedRankMapper extends BaseMapper<SpeedRank> {
    @Select("SELECT id FROM speed_rank ORDER BY id DESC LIMIT 1")
    Long selectLatestId();
}
