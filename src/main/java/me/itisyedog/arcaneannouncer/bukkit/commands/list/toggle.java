package me.itisyedog.arcaneannouncer.bukkit.commands.list;

import java.util.UUID;

import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import me.itisyedog.arcaneannouncer.bukkit.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.itisyedog.arcaneannouncer.bukkit.Perm;
import me.itisyedog.arcaneannouncer.bukkit.commands.CommandProcessor;
import me.itisyedog.arcaneannouncer.bukkit.commands.ICommand;

import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.getMsgProperty;
import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.sendMsg;

@CommandProcessor(name = "toggle", permission = Perm.TOGGLE)
public class toggle implements ICommand {
    @Override
    public boolean run(ArcaneAnnouncer plugin, CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            sendMsg(sender, getMsgProperty("toggle.console-usage", "%command%", label));
            return false;
        }

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("all")) {
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    toggleMsg(pl.getUniqueId());
                }

                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sendMsg(sender, getMsgProperty("toggle.player-not-found", "%target%", args[1]));
                return false;
            }

            sendMsg(sender, getMsgProperty("toggle." + (toggleMsg(target.getUniqueId()) ? "enabled" : "disabled")));
            return true;
        }

        sendMsg(sender,
                getMsgProperty("toggle." + (toggleMsg(((Player) sender).getUniqueId()) ? "enabled" : "disabled")));
        return true;
    }

    private boolean toggleMsg(UUID uuid) {
        if (!Commands.ENABLED.containsKey(uuid)) {
            Commands.ENABLED.put(uuid, true);
            return false;
        }

        Commands.ENABLED.remove(uuid);
        return true;
    }
}
