package me.itisyedog.arcaneannouncer.bukkit.commands.list;

import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import me.itisyedog.arcaneannouncer.bukkit.commands.ICommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.itisyedog.arcaneannouncer.bukkit.Perm;
import me.itisyedog.arcaneannouncer.bukkit.commands.CommandProcessor;

import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.getMsgProperty;
import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.sendMsg;

@CommandProcessor(name = "forcesend", permission = Perm.FORCESEND, playerOnly = true)
public class forcesend implements ICommand {

    @Override
    public boolean run(ArcaneAnnouncer plugin, CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            ((Player) sender).performCommand("am help");
            return false;
        }

        int index = 0;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
        }

        if (index < 0) {
            sendMsg(sender, getMsgProperty("bad-number"));
            return false;
        }

        if (index > plugin.getFileHandler().getTexts().size() - 1) {
            sendMsg(sender, getMsgProperty("index-start"));
            return false;
        }

        plugin.getFileHandler().getTexts().get(index).sendTo(((Player) sender), true);
        return true;
    }
}
