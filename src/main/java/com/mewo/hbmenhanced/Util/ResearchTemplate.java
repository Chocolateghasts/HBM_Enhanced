package com.mewo.hbmenhanced.Util;

public class ResearchTemplate {
    public String type;
    public int id;

    public ResearchTemplate(String type, int id) {
        this.type = type;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
