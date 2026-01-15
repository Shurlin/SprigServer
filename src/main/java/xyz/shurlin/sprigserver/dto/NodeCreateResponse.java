package xyz.shurlin.sprigserver.dto;

public class NodeCreateResponse {
    private Long nodeId;
    private String simulation;
    private int depth; // 节点深度

    public NodeCreateResponse(Long nodeId, String simulation, int depth) {
        this.nodeId = nodeId;
        this.simulation = simulation;
        this.depth = depth;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public String getSimulation() {
        return simulation;
    }
}
