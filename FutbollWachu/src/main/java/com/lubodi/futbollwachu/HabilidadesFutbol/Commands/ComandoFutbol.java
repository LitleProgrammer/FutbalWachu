package com.lubodi.futbollwachu.HabilidadesFutbol.Commands;



import com.lubodi.futbollwachu.BolaFisicasYMetodos.Metodos;
import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.HabilidadesFutbol.GUI.HabilidadGUI;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.Instance.Arena;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ComandoFutbol implements CommandExecutor {
    private final FutballBola plugin;
    private final Metodos metodos;

    public ComandoFutbol(FutballBola plugin, Metodos metodos) {
        this.plugin = plugin;
        this.metodos = metodos;
        new Metodos(plugin,plugin.getFisicas());
    }


    /**
     * Overrides the onCommand method to handle different subcommands and their respective actions.
     *
     * @param  sender  the command sender
     * @param  command the command being executed
     * @param  label   the alias of the command used
     * @param  args    the arguments provided with the command
     * @return         true if the command was handled, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // No se proporcionaron subcomandos, muestra un mensaje de ayuda.
            sender.sendMessage("Por favor proporciona un subcomando: crearbola, matarbola, habilidades");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        switch (args[0]) {
            case "crearbola":
                if (args.length == 1) {
                   metodos.spawnSilverfishAtLocation(player.getLocation());
                } else if (args.length == 4) {
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);
                    Location location = new Location(player.getWorld(), x, y, z);
                   metodos.spawnSilverfishAtLocation(location);
                } else {
                    player.sendMessage("Uso incorrecto. /futbol crearbola [x] [y] [z]");
                }
                return true;
            case "matarbola":
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity.getCustomName() != null && entity.getCustomName().equals("Bola")) {
                        metodos.matarBola(entity);
                    }
                }
                player.sendMessage("Todas las bolas han sido eliminadas.");
                return true;
            case "habilidades":
                Arena arena = plugin.getArenaManager().getArena(player);
                if(arena != null){
                    if(arena.getState() != GameState.LIVE){
                        HabilidadGUI habilidadGUI = new HabilidadGUI(plugin, HabilidadesManager.getInstance());
                        habilidadGUI.abrir(player);
                    }
                    player.sendMessage("no puedes usar este comando en este momento ");
                }

                return true;
            default:
                sender.sendMessage("Subcomando desconocido: " + args[0]);
                return true;
        }
    }
}
