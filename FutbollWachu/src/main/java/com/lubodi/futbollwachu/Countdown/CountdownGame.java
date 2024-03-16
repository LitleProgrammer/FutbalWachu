package com.lubodi.futbollwachu.Countdown;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Instance.Game;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import com.lubodi.futbollwachu.team.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class CountdownGame extends BukkitRunnable {
    private FutballBola minigame;
    private Arena arenas;
    private int countdownSeconds;
    private Game game;

    public CountdownGame(FutballBola minigame, Arena arenas, Game game) {
        this.minigame = minigame;
        this.arenas = arenas;
        this.countdownSeconds = ConfigManager.getCountDownGameSeconds();
        this.game = game;
    }
    /**
     * Starts the function, running a task timer and placing a redstone block in the specified arena.
     *
     */
    public void start() {

        runTaskTimer(minigame, 0, 20);

    }
    /**
     * The run method for the game countdown.
     */
    @Override
    public void run() {
        if(countdownSeconds == 0){
            cancel();
            Team winningTeam = game.getWinningTeam();
            if (winningTeam != null) {
                // Hay un equipo ganador
                String winningTeamName = game.getWinningTeam().getDisplay();
                arenas.sendTitle(winningTeamName + " ha ganado", "");
            } else {
                // Empate
                arenas.sendTitle("Empate", "");
            }
            arenas.startCountdownEndGame();
            arenas.matarSilverfish(arenas.getBallSpawn(), 80);
            return;
        }

        for (UUID uuid : arenas.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && arenas.isPlayerPortero(player)) {
                if (!arenas.getPorteroOfZona(player)) {
                    List<Entity> passengers = player.getPassengers();
                    if (!passengers.isEmpty()) {
                        player.eject();
                    }
                }
            }
        }
        arenas.updateScoresTime(1);
        game.handleBall(arenas.getEntityInCancha());
        arenas.IniciarTirosLibres();
        countdownSeconds--;
    }
}
