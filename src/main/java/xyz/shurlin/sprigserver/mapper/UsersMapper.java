package xyz.shurlin.sprigserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.shurlin.sprigserver.entity.Users;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

    @Select("SELECT id FROM users ORDER BY id DESC LIMIT 1")
    Long selectLatestId();

}
