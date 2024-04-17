package com.lubodi.futbollwachu.Commands;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.ArenaManager;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import com.lubodi.futbollwachu.Manager.Region;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AdminArenaCommand implements CommandExecutor {
    private final FutballBola minigame;
    public AdminArenaCommand(FutballBola minigame) {
        this.minigame = minigame;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to execute this command!");
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("create")) {
            // adminArena create <name>
            if (args.length == 2) {
                String name = args[1];
                if (createYAMLArena(name, player.getWorld().getName())) {
                    sender.sendMessage(ChatColor.GREEN + "Successfully created the arena named: " + name);
                } else {
                    sender.sendMessage(ChatColor.RED + "Something went wrong during the creation");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
            }
        } else if (args[0].equalsIgnoreCase("edit")) {
            String name = args[1];
            switch (args[2]) {
                case "setField":
                    // /adminArena edit <name> setField x1 y1 z1 x2 y2 z2
                    if (args.length == 9) {
                        //Getting the corners from the command
                        Location corner1 = new Location(player.getWorld(), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
                        Location corner2 = new Location(player.getWorld(), Double.parseDouble(args[6]), Double.parseDouble(args[7]), Double.parseDouble(args[8]));

                        //Creating a region and splitting it
                        Region field = new Region(corner1, corner2);
                        Region red = field.splitInHalf()[0];
                        Region blue = field.splitInHalf()[1];

                        int arenaID = minigame.getSetupArenas().get(name);

                        //Setting the new regions to the config file
                        ConfigManager.setRegion("arenas." + arenaID + ".teams.RED.zona", red);
                        ConfigManager.setRegion("arenas." + arenaID + ".teams.BLUE.zona", blue);

                        //Getting the middle of the field to get the spawn locations
                        Location spawn = field.getMiddle();
                        spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));

                        //Setting the spawn locations to the config
                        ConfigManager.setLocation("arenas." + arenaID + ".ball-spawn", spawn);
                        ConfigManager.setLocation("arenas." + arenaID + ".spawn", spawn);

                        sender.sendMessage(ChatColor.GREEN + "Successfully set the field of arena: " + name);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
                    }
                    break;
                case "setGoalTeam1":
                    // /adminArena edit <name> setGoalRed x1 y1 z1 x2 y2 z2
                    if (args.length == 9) {
                        //Getting the corners from the command
                        Location corner1 = new Location(player.getWorld(), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
                        Location corner2 = new Location(player.getWorld(), Double.parseDouble(args[6]), Double.parseDouble(args[7]), Double.parseDouble(args[8]));

                        //Creating a region and splitting it
                        Region goal = new Region(corner1, corner2);

                        int arenaID = minigame.getSetupArenas().get(name);

                        //Setting the new regions to the config file
                        ConfigManager.setRegion("arenas." + arenaID + ".teams.RED.cancha", goal);

                        sender.sendMessage(ChatColor.GREEN + "Successfully set the red goal of arena: " + name);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
                    }
                    break;
                case "setGoalTeam2":
                    // /adminArena edit <name> setGoalBlue x1 y1 z1 x2 y2 z2
                    if (args.length == 9) {
                        //Getting the corners from the command
                        Location corner1 = new Location(player.getWorld(), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
                        Location corner2 = new Location(player.getWorld(), Double.parseDouble(args[6]), Double.parseDouble(args[7]), Double.parseDouble(args[8]));

                        //Creating a region and splitting it
                        Region goal = new Region(corner1, corner2);

                        int arenaID = minigame.getSetupArenas().get(name);

                        //Setting the new regions to the config file
                        ConfigManager.setRegion("arenas." + arenaID + ".teams.BLUE.cancha", goal);

                        sender.sendMessage(ChatColor.GREEN + "Successfully set the blue goal of arena: " + name);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
                    }
                    break;
                case "setPenaltyAreaTeam1":
                    // /adminArena edit <name> setPenaltyAreaRed x1 y1 z1 x2 y2 z2
                    if (args.length == 9) {
                        //Getting the corners from the command
                        Location corner1 = new Location(player.getWorld(), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
                        Location corner2 = new Location(player.getWorld(), Double.parseDouble(args[6]), Double.parseDouble(args[7]), Double.parseDouble(args[8]));

                        //Creating a region and splitting it
                        Region area = new Region(corner1, corner2);

                        int arenaID = minigame.getSetupArenas().get(name);

                        //Setting the new regions to the config file
                        ConfigManager.setRegion("arenas." + arenaID + ".teams.RED.portero", area);

                        sender.sendMessage(ChatColor.GREEN + "Successfully set the red penalty area of arena: " + name);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
                    }
                    break;
                case "setPenaltyAreaTeam2":
                    // /adminArena edit <name> setPenaltyAreaBlue x1 y1 z1 x2 y2 z2
                    if (args.length == 9) {
                        //Getting the corners from the command
                        Location corner1 = new Location(player.getWorld(), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
                        Location corner2 = new Location(player.getWorld(), Double.parseDouble(args[6]), Double.parseDouble(args[7]), Double.parseDouble(args[8]));

                        //Creating a region and splitting it
                        Region area = new Region(corner1, corner2);

                        int arenaID = minigame.getSetupArenas().get(name);

                        //Setting the new regions to the config file
                        ConfigManager.setRegion("arenas." + arenaID + ".teams.BLUE.portero", area);

                        sender.sendMessage(ChatColor.GREEN + "Successfully set the blue penalty area of arena: " + name);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
                    }
                    break;
                case "setTeam1":
                    if (args.length == 5) {
                        String teamName = args[3];
                        String teamColor = args[4];
                        int arenaID = minigame.getSetupArenas().get(name);

                        ConfigManager.setString("arenas." + arenaID + ".teams.RED.name", teamName);
                        ConfigManager.setString("arenas." + arenaID + ".teams.RED.color", teamColor);
                        sender.sendMessage(ChatColor.GREEN + "Successfully set the name of team 1 of arena: " + name);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
                    }
                    break;
                case "setTeam2":
                    if (args.length == 5) {
                        String teamName = args[3];
                        String teamColor = args[4];
                        int arenaID = minigame.getSetupArenas().get(name);

                        ConfigManager.setString("arenas." + arenaID + ".teams.BLUE.name", teamName);
                        ConfigManager.setString("arenas." + arenaID + ".teams.BLUE.color", teamColor);
                        sender.sendMessage(ChatColor.GREEN + "Successfully set the name of team 2 of arena: " + name);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
                    }
                    break;
            }
        } else if (args[0].equalsIgnoreCase("remove")) {
            // adminArena remove <name>
            if (args.length == 2) {
                String name = args[1];
                if (removeYAMLArena(name)) {
                    try {
                        //Updating the arena manager
                        ArenaManager arenaManager = new ArenaManager(minigame);
                        minigame.setArenaManager(arenaManager);
                        sender.sendMessage(ChatColor.GREEN + "Successfully removed the arena named: " + name);
                    } catch (NullPointerException e) {
                        sender.sendMessage(ChatColor.RED + "Something went wrong! Check, if the name is correct, or if the arena is already removed.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Something went wrong! Check, if the name is correct, or if the arena is already removed.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
            }
        } else if (args[0].equalsIgnoreCase("finish")) {
            //Updating the arenaManager
            if (args.length == 2) {
                String name = args[1];
                try {
                    ArenaManager arenaManager = new ArenaManager(minigame);
                    minigame.setArenaManager(arenaManager);
                    sender.sendMessage(ChatColor.GREEN + "Successfully finished the arena named: " + name);
                } catch (NullPointerException e) {
                    sender.sendMessage(ChatColor.RED + "Something went wrong! Check, if all values are set in the config.");
                }
            }
        } else if (args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(player);
        } else {
            sender.sendMessage(ChatColor.RED + "Wrong usage type /adminArena help to get help");
        }
        return false;
    }

    public boolean createYAMLArena(String name, String world) {
        FileConfiguration config = minigame.getConfig();

        //Check, if an arena with this name already exists
        if (minigame.getArenaManager().getArena(name) != null) { return false; }

        int arenaID = getNextAvailableArenaNumber();

        config.createSection("arenas." + arenaID);
        config.set("arenas." + arenaID + ".name", name);
        config.set("arenas." + arenaID + ".world", world);
        minigame.saveConfig();
        minigame.getSetupArenas().put(name, arenaID);

        return true;
    }

    public boolean removeYAMLArena(String name) {
        FileConfiguration config = minigame.getConfig();

        //Check, if an arena with this name exists
        if (minigame.getArenaManager().getArena(name) == null) { return false; }

        //Getting the arena by it's name
        Arena arena = minigame.getArenaManager().getArena(name);

        config.set("arenas." + arena.getId(), null);
        minigame.saveConfig();

        return true;
    }

    public int getNextAvailableArenaNumber() {
        FileConfiguration config = minigame.getConfig();

        // Get the 'arenas' section from the config
        if (!config.contains("arenas")) {
            // If 'arenas' section doesn't exist, return 0
            return 0;
        }

        //Looping trough valid arena id's
        int highestNumber = -1;
        for (String key : config.getConfigurationSection("arenas.").getKeys(false)) {
            try {
                if (Integer.parseInt(key) > highestNumber) {
                    highestNumber = Integer.parseInt(key);
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return highestNumber + 1;
    }

    public void sendHelpMessage(Player target) {
        target.sendMessage(ChatColor.WHITE + "----------------------------------");
        target.sendMessage(ChatColor.WHITE + "  Available commands:");
        target.sendMessage(ChatColor.WHITE + "  /adminArena help - show this menu");
        target.sendMessage(ChatColor.WHITE + "  /adminArena create <arenaName> - start the creation of a new arena");
        target.sendMessage(ChatColor.WHITE + "  /adminArena edit <arenaName> <option> <corner1> <corner2> - edit an option of the arena");
        target.sendMessage(ChatColor.WHITE + "  /adminArena finish <arenaName> - finish the creation of an arena and reload the arena list");
        target.sendMessage(ChatColor.WHITE + "  /adminArena remove <arenaName> - remove an arena from the config");
        target.sendMessage(ChatColor.WHITE + "----------------------------------");
    }

}
