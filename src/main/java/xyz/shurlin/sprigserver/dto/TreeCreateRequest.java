package xyz.shurlin.sprigserver.dto;

public class TreeCreateRequest {
    private String username;
    private String background;

    public TreeCreateRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
