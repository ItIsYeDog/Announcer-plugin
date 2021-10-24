package me.itisyedog.arcaneannouncer.bukkit.announce;

import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import me.itisyedog.arcaneannouncer.bukkit.announce.message.Message;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public interface BaseAnnounce {

    default List<Message> getMessageList() {
        return JavaPlugin.getPlugin(ArcaneAnnouncer.class).getFileHandler().getTexts();
    }
}
