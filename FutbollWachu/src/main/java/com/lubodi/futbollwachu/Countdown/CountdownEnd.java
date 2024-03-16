package com.lubodi.futbollwachu.Countdown;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Instance.Game;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class CountdownEnd extends BukkitRunnable {
    private FutballBola minigame;
    private Arena arenas;
    private AtomicInteger countdownSeconds;

    private Game game;

    /**
     * Constructor for the CountdownEnd class.
     *
     * @param minigame The instance of the FutballBola class.
     * @param arenas The instance of the Arena class.
     * @param game The instance of the Game class.
     * @throws IllegalArgumentException if minigame, arenas, or game is null.
     */
    public CountdownEnd(FutballBola minigame, Arena arenas, Game game) {
        this.minigame = minigame;
        this.arenas = arenas;
        this.countdownSeconds = new AtomicInteger(ConfigManager.getCountDownEndSeconds());
        this.game = game;
    }
    /**
     * The start method begins the minigame if the arenas state is not in countdown
     *
     */
    public void start() {
      if (arenas.getState() != GameState.COUNTDOWN) {
          arenas.setState(GameState.COUNTDOWN);
      }
      runTaskTimer(minigame, 1, 20);
    }
    /**
     * The run function overrides the run method. It checks if the countdownSeconds
     * is 0 and cancels the operation. It also ends the arenas, removes the redstone block,
     * and decrements the countdownSeconds.
     */
    @Override
    public void run() {
        if (countdownSeconds.get() == 0) {
            cancel();
            arenas.End();
            return;
        }
        countdownSeconds.decrementAndGet();
    }
}

