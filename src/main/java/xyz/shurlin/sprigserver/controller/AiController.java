package xyz.shurlin.sprigserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.shurlin.sprigserver.dto.*;
import xyz.shurlin.sprigserver.entity.DecisionNode;
import xyz.shurlin.sprigserver.entity.DecisionTree;
import xyz.shurlin.sprigserver.entity.Users;
import xyz.shurlin.sprigserver.mapper.DecisionNodeMapper;
import xyz.shurlin.sprigserver.mapper.DecisionTreeMapper;
import xyz.shurlin.sprigserver.mapper.UsersMapper;
import xyz.shurlin.sprigserver.service.DecisionService;
import xyz.shurlin.sprigserver.service.impl.AiService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {
    private final AiService aiService;
    private final DecisionTreeMapper treeMapper;
    private final DecisionNodeMapper nodeMapper;
    private final UsersMapper usersMapper;
    private final DecisionService service;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(AiController.class);

    public AiController(AiService aiService, DecisionTreeMapper treeMapper, DecisionNodeMapper nodeMapper, UsersMapper usersMapper, DecisionService service) {
        this.aiService = aiService;
        this.treeMapper = treeMapper;
        this.nodeMapper = nodeMapper;
        this.usersMapper = usersMapper;
        this.service = service;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> req) {
        try {
            String reply = aiService.chat(req.get("message"));
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            logger.error("Qwen chat error: {}", e.getMessage());
            return ResponseEntity.status(500).body("Qwen error");
        }
    }

    @GetMapping("/dec_tree/all")
    public ResponseEntity<DecisionTreesResponse> getAllTree(@RequestParam String username) {
        logger.info("Get all decision trees for user: {}", username);
        Long userId = usersMapper.selectOne(new QueryWrapper<Users>().eq("username", username)).getId();
        List<DecisionTree> trees = treeMapper.selectList(new QueryWrapper<DecisionTree>().eq("user_id", userId));
        logger.info("User {} has {} decision trees", username, trees.size());
        if (trees.isEmpty()) {
            return ResponseEntity.ok(new DecisionTreesResponse(Map.of()));
        }

        return ResponseEntity.ok(new DecisionTreesResponse(
                trees.stream().collect(java.util.stream.Collectors.toMap(DecisionTree::getTreeId, DecisionTree::getTitle))));
    }

    @GetMapping(
            value = "/dec_tree/{treeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DecisionTreeResponse> getTree(@PathVariable("treeId") long treeId) {
        logger.info("Get decision tree details for treeId: {}", treeId);
        DecisionTree tree = treeMapper.selectOne(new QueryWrapper<DecisionTree>().eq("id", treeId));

        if (tree == null) {
            return ResponseEntity.notFound().build();
        }
        List<DecisionNode> nodes = nodeMapper.selectList(new QueryWrapper<DecisionNode>().eq("tree_id", treeId));

        DecisionTreeResponse resp = new DecisionTreeResponse();
        resp.setTreeId(tree.getTreeId());
        logger.info("Decision tree title: {}", tree.getTitle());
        resp.setTitle(tree.getTitle());
        resp.setBackground(tree.getBackground());
        if (nodes.isEmpty()) {
            resp.setNodes(List.of());
            logger.info("Decision tree has no nodes");
            return ResponseEntity.ok(resp);
        }
        resp.setNodes(nodes.stream().map(n -> {
            DecisionNode node = new DecisionNode();
            node.setNodeId(n.getNodeId());
            node.setTreeId(n.getTreeId());

            node.setParentNodeId(n.getParentNodeId());
            node.setDecisionText(n.getDecisionText());
            node.setSimulation(n.getSimulation());
            node.setDepth(n.getDepth());
//            logger.info(n.getSimulation());
            return node;
        }).toList());


        return ResponseEntity.ok(resp);
    }

    @PostMapping("/dec_tree/create_tree")
    public ResponseEntity<?> createTree(@RequestBody TreeCreateRequest req) {
        try {
            DecisionTree tree = service.createTree(req.getUsername(), req.getBackground());
            return ResponseEntity.ok(new TreeCreateResponse(tree.getTreeId(), tree.getTitle()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Create tree error");
        }
    }

    @PostMapping("/dec_tree/create_node")
    public ResponseEntity<?> createNode(@RequestBody NodeCreateRequest req) {
        try {
            DecisionNode node = service.expandNode(req.getTreeId(), req.getParentNodeId(), req.getDecisionText());
            return ResponseEntity.ok(new NodeCreateResponse(node.getNodeId(), node.getSimulation(), node.getDepth()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Create node error");
        }
    }
}
