package com.simpleplugins.simplefly;

import com.simpleplugins.simplefly.commands.FlyCommand;
import com.simpleplugins.simplefly.config.MessagesConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.lang.reflect.Field;
import java.util.logging.Level;

@Author("SimplePlugins")
@Plugin(name = "SimpleFly", version = "1.0.1")
public class SimpleFly extends JavaPlugin {
    private static SimpleFly instance;

    @Getter
    private MessagesConfig messagesConfig;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        messagesConfig = new MessagesConfig();
        messagesConfig.reload();

        registerCommands();
    }

    private void registerCommands() {
        final ConfigurationSection section = getConfig().getConfigurationSection("command");
        if (section == null) {
            getLogger().log(Level.SEVERE, "Could not found Configuration Section 'command' in config.yml");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        String name = section.getString("name");
        if (name == null) name = "fly";

        final FlyCommand command = new FlyCommand(name, section);
        command.setAliases(section.getStringList("aliases"));

        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);

            final CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer());
            commandMap.register(name, command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            getLogger().log(Level.SEVERE, "Could not register commands on command map");

            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().log(Level.INFO, "registered Command 'fly' successful");
    }

    public static SimpleFly getInstance() {
        return instance;
    }
}