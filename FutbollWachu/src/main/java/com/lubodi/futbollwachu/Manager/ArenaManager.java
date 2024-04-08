package com.lubodi.futbollwachu.Manager;



import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaManager {
    private static List<Arena> arenas = new ArrayList<>();


    public ArenaManager(FutballBola minigame){
        FileConfiguration config = minigame.getConfig();
        for (String str: config.getConfigurationSection("arenas.").getKeys(false)){
            String arenaName = config.getString("arenas." + str + ".name");
            String worldName = config.getString("arenas." + str + ".world");
            World world = Bukkit.getWorld(worldName);
            ConcurrentHashMap<Team, Region> zones = new  ConcurrentHashMap<>();
            ConcurrentHashMap<Team, Region> portero = new ConcurrentHashMap<>();
            ConcurrentHashMap<Team, Region> canchas = new ConcurrentHashMap<>();
            for(String team : config.getConfigurationSection("arenas." + str + ".teams").getKeys(false)){
                zones.put(Team.valueOf(team.toUpperCase()), new Region(
                        new Location(
                                world,
                                config.getDouble("arenas." + str + ".teams." + team + ".zona.x"),
                                config.getDouble("arenas." + str +".teams." + team + ".zona.y"),
                                config.getDouble("arenas." + str + ".teams." + team + ".zona.z"),
                                (float) config.getDouble("arenas." + str + ".teams." + team + ".zona.yaw"),
                                (float) config.getDouble("arenas." + str + ".teams." + team + ".zona.pitch")
                        ),
                        new Location(
                                world,
                                config.getDouble("arenas." + str + ".teams." + team + ".zona.x2"),
                                config.getDouble("arenas." + str +".teams." + team + ".zona.y2"),
                                config.getDouble("arenas." + str+ ".teams." + team+ ".zona.z2"),
                                (float)config.getDouble("arenas."+str+".teams."+team+".zona.yaw2"),
                                (float)config.getDouble("arenas."+str+".teams."+team+".zona.pitch2")
                        )
                ));
                portero.put(Team.valueOf(team.toUpperCase()), new Region(
                        new Location(
                                world,
                                config.getDouble("arenas." + str + ".teams." + team + ".portero.x"),
                                config.getDouble("arenas." + str +".teams." + team + ".portero.y"),
                                config.getDouble("arenas." + str + ".teams." + team + ".portero.z"),
                                (float) config.getDouble("arenas." + str + ".teams." + team + ".portero.yaw"),
                                (float) config.getDouble("arenas." + str + ".teams." + team + ".portero.pitch")
                        ),
                        new Location(
                                world,
                                config.getDouble("arenas." + str + ".teams." + team + ".portero.x2"),
                                config.getDouble("arenas." + str +".teams." + team + ".portero.y2"),
                                config.getDouble("arenas." + str+ ".teams." + team+ ".portero.z2"),
                                (float)config.getDouble("arenas."+str+".teams."+team+".portero.yaw2"),
                                (float)config.getDouble("arenas."+str+".teams."+team+".portero.pitch2")
                        )
                ));
                canchas.put(Team.valueOf(team.toUpperCase()), new Region(
                        new Location(
                                world,
                                config.getDouble("arenas." + str + ".teams." + team + ".cancha.x"),
                                config.getDouble("arenas." + str +".teams." + team + ".cancha.y"),
                                config.getDouble("arenas." + str+ ".teams." + team+ ".cancha.z"),
                                (float)config.getDouble("arenas."+str+".teams."+team+".cancha.yaw"),
                                (float)config.getDouble("arenas."+str+".teams."+team+".cancha.pitch")
                        ),
                        new Location(
                                world,
                                config.getDouble("arenas." + str+ ".teams."+team+".cancha.x2"),
                                config.getDouble("arenas."+str+".teams."+team+".cancha.y2"),
                                config.getDouble("arenas."+str+".teams."+team+".cancha.z2"),
                                (float)config.getDouble("arenas."+str+".teams."+team+".cancha.yaw2"),
                                (float)config.getDouble("arenas."+str+".teams."+team+".cancha.pitch2")
                        )
                ));


            }

            Location ballSpawn = new Location(
                    world,
                    config.getDouble("arenas." + str+ ".ball-spawn.x"),
                    config.getDouble("arenas."+str+".ball-spawn.y"),
                    config.getDouble("arenas."+str+".ball-spawn.z"),
                    (float)config.getDouble("arenas."+str+".ball-spawn.yaw"),
                    (float)config.getDouble("arenas."+str+".ball-spawn.pitch")
            );
            Location spawn = new Location(
                    world,
                    config.getDouble("arenas."+str+".spawn.x"),
                    config.getDouble("arenas."+str+".spawn.y"),
                    config.getDouble("arenas."+str+".spawn.z"),
                    (float)config.getDouble("arenas."+str+".spawn.yaw"),
                    (float)config.getDouble("arenas."+str+".spawn.pitch")
            );
            arenas.add(new Arena(minigame,Integer.parseInt(str), arenaName, portero, canchas, ballSpawn, spawn, zones));
        }
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public Arena getArena(int id) {
        for (Arena arena : arenas) {
            if (arena.getId() == id) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArena(String name) {
        for (Arena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArena(Player player) {
        for (Arena arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }
        return null;
    }


}