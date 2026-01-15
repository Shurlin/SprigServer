package xyz.shurlin.sprigserver.dto;

import xyz.shurlin.sprigserver.entity.DecisionNode;

import java.util.List;
import java.util.Map;

public class DecisionTreesResponse {
    private Map<Long, String> trees;

    public DecisionTreesResponse(Map<Long, String> trees) {
        this.trees = trees;
    }

    public Map<Long, String> getTrees() {
        return trees;
    }

}
