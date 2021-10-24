package me.itisyedog.arcaneannouncer.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import me.itisyedog.arcaneannouncer.bukkit.announce.Announce;
import me.itisyedog.arcaneannouncer.bukkit.commands.Commands;
import me.itisyedog.arcaneannouncer.bukkit.config.ConfigConstants;
import me.itisyedog.arcaneannouncer.bukkit.config.MessageFileHandler;
import me.itisyedog.arcaneannouncer.bukkit.utils.stuff.Complement;
import me.itisyedog.arcaneannouncer.bukkit.config.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.permission.Permission;

import static me.itisyedog.arcaneannouncer.bukkit.utils.Util.logConsole;

public final class ArcaneAnnouncer extends JavaPlugin implements Listener {
    private Configuration conf;
    private Announce announce;
    private MessageFileHandler fileHandler;
    private Permission perm;
    private Complement complement;

    private boolean isPaper = false, isSpigot = false;

    @Override
    public void onEnable() {
        long load = System.currentTimeMillis();

        try {

            verifyServerSoftware();
            startUp();

            if (ConfigConstants.isPlaceholderapi() && isPluginEnabled("PlaceholderAPI")) {
                logConsole("Hooked PlaceholderAPI version: "
                        + me.clip.placeholderapi.PlaceholderAPIPlugin.getInstance().getDescription().getVersion());
            }

            setupVaultPerm();

            Optional.ofNullable(getCommand("arcaneannouncer")).ifPresent(cmd -> {
                Commands cmds = new Commands(this);
                cmd.setExecutor(cmds);
                cmd.setTabCompleter(cmds);
            });

            loadToggledMessages();
            announce.beginScheduling();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        announce.cancelSchedulers();
        saveToggledMessages();
        conf.removeUnnededFiles();

        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public FileConfiguration getConfig() {
        return conf.getConfig();
    }

    @Override
    public void saveConfig() {
    }

    private void verifyServerSoftware() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            isSpigot = true;
        } catch (ClassNotFoundException e) {
            isSpigot = false;
        }

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            isPaper = true;
        } catch (ClassNotFoundException e) {
            isPaper = false;
        }
    }

    private void startUp() {
        conf = new Configuration(this);
        conf.loadConfigs();

        fileHandler = new MessageFileHandler(this);
        fileHandler.loadMessages();

        announce = new Announce();
    }

    public void reload() {
        conf.loadConfigs();

        if (fileHandler == null) {
            fileHandler = new MessageFileHandler(this);
        }

        fileHandler.loadFile();
        fileHandler.loadMessages();

        announce.cancelSchedulers();
        (announce = new Announce()).beginScheduling();
    }

    private boolean setupVaultPerm() {
        if (!isPluginEnabled("Vault")) {
            return false;
        }

        org.bukkit.plugin.RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager()
                .getRegistration(Permission.class);
        return rsp != null && (perm = rsp.getProvider()) != null;
    }

    private void loadToggledMessages() {
        if (!ConfigConstants.isRememberToggleToFile()) {
            return;
        }

        Commands.ENABLED.clear();

        File f = new File(getFolder(), "toggledmessages.yml");
        if (!f.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        if (!config.isConfigurationSection("players")) {
            return;
        }

        for (String uuid : config.getConfigurationSection("players").getKeys(false)) {
            Commands.ENABLED.put(UUID.fromString(uuid), config.getConfigurationSection("players").getBoolean(uuid));
        }

        config.set("players", null);
        try {
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToggledMessages() {
        File f = new File(getFolder(), "toggledmessages.yml");

        if (!ConfigConstants.isRememberToggleToFile() || Commands.ENABLED.isEmpty()) {
            if (f.exists()) {
                f.delete();
            }

            return;
        }

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        config.set("players", null);

        for (Entry<UUID, Boolean> list : Commands.ENABLED.entrySet()) {
            if (!list.getValue()) {
                config.set("players." + list.getKey(), list.getValue());
            }
        }

        try {
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Commands.ENABLED.clear();
    }

    public File getFolder() {
        File folder = getDataFolder();
        folder.mkdirs();
        return folder;
    }

    public boolean isPluginEnabled(String name) {
        return getServer().getPluginManager().getPlugin(name) != null
                && getServer().getPluginManager().isPluginEnabled(name);
    }

    public Configuration getConf() {
        return conf;
    }

    public Announce getAnnounce() {
        return announce;
    }

    public MessageFileHandler getFileHandler() {
        return fileHandler;
    }

    public Permission getVaultPerm() {
        return perm;
    }

    public boolean isSpigot() {
        return isSpigot;
    }

    public boolean isPaper() {
        return isPaper;
    }

    public Complement getComplement() {
        return complement;
    }
}
