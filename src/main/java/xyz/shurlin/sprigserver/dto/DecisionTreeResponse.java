package xyz.shurlin.sprigserver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import xyz.shurlin.sprigserver.entity.DecisionNode;

import java.util.List;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class DecisionTreeResponse {
    private Long treeId;
    private String title;
    private String background;
    private List<DecisionNode> nodes;

    public DecisionTreeResponse() {
    }

    public void setTreeId(Long treeId) {
        this.treeId = treeId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public void setNodes(List<DecisionNode> nodes) {
        this.nodes = nodes;
    }

    public Long getTreeId() {
        return treeId;
    }

    public String getTitle() {
        return title;
    }

    public String getBackground() {
        return background;
    }

    public List<DecisionNode> getNodes() {
        return nodes;
    }
}
