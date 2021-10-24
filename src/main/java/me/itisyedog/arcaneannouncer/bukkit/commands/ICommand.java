package me.itisyedog.arcaneannouncer.bukkit.commands;

import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface ICommand {

    boolean run(final ArcaneAnnouncer plugin, final CommandSender sender, final Command cmd, final String label, final String[] args);
}
