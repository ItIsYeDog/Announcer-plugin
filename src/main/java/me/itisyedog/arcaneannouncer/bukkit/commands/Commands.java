package me.itisyedog.arcaneannouncer.bukkit.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import me.itisyedog.arcaneannouncer.bukkit.Perm;
import me.itisyedog.arcaneannouncer.bukkit.utils.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.reflect.TypeToken;

import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.colorMsg;
import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.sendMsg;

public class Commands implements CommandExecutor, TabCompleter {
    private ArcaneAnnouncer plugin;

    public static final Map<UUID, Boolean> ENABLED = new HashMap<>();

    private final Set<ICommand> cmds = new HashSet<>();

    public Commands(ArcaneAnnouncer plugin) {
        this.plugin = plugin;

        for (String s : Arrays.asList("help", "reload", "toggle", "list", "add", "remove", "clearall", "restricted",
                "forcesend")) {
            try {
                Class<?> c = null;
                try {
                    c = ArcaneAnnouncer.class.getClassLoader()
                            .loadClass("me.itisyedog.arcaneannouncer.bukkit.commands.list." + s);
                } catch (ClassNotFoundException e) {
                }

                if (c == null) {
                    continue;
                }

                if (Util.getCurrentVersion() >= 9) {
                    cmds.add((ICommand) c.getDeclaredConstructor().newInstance());
                } else {
                    cmds.add((ICommand) c.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("serial")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            sendMsg(sender, colorMsg("&3&lArcane&a&lAnnouncer"));
            sendMsg(sender, colorMsg("&5Version:&a " + plugin.getDescription().getVersion()));
            sendMsg(sender, colorMsg("&5Author, created by:&a ItIsYeDog"));
            sendMsg(sender, colorMsg("&5Commands:&8 /&7" + commandLabel + "&a help"));
            sendMsg(sender, colorMsg(
                    "&4If you find a bug, message ItIsYeDog#0001 on discord!"));
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            if (sender instanceof Player && !sender.hasPermission(Perm.HELP.getPerm())) {
                sendMsg(sender, Util.getMsgProperty("no-permission", "%perm%", Perm.HELP.getPerm()));
                return false;
            }

            Util.getMsgProperty(new TypeToken<List<String>>() {}
                    .getSubtype(List.class), "chat-messages", "%command%", commandLabel).forEach(s -> sendMsg(sender, s));
            return true;
        }

        boolean found = false;
        for (ICommand command : cmds) {
            CommandProcessor proc = command.getClass().getAnnotation(CommandProcessor.class);
            if (proc != null && proc.name().equalsIgnoreCase(args[0])) {
                found = true;

                if (proc.playerOnly() && !(sender instanceof Player)) {
                    sendMsg(sender, colorMsg("&cThis command can only be performed by player."));
                    return false;
                }

                if (sender instanceof Player && !sender.hasPermission(proc.permission().getPerm())) {
                    sendMsg(sender, Util.getMsgProperty("no-permission", "%perm%", proc.permission().getPerm()));
                    return false;
                }

                command.run(plugin, sender, cmd, commandLabel, args);
                break;
            }
        }

        if (!found) {
            sendMsg(sender, Util.getMsgProperty("unknown-sub-command", "%subcmd%", args[0]));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> cmds = new ArrayList<>();

        switch (args.length) {
            case 1:
                cmds.addAll(getCmds(sender));
                break;
            case 2:
                if (args[0].equalsIgnoreCase("restricted")) {
                    cmds.addAll(Arrays.asList("add", "remove", "list"));
                }

                break;
            case 3:
                if (args[1].equalsIgnoreCase("remove")) {
                    cmds.addAll(plugin.getConf().getRestrictConfig().getStringList("restricted-players"));
                }

                break;
            default:
                break;
        }

        return cmds.isEmpty() ? null : cmds;
    }

    private Set<String> getCmds(CommandSender sender) {
        // Try to avoid using stream for tab-complete
        Set<String> c = new HashSet<>();

        for (ICommand cmd : cmds) {
            if (cmd.getClass().isAnnotationPresent(CommandProcessor.class)) {
                CommandProcessor proc = cmd.getClass().getAnnotation(CommandProcessor.class);
                if (!(sender instanceof Player) || sender.hasPermission(proc.permission().getPerm())) {
                    c.add(proc.name());
                }
            }
        }

        return c;
    }
}
