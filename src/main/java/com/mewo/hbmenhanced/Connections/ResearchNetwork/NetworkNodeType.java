package com.mewo.hbmenhanced.Connections.ResearchNetwork;

public enum NetworkNodeType {
    RESEARCH,
    TERMINAL,
    CONTROLLER;

    public static NetworkNodeType next(NetworkNodeType current) {
        NetworkNodeType[] values = values();
        int nextOrdinal = (current.ordinal() + 1) % values.length;
        return values[nextOrdinal];
    }
}
