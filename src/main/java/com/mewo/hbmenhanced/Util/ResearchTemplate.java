package com.mewo.hbmenhanced.Util;

import java.util.Objects;

public class ResearchTemplate {
    public String type;
    public String id;

    public ResearchTemplate(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchTemplate)) return false;
        ResearchTemplate other = (ResearchTemplate) o;
        return Objects.equals(type, other.type) && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }

    @Override
    public String toString() {
        return "ResearchTemplate{" + "type='" + type + '\'' + ", id='" + id + '\'' + '}';
    }
}
