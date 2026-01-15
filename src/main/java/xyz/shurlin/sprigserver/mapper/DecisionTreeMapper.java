package xyz.shurlin.sprigserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.shurlin.sprigserver.entity.DecisionTree;

import java.util.List;

@Mapper
public interface DecisionTreeMapper extends BaseMapper<DecisionTree> {

//    @Select("SELECT * FROM decision_tree ORDER BY id DESC LIMIT 1")
//    DecisionTree selectByTreeId(Long id);

//    @Select("SELECT * FROM decision_tree WHERE user_id = #{userId}")
//    List<DecisionTree> selectByUserId(Long userId);
}
