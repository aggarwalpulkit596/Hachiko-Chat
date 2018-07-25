package me.dats.com.datsme.Models;

public class notifications {
    private String type;
    private String from;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String sender) {
        this.from = sender;
    }
}
