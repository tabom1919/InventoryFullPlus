package me.lewis.inventoryfull.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.lewis.inventoryfull.InventoryFullPlus;

public class DataManager {

    private InventoryFullPlus plugin;

    private File fileData;
    private FileConfiguration fileConfigData;

    private Map<String, Boolean> players = new HashMap<String, Boolean>();

    public DataManager(InventoryFullPlus plugin) {
        this.plugin = plugin;
        registerFile();
    }

    private void registerFile() {
        fileData = new File(plugin.getDataFolder(), "data.yml");
        fileConfigData = YamlConfiguration.loadConfiguration(fileData);

        saveDataFile();

        if (fileConfigData.contains("Users")) {
            for (String uuid : fileConfigData.getConfigurationSection("Users").getKeys(false)) {
                players.put(uuid, fileConfigData.getBoolean("Users." + uuid + ".enabled", true));
            }
        }
    }

    private void saveDataFile() {
        try {
            fileConfigData.save(fileData);
        } catch (IOException localIOException) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
        }
    }

    public void reloadFile() {
        fileConfigData = YamlConfiguration.loadConfiguration(fileData);
        saveDataFile();
    }

    public void onDisable() {
        for (Map.Entry<String, Boolean> entry : players.entrySet()) {
            String uuid = entry.getKey();
            Boolean value = entry.getValue();

            fileConfigData.set("Users." + uuid + ".enabled", value);
        }
        saveDataFile();
    }

    private boolean getPlayer(UUID uuid) {
        return players.containsKey(uuid.toString());
    }

    public boolean hasAlerts(UUID uuid) {
        if (!getPlayer(uuid)) {
            players.put(uuid.toString(), false);
            return false;
        }
        return players.get(uuid.toString());
    }

    public void setAlerts(UUID uuid, boolean value) {
        players.put(uuid.toString(), value);
    }

}
