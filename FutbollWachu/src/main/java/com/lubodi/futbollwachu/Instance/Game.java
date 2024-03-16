package com.lubodi.futbollwachu.Instance;

import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.team.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Game {
    private Arena arena;
    private HashMap<UUID, Integer> points;
    private Map<Team, Integer> teamScores;

    public Game(Arena arena) {
        this.arena = arena;
        points = new HashMap<>();
        teamScores = new HashMap<>();
        for (Team team : Team.values()) {
            teamScores.put(team, 0);
        }
    }

    public void end() {
        arena.reset(true);
    }

    /**
     * The start function initializes the game, sets the state to LIVE, sends a message to the arena,
     * spawns the ball, and resets the points for all players.
     */
    public void start() {
        arena.setState(GameState.LIVE);
        arena.sendmessage(ChatColor.GREEN + "empieza el partido");
        arena.spawnearbola();
        for (UUID uuid : arena.getPlayers()) {
            points.put(uuid, 0);
            Bukkit.getPlayer(uuid).closeInventory();
        }
    }
    /**
     * Adds a point to the specified team's score.
     *
     * @param  team  the team to which a point will be added
     * @return      void
     */
    public void addPoint(Team team) {
        int currentScore = teamScores.get(team);
        teamScores.put(team, currentScore + 1);
    }

    /**
     * Handle the ball entity within the arena.
     *
     * @param  entity   the entity to handle
     * @return          void
     */
    public void handleBall(Entity entity) {

        Team ballTeam = arena.getTeamOfCancha(entity);

        if (ballTeam == null) {
            return;
        }
        // La entidad "Bola" está en la cancha de un equipo

        Team opposingTeam = (ballTeam == Team.RED) ? Team.BLUE : Team.RED; // Obtiene el equipo contrario
        int currentPoints = getTeamPoints(opposingTeam); // Obtiene los puntos del equipo contrario

        // Suma puntos al equipo contrario
        currentPoints++;
        // Actualiza los puntos del equipo contrario
        arena.updateScores(opposingTeam, currentPoints);
        addPoint(opposingTeam);
        // Elimina la entidad "Bola"
        Player player = arena.getLastHitters();
        if(player != null) {
           arena.sendmessage(player.getName() + " ha metido el gol");
        }
        entity.remove();
        arena.spawnearbola();
    }


    /**
     * Retrieves the points for the given team from the teamScores map.
     *
     * @param  team  the team for which to retrieve the points
     * @return       the points of the given team, or 0 if the team is not in the map
     */
    public int getTeamPoints(Team team) {
        if (teamScores.containsKey(team)) {
            return teamScores.get(team);
        } else {
            return 0; // Puedes cambiar esto al valor predeterminado que desees si el equipo no está en el mapa
        }
    }


    /**
     * Get the winning team from the teamScores map.
     *
     * @return          the winning team, or null if there is a tie
     */
    public Team getWinningTeam() {
        Team winningTeam = null;
        int highestScore = 0;

        for (Map.Entry<Team, Integer> entry : teamScores.entrySet()) {
            Team team = entry.getKey();
            int score = entry.getValue();

            if (score > highestScore) {
                winningTeam = team;
                highestScore = score;
            } else if (score == highestScore) {
                // Si hay un empate, regresamos null
                return null;
            }
        }

        return winningTeam;
    }
}
