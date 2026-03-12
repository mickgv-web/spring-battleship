package dev.mg.battleships.dto;

public class GameEvent {

    private String type;

    public GameEvent() {
    }

    public GameEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}