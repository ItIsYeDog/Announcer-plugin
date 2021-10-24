package me.itisyedog.arcaneannouncer.bukkit.commands.list;

import java.io.IOException;
import java.io.PrintWriter;

import me.itisyedog.arcaneannouncer.bukkit.config.MessageFileHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import me.itisyedog.arcaneannouncer.bukkit.Perm;
import me.itisyedog.arcaneannouncer.bukkit.commands.CommandProcessor;
import me.itisyedog.arcaneannouncer.bukkit.commands.ICommand;

import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.getMsgProperty;
import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.sendMsg;

@CommandProcessor(name = "clearall", permission = Perm.CLEARALL)
public class clearall implements ICommand {
    @Override
    public boolean run(ArcaneAnnouncer plugin, CommandSender sender, Command cmd, String label, String[] args) {
        MessageFileHandler handler = plugin.getFileHandler();

        if (!handler.isFileExists() || handler.getTexts().isEmpty()) {
            sendMsg(sender, getMsgProperty("no-messages-in-file"));
            return false;
        }

        handler.getTexts().clear();

        try {
            if (handler.isYaml()) {
                handler.getFileConfig().set("messages", null);
                handler.getFileConfig().save(handler.getFile());
            } else {
                PrintWriter writer = new PrintWriter(handler.getFile());
                writer.print("");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMsg(sender, getMsgProperty("all-messages-cleared"));
        return true;
    }
}
