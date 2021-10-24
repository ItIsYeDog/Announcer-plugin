package me.itisyedog.arcaneannouncer.bukkit.commands.list;

import me.itisyedog.arcaneannouncer.bukkit.Perm;
import me.itisyedog.arcaneannouncer.bukkit.commands.CommandProcessor;
import me.itisyedog.arcaneannouncer.bukkit.utils.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import me.itisyedog.arcaneannouncer.bukkit.commands.ICommand;

@CommandProcessor(name = "reload", permission = Perm.RELOAD)
public class reload implements ICommand {
    @Override
    public boolean run(ArcaneAnnouncer plugin, CommandSender sender, Command cmd, String label, String[] args) {
        plugin.reload();
        Util.sendMsg(sender, Util.getMsgProperty("reload-config"));
        return true;
    }
}
