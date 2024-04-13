package com.simpleplugins.simplefly.commands;

import com.simpleplugins.simplefly.SimpleFly;
import com.simpleplugins.simplefly.config.MessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FlyCommand extends BukkitCommand {
    private final ConfigurationSection section;

    public FlyCommand(@NotNull String name, ConfigurationSection section) {
        super(name);

        this.section = section;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        final MessagesConfig messages = SimpleFly.getInstance().getMessagesConfig();

        final String permission = section.getString("permission");
        if(permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(messages.getMessage("no_permission"));
            return true;
        }

        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(messages.getMessage("no_console"));
                return true;
            }

            if(((Player) sender).getAllowFlight()) {
                ((Player) sender).setAllowFlight(false);
                sender.sendMessage(messages.getMessage("flight_disabled"));
            } else {
                ((Player) sender).setAllowFlight(true);
                sender.sendMessage(messages.getMessage("flight_enabled"));
            }

            return true;
        }

        final String permissionOther = section.getString("permission-other");
        if(permissionOther != null && !sender.hasPermission(permissionOther)) {
            sender.sendMessage(messages.getMessage("no_permission"));
            return true;
        }

        final Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage(messages.getMessage("player_not_found"));
            return true;
        }

        if(player.getAllowFlight()) {
            player.setAllowFlight(false);
            sender.sendMessage(messages.getMessage("flight_disabled_other")
                    .replace("{Player}",player.getName()));
        } else {
            player.setAllowFlight(true);
            sender.sendMessage(messages.getMessage("flight_enabled_other")
                    .replace("{Player}",player.getName()));
        }

        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        final String permissionOther = section.getString("permission-other");

        if(args.length == 1 && permissionOther != null && sender.hasPermission(permissionOther))
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());

        return Collections.emptyList();
    }
}