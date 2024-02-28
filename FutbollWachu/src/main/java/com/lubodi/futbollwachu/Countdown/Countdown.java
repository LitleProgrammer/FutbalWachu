package com.lubodi.futbollwachu.Countdown;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import com.lubodi.futbollwachu.team.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Countdown extends BukkitRunnable {
    private FutballBola minigame;
    private Arena arenas;
    private AtomicInteger countdownSeconds;


    public Countdown(FutballBola minigame, Arena arenas) {
        this.minigame = minigame;
        this.arenas = arenas;
        this.countdownSeconds = new AtomicInteger(ConfigManager.getCountDownSeconds());
    }

    /**
     * Start the game by setting the state to countdown, scheduling a task timer, and
     * checking and teleporting each player in the game arena.
     */
    public void start() {
        arenas.setState(GameState.COUNTDOWN);
        runTaskTimer(minigame, 1, 20);
        for (UUID uuid : arenas.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            arenas.checkAndTeleportPlayer(player);
        }

    }
    /**
     * Run method for starting the countdown and game in the arena.
     */
    @Override
    public void run() {
        if (countdownSeconds.get() == 0) {
            this.cancel();
            arenas.Start();
            arenas.startCountdownGame();
            arenas.sendTitle("", "");
            arenas.showScoreboardToPlayersInRadius(200);
            return;
        }
        if (countdownSeconds.get() <= 10 || countdownSeconds.get() % 15 == 0) {
            arenas.sendmessage(String.format("%sGame will start in %d second%s.", ChatColor.GREEN, countdownSeconds.get(), (countdownSeconds.get() == 1 ? "" : "s")));
        }
        arenas.sendTitle(String.format("%s%d second%s", ChatColor.GREEN, countdownSeconds.get(), (countdownSeconds.get() == 1 ? "" : "s")), ChatColor.GRAY + "until game start");
        countdownSeconds.decrementAndGet();
    }
}
