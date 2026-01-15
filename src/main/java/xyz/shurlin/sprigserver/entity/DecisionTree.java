package xyz.shurlin.sprigserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

public class DecisionTree {
    @TableField("id")
    private Long treeId;

    private Long userId;
    private String title;          // 决策主题
    private String background;     // 初始背景信息（可不断补充后的最终版）
    private Date createTime;

    public DecisionTree() {
    }

    public void setTreeId(Long treeId) {
        this.treeId = treeId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Long getTreeId() {
        return treeId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getBackground() {
        return background;
    }
}
