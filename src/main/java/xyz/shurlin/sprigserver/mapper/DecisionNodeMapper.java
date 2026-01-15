package xyz.shurlin.sprigserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.shurlin.sprigserver.entity.DecisionNode;

import java.util.List;

@Mapper
public interface DecisionNodeMapper extends BaseMapper<DecisionNode> {

//    @Select("SELECT * FROM decision_node WHERE id = #{id}")
//    DecisionNode selectById(Long id);
//
//    @Select("SELECT * FROM decision_node WHERE tree_id = #{treeId}")
//    List<DecisionNode> selectByTreeId(Long treeId);
//
//    @Select("SELECT * FROM decision_node WHERE parent_node_id = #{parentNodeId}")
//    List<DecisionNode> selectByParentId(Long parentNodeId);
}
