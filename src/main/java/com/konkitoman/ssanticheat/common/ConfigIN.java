package com.konkitoman.ssanticheat.common;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfigIN {
    Map<String, Object> map;

    public ConfigIN(Map<String, Object> map) {
        this.map = map;
    }

    private Optional<Object> readObject(String name) {
        if (!map.containsKey(name)) {
            SSAntiCheat.LOGGER.info("Cannot find: {}", name);
            return Optional.empty();
        }
        return Optional.of(map.get(name));
    }

    public Optional<Integer> readInt(String name) {
        Optional<Object> value = readObject(name);
        if (value.isEmpty()) {
            return Optional.empty();
        }

        if (value.get() instanceof Integer) {
            return Optional.of((Integer) value.get());
        }

        SSAntiCheat.LOGGER.info("Cannot find: {} of type int", name);

        return Optional.empty();
    }

    public Optional<Boolean> readBool(String name) {
        Optional<Object> value = readObject(name);
        if (value.isEmpty()) {
            return Optional.empty();
        }

        if (value.get() instanceof Boolean) {
            return Optional.of((Boolean) value.get());
        }

        SSAntiCheat.LOGGER.info("Cannot find: {} of type bool", name);

        return Optional.empty();
    }

    public Optional<String> readString(String name) {
        Optional<Object> value = readObject(name);
        if (value.isEmpty()) {
            return Optional.empty();
        }

        if (value.get() instanceof String) {
            return Optional.of((String) value.get());
        }

        SSAntiCheat.LOGGER.info("Cannot find: {} of type String", name);

        return Optional.empty();
    }

    public Optional<Map<?, ?>> readMap(String name) {
        Optional<Object> value = readObject(name);
        if (value.isEmpty()) {
            return Optional.empty();
        }

        if (value.get() instanceof Map<?, ?>) {
            return Optional.of((Map<?, ?>) value.get());
        }

        SSAntiCheat.LOGGER.info("Cannot find: {} of type Map", name);

        return Optional.empty();
    }

    public Optional<ConfigIN> readConfig(String name) {
        Optional<Object> value = readObject(name);
        if (value.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new ConfigIN((Map<String, Object>) value.get()));
    }

    public Optional<List<String>> readStringList(String name) {
        Optional<Object> value = readObject(name);
        if (value.isEmpty()) {
            return Optional.empty();
        }

        if (value.get() instanceof List<?>) {
            return Optional.of((List<String>) value.get());
        }

        SSAntiCheat.LOGGER.info("Cannot find: {} of type List<String>", name);


        return Optional.empty();
    }

}
