package me.itisyedog.arcaneannouncer.bukkit.commands.list;

import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.itisyedog.arcaneannouncer.bukkit.Perm;
import me.itisyedog.arcaneannouncer.bukkit.commands.CommandProcessor;
import me.itisyedog.arcaneannouncer.bukkit.commands.ICommand;

import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.getMsgProperty;
import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.sendMsg;

@CommandProcessor(name = "remove", permission = Perm.REMOVE)
public class remove implements ICommand {
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

        plugin.getFileHandler().removeText(index);
        sendMsg(sender, getMsgProperty("text-removed", "%index%", index));
        return true;
    }
}
