package me.itisyedog.arcaneannouncer.bukkit.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.earth2me.essentials.Essentials;
import me.itisyedog.arcaneannouncer.bukkit.ArcaneAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PluginUtils {

    private static final ArcaneAnnouncer PLUGIN = JavaPlugin.getPlugin(ArcaneAnnouncer.class);

    public static boolean hasPermission(Player player, String perm) {
        if (perm.isEmpty()) {
            return false;
        }

        if (PLUGIN.isPluginEnabled("PermissionsEx")) {
            try {
                return PermissionsEx.getPermissionManager().has(player, perm);
            } catch (Exception e) {
                // No need Pex2 implementation as it supports default checks
            }
        }

        return player.isPermissionSet(perm) && player.hasPermission(perm);
    }

    public static boolean isAfk(Player p) {
        if (PLUGIN.isPluginEnabled("Essentials")) {
            return JavaPlugin.getPlugin(Essentials.class).getUser(p).isAfk();
        }

        if (PLUGIN.isPluginEnabled("CMI")) {
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(p);
            return user != null && user.isAfk();
        }

        return false;
    }
}
