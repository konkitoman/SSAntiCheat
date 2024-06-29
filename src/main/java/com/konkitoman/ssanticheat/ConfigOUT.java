package com.konkitoman.ssanticheat;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConfigOUT {
    public Map<String, Object> map = new TreeMap<>();

    public void writeInt(String name, int value) {
        map.put(name, value);
    }

    public void writeBool(String name, boolean value) {
        map.put(name, value);
    }

    public void writeString(String name, String value) {
        map.put(name, value);
    }

    public void writeConfig(String name, ConfigOUT value) {
        map.put(name, value.map);
    }

    public void writeStringList(String name, List<String> value) {
        map.put(name, value);
    }
}
