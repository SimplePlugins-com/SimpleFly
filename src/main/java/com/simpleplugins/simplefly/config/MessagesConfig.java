package com.simpleplugins.simplefly.config;

import com.google.gson.GsonBuilder;
import com.simpleplugins.simplefly.SimpleFly;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MessagesConfig {
    private final Map<String,Object> values = new HashMap<>();
    private final Path path;

    public MessagesConfig() {
        path = SimpleFly.getInstance().getDataFolder().toPath().resolve("messages.json");
    }

    public String getMessage(String path) {
        return ((String) values.get(path)).replace("{Prefix}",(String) values    .get("prefix"));
    }

    public void reload() {
        if(Files.notExists(path))
            SimpleFly.getInstance().saveResource(path.getFileName().toString(),false);

        try {
            values.putAll(new GsonBuilder().setPrettyPrinting().create().fromJson(Files.newBufferedReader(path), values.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
