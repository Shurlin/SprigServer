package xyz.shurlin.sprigserver.dto;

public class TreeCreateResponse {
    private Long treeId;
    private String title;

    public TreeCreateResponse(Long treeId, String title) {
        this.treeId = treeId;
        this.title = title;
    }

    public TreeCreateResponse() {
    }

    public Long getTreeId() {
        return treeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTreeId(Long treeId) {
        this.treeId = treeId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
