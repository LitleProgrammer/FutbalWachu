package com.lubodi.futbollwachu.Commands;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.Instance.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminArenaTab implements TabCompleter {
    private final FutballBola minigame;
    public AdminArenaTab(FutballBola minigame) {
        this.minigame = minigame;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) { return null; }
        Player player = (Player) sender;

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("create", "edit", "remove", "finish"), new ArrayList<>());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("finish")) {
                //return names of arenas
                List<String> arenas = new ArrayList<>();
                for (Arena arena : minigame.getArenaManager().getArenas()) {
                    arenas.add(arena.getName());
                }

                for (String arena : minigame.getSetupArenas().keySet()) {
                    arenas.add(arena);
                }

                return arenas;
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
            return StringUtil.copyPartialMatches(args[2], Arrays.asList("setField", "setGoalRed", "setGoalBlue", "setPenaltyAreaRed", "setPenaltyAreaBlue"), new ArrayList<>());
            // /adminArena edit <name> setPenaltyAreaBlue x1 y1 z1 x2 y2 z2
        } else if ((args.length == 4 && args[0].equalsIgnoreCase("edit")) || (args.length == 7 && args[0].equalsIgnoreCase("edit"))) {
            StringBuilder result = new StringBuilder();
            result.append(player.getTargetBlock(null, 5).getLocation().getX() + " ");
            result.append(player.getTargetBlock(null, 5).getLocation().getY() + " ");
            result.append(player.getTargetBlock(null, 5).getLocation().getZ());

            return Arrays.asList(result.toString());
        }

        return null;
    }
}
