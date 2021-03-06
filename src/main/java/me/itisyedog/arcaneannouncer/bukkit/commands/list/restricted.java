package me.itisyedog.arcaneannouncer.bukkit.commands.list;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.reflect.TypeToken;
import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import me.itisyedog.arcaneannouncer.bukkit.commands.ICommand;

import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.*;

public class restricted implements ICommand {
    private enum Actions {
        ADD, REMOVE, LIST;
    }

    @SuppressWarnings("serial")
    @Override
    public boolean run(ArcaneAnnouncer plugin, CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            if (sender instanceof Player) {
                ((Player) sender).performCommand("am help");
            } else {
                Bukkit.dispatchCommand(sender, "am help");
            }

            return false;
        }

        final FileConfiguration file = plugin.getConf().getRestrictConfig();
        final List<String> restricted = file.getStringList("restricted-players");

        Actions action = Actions.ADD;

        switch (args[1].toLowerCase()) {
            case "add":
                action = Actions.ADD;
                break;
            case "remove":
                action = Actions.REMOVE;
                break;
            case "list":
                action = Actions.LIST;
                break;
            default:
                break;
        }

        boolean fileChanged = false;

        switch (action) {
            case ADD:
                if (args.length < 3) {
                    if (sender instanceof Player) {
                        ((Player) sender).performCommand("am help");
                    } else {
                        Bukkit.dispatchCommand(sender, "am help");
                    }

                    return false;
                }

                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sendMsg(sender, getMsgProperty("restricted.player-not-found", "%player%", args[2]));
                    return false;
                }

                String name = target.getName();
                if (restricted.contains(name)) {
                    sendMsg(sender, getMsgProperty("restricted.player-already-added", "%player%", name));
                    return false;
                }

                fileChanged = restricted.add(name);
                sendMsg(sender, getMsgProperty("restricted.success-add", "%player%", name));
                break;
            case REMOVE:
                if (args.length < 3) {
                    if (sender instanceof Player) {
                        ((Player) sender).performCommand("am help");
                    } else {
                        Bukkit.dispatchCommand(sender, "am help");
                    }

                    return false;
                }

                String pName = args[2];
                if (!restricted.contains(pName)) {
                    sendMsg(sender, getMsgProperty("restricted.player-already-removed", "%player%", pName));
                    return false;
                }

                fileChanged = restricted.remove(pName);
                sendMsg(sender, getMsgProperty("restricted.success-remove", "%player%", pName));
                break;
            case LIST:
                if (restricted.isEmpty()) {
                    sendMsg(sender, getMsgProperty("restricted.no-player-added"));
                    return false;
                }

                Collections.sort(restricted);

                String msg = "";
                for (String fpl : restricted) {
                    if (!msg.isEmpty()) {
                        msg += "&r, ";
                    }

                    msg += fpl;
                }

                for (String bp : getMsgProperty(new TypeToken<List<String>>() {}.getSubtype(List.class), "restricted.list")) {
                    sendMsg(sender, colorMsg(bp.replace("%players%", msg)));
                }

                break;
            default:
                break;
        }

        if (fileChanged) {
            file.set("restricted-players", restricted);

            try {
                file.save(plugin.getConf().getRestrictFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
