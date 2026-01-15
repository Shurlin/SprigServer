package xyz.shurlin.sprigserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.shurlin.sprigserver.entity.DecisionNode;
import xyz.shurlin.sprigserver.entity.DecisionTree;
import xyz.shurlin.sprigserver.entity.Users;
import xyz.shurlin.sprigserver.mapper.DecisionNodeMapper;
import xyz.shurlin.sprigserver.mapper.DecisionTreeMapper;
import xyz.shurlin.sprigserver.mapper.UsersMapper;
import xyz.shurlin.sprigserver.service.DecisionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DecisionServiceImpl implements DecisionService {

    private static final Logger logger = LoggerFactory.getLogger(DecisionServiceImpl.class);

    @Value("${qwen.model}")
    private String model;

    private final DecisionTreeMapper treeMapper;
    private final DecisionNodeMapper nodeMapper;
    private final UsersMapper usersMapper;
    private final AiService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DecisionServiceImpl(DecisionTreeMapper treeMapper, DecisionNodeMapper nodeMapper, UsersMapper usersMapper, AiService aiService) {
        this.treeMapper = treeMapper;
        this.nodeMapper = nodeMapper;
        this.usersMapper = usersMapper;
        this.aiService = aiService;
    }

    // ================== Tree ==================

    @Override
    public DecisionTree createTree(String username, String background) throws IOException {
        logger.info("Create tree request: username={}, background.length={}", username, background == null ? 0 : background.length());

        Users user = usersMapper.selectOne(new QueryWrapper<Users>().eq("username", username));

        if (user == null) {
            logger.warn("Create tree failed: user not found, username={}", username);
            throw new IllegalArgumentException("User not found: " + username);
        }

        logger.debug("Calling AI to generate tree title");

        String title = aiService.chat("请为以下背景信息生成一个8个字以内的标题，概况需要决策的主题，作为人生决策树的名称：" + background);

        DecisionTree tree = new DecisionTree();
        tree.setUserId(user.getId());
        tree.setTitle(title);
        tree.setBackground(background);

        long id = treeMapper.insert(tree);


        logger.info("Tree created: treeId={}, userId={}, title={}", id, user.getId(), title);

        return tree;
    }

//    @Override
//    public List<DecisionTree> listTrees(long userId) {
//        logger.debug("List trees for userId={}", userId);
//        return treeMapper.selectByUserId(userId);
//    }

    // ================== Node ==================

    private DecisionNode createNode(Long treeId, Long parentNodeId, String decisionText, String simulation, int depth) {

        DecisionNode node = new DecisionNode();
        node.setTreeId(treeId);
        node.setParentNodeId(parentNodeId);
        node.setDecisionText(decisionText);
        node.setSimulation(simulation);
        node.setDepth(depth);

        long nodeId = nodeMapper.insert(node);
        node.setNodeId(nodeId);

        logger.info("Node created: nodeId={}, treeId={}, parentNodeId={}, depth={}", node.getNodeId(), treeId, parentNodeId, depth);

        return node;
    }

//    @Override
//    public List<DecisionNode> getTreeNodes(Long treeId) {
//        logger.debug("Get nodes for treeId={}", treeId);
//        return nodeMapper.selectByTreeId(treeId);
//    }

    /**
     * 从当前节点一路回溯到 root
     */
    public List<DecisionNode> getPathNodes(Long currentNodeId) {
        logger.debug("Build path nodes: currentNodeId={}", currentNodeId);

        List<DecisionNode> path = new ArrayList<>();
        DecisionNode cur = nodeMapper.selectOne(new QueryWrapper<DecisionNode>().eq("id", currentNodeId));

        if (cur == null) {
            logger.warn("Path build aborted: node not found, nodeId={}", currentNodeId);
            return path;
        }

        while (cur != null) {
            path.add(cur);
            logger.info("Path node added: nodeId={}, parentNodeId={}", cur.getNodeId(), cur.getParentNodeId());
            if (cur.getParentNodeId() == null || cur.getParentNodeId() == -1) break;
            cur = nodeMapper.selectOne(new QueryWrapper<DecisionNode>().eq("id", currentNodeId));
        }

        Collections.reverse(path);

        logger.debug("Path built: length={}", path.size());
        return path;
    }

    // ================== AI ==================

    public ObjectNode buildMessages(DecisionTree tree, List<DecisionNode> pathNodes, String newDecisionText) {

        logger.debug("Build LLM messages: treeId={}, pathSize={}, newDecision.length={}", tree.getTreeId(), pathNodes == null ? 0 : pathNodes.size(), newDecisionText == null ? 0 : newDecisionText.length());

        ObjectNode json = objectMapper.createObjectNode();
        json.put("model", model);
        json.put("temperature", 1.3);
        ArrayNode messages = json.putArray("messages");

        messages.add(msg("system", "你是一个“人生模拟器”。用户交代自己背景后会假设自己的选择或境遇，你需基于已有背景和历史选择或境遇及其模拟内容，模拟其后续发展。只需输出模拟内容，不要输出任何多余文字，不要有多余换行，文本在500字以内。"));

        messages.add(msg("user", "【背景】" + tree.getBackground()));

        for (DecisionNode node : pathNodes) {
            messages.add(msg("user", "【决策/际遇】" + node.getDecisionText()));
            messages.add(msg("assistant", node.getSimulation()));
        }

        messages.add(msg("user", "【新的决策/际遇】" + newDecisionText));

        logger.debug("LLM messages built: totalMessages={}", messages.size());

        return json;
    }

    private ObjectNode msg(String role, String content) {
        ObjectNode msg = objectMapper.createObjectNode();
        msg.put("role", role);
        msg.put("content", content);
        return msg;
    }

    /**
     * 在某个节点下扩展新决策
     */
    @Override
    public DecisionNode expandNode(Long treeId, Long parentNodeId, String decisionText) throws IOException {

        logger.info("Expand node request: treeId={}, parentNodeId={}, decision.length={}", treeId, parentNodeId, decisionText == null ? 0 : decisionText.length());

        DecisionTree tree = treeMapper.selectOne(new QueryWrapper<DecisionTree>().eq("id", treeId));
        if (tree == null) {
            logger.error("Expand node failed: tree not found, treeId={}", treeId);
            throw new IllegalArgumentException("Tree not found: " + treeId);
        }

        List<DecisionNode> path;
        int depth;
        logger.info("Building path nodes...");

        if (parentNodeId == -1 || parentNodeId == null) {
            path = List.of();
            depth = 1;
        }else {
            path = getPathNodes(parentNodeId);
            depth = path.size() + 1;
        }

        ObjectNode json = buildMessages(tree, path, decisionText);

        logger.info("Calling AI generate simulation...");
        String simulation = aiService.generate(json);
        logger.info("AI simulation generated, length={}", simulation == null ? 0 : simulation.length());

        return createNode(treeId, parentNodeId, decisionText, simulation, depth);
    }
}

