package xyz.shurlin.sprigserver.service;

import xyz.shurlin.sprigserver.entity.DecisionNode;
import xyz.shurlin.sprigserver.entity.DecisionTree;

import java.io.IOException;
import java.util.List;

public interface DecisionService {
    DecisionTree createTree(String username, String background) throws IOException;

//    List<DecisionTree> listTrees(long userId);

//    DecisionNode createNode(Long treeId, Long parentNodeId, String decisionText, String simulation, int depth);

//    List<DecisionNode> getTreeNodes(Long treeId);

    public List<DecisionNode> getPathNodes(Long currentNodeId);

    DecisionNode expandNode(Long treeId, Long parentNodeId, String decisionText) throws IOException;
}
