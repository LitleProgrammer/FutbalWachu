package com.lubodi.futbollwachu.Manager;

import com.lubodi.futbollwachu.FutballBola;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private static FileConfiguration config;
    private static FutballBola minigame;

    /**
     * Set up the configuration for the Java plugin.
     *
     * @param  plugin  the Java plugin to set up the configuration for
     * @return         void
     */
    public static void setupConfig(JavaPlugin plugin, FutballBola futballBola) {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        minigame = futballBola;
    }

    /**
     * Retrieves the lobby spawn location.
     *
     * @return  the lobby spawn location
     */
    public static Location getLobbySpawn(){
        return new Location(
                Bukkit.getWorld(config.getString("lobby-spawn.world")),
                config.getDouble("lobby-spawn.x"),
                config.getDouble("lobby-spawn.y"),
                config.getDouble("lobby-spawn.z"),
                (float) config.getDouble("lobby-spawn.yaw"),
                (float) config.getDouble("lobby-spawn.pitch"));
    }


    public static int getRequiredPlayers() {
        return config.getInt("required-players");
    }
    public static int getMaxPortero() {
        return config.getInt("max-portero");
    }


    public static int getCountDownSeconds() {
        return config.getInt("countdown-seconds");
    }
    public static int getCountDownGameSeconds() {
        return config.getInt("countdownGame-seconds");
    }
    public static int getCountDownEndSeconds() {
        return config.getInt("countdownEnd-seconds");
    }


    /**
     * Retrieves the location from the given path in the configuration file.
     *
     * @param  path  the path in the configuration file
     * @return       the location retrieved from the configuration
     */
    private static Location getLocation(String path) {
      return new Location(
              Bukkit.getWorld(config.getString(path + ".world")),
              config.getDouble(path + ".x"),
              config.getDouble(path + ".y"),
              config.getDouble(path + ".z"),
              (float) config.getDouble(path + ".yaw"),
              (float) config.getDouble(path + ".pitch")
      );
    }

    public static void setLocation(String path, Location location) {
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pith", location.getPitch());
        minigame.saveConfig();
    }

    public static void setRegion(String path, Region region) {
        config.set(path + ".world", region.getCorner1().getWorld().getName());
        config.set(path + ".x", region.getCorner1().getX());
        config.set(path + ".y", region.getCorner1().getY());
        config.set(path + ".z", region.getCorner1().getZ());
        config.set(path + ".yaw", region.getCorner1().getYaw());
        config.set(path + ".pith", region.getCorner1().getPitch());

        config.set(path + ".x2", region.getCorner2().getX());
        config.set(path + ".y2", region.getCorner2().getY());
        config.set(path + ".z2", region.getCorner2().getZ());
        config.set(path + ".yaw2", region.getCorner2().getYaw());
        config.set(path + ".pith2", region.getCorner2().getPitch());
        minigame.saveConfig();
    }
}