package com.lubodi.futbollwachu.Commands;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.team.teamsGUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {
    private final FutballBola minigame;
    public ArenaCommand(FutballBola minigame) {
        this.minigame = minigame;
    }

    /**
     * Handles the command execution for the given sender.
     *
     * @param  sender	the command sender
     * @param  command	the command being executed
     * @param  label	the label of the command
     * @param  args 	the arguments for the command
     * @return         whether the command was executed successfully
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                for (Arena arenas : minigame.getArenaManager().getArenas()) {
                    player.sendMessage(ChatColor.GREEN + "-" + arenas.getId() + "-(" + arenas.getState().name());
                }

            }else if (args.length == 1 && args[0].equalsIgnoreCase("team")) {
                Arena arenas = minigame.getArenaManager().getArena(player);
                if(arenas != null){
                    if(arenas.getState() != GameState.LIVE){
                        new teamsGUI(arenas,player);
                    }else {
                        player.sendMessage("no puedes usar este comando en este momento ");
                    }

                }else {
                    player.sendMessage(ChatColor.RED + " no estas en una arena");
                }

            } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                Arena arenas = minigame.getArenaManager().getArena(player);
                if(arenas != null){
                    player.sendMessage(ChatColor.RED + "te fuiste de la sala");
                    arenas.removePlayers(player);
                }else {
                    player.sendMessage(ChatColor.RED + " no estas en una arena");
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
                if(minigame.getArenaManager().getArena(player) != null) {
                    player.sendMessage(ChatColor.RED + "ya estas en una arena");
                    return false;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e){
                    player.sendMessage(ChatColor.RED + "especificaste un id de arena invalido");
                    return false;
                }

                if(id >= 0 && id < minigame.getArenaManager().getArenas().size()){
                    Arena arenas = minigame.getArenaManager().getArena(id);
                    if(arenas.getState() == GameState.RECRUITING || arenas.getState() == GameState.COUNTDOWN){
                        player.sendMessage(ChatColor.GREEN + "estas jugando en la arena" + id);
                        arenas.addPlayers(player);
                    }else {
                        player.sendMessage(ChatColor.RED + "no puedes unirtte");
                    }
                }else {
                    player.sendMessage(ChatColor.RED + "ya estas en una arena");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Uso invalido, estas on las opciones ");
                player.sendMessage(ChatColor.RED + "/ arena list");
                player.sendMessage(ChatColor.RED + "/ arena join");
                player.sendMessage(ChatColor.RED + "/ arena join <id> (número de la arena a la cual quieres entrar");

                player.sendMessage(ChatColor.RED + "/ arena team");

            }
        }
        return false;
    }
}

