package com.lubodi.futbollwachu.Instance;


import com.google.common.collect.TreeMultimap;
import com.lubodi.futbollwachu.BolaFisicasYMetodos.Metodos;
import com.lubodi.futbollwachu.Countdown.Countdown;
import com.lubodi.futbollwachu.Countdown.CountdownEnd;
import com.lubodi.futbollwachu.Countdown.CountdownGame;
import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.Instance.mecanicas.AdministradorDeSaques;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import com.lubodi.futbollwachu.Manager.Region;
import com.lubodi.futbollwachu.team.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;

import org.bukkit.entity.*;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Arena {


    /**
     * Mapa concurrente que almacena a los porteros de cada equipo.
     */
    private final ConcurrentHashMap<Team, Region> portero;



    /**
     * Scoreboard del juego.
     */
    private Scoreboard scoreboard;

    /**
     * Objetivo del scoreboard.
     */
    private Objective objective;

    /**
     * Instancia del juego FutballBola.
     */
    private final FutballBola minigame;



    /**
     * ID de la arena.
     */
    private final int id;

    /**
     * Nom de la arena.
     */
    private final  String name;

    /**
     * Mapa concurrente que almacena las canchas de cada equipo.
     */
    private final ConcurrentHashMap<Team, Region> canchas;

    /**
     * Ubicación de generación del balón.
     */
    private final Location ballSpawn;

    /**
     * Ubicación de generación de jugadores.
     */
    private final Location spawn;

    /**
     * Mapa concurrente que almacena las zonas de cada equipo.
     */
    private final ConcurrentHashMap<Team, Region> zones;

    /**
     * Instancia del juego.
     */
    private Game game;

    /**
     * Instancia de la clase Metodos.
     */
    private final Metodos metodos;

    /**
     * Mapa concurrente que almacena los ID de los porteros de cada equipo.
     */
    private final ConcurrentHashMap<Team, UUID> porteros;

    /**
     * Countdown para el inicio del juego.
     */
    private CountdownGame countdownGame;

    /**
     * Countdown para el final del juego.
     */
    private CountdownEnd countdownEnd;

    /**
     * Countdown general.
     */
    private Countdown countdown;

    /**
     * Estado del juego.
     */
    private GameState state;

    /**
     * HashMap que almacena los equipos de los jugadores.
     */
    private final HashMap<UUID, Team> teams;

    /**
     * HashMap containing the teams and their corresponding name
     */

    private final HashMap<Team, String> teamNames;


    private final HashMap<Team, ChatColor> teamColors;

    /**
     * Lista de jugadores.
     */
    private final List<UUID> players = new CopyOnWriteArrayList<>();

    /**
     * Instancia del administrador de habilidades.
     */
    private final HabilidadesManager habilidades;

    /**
     * Último jugador en golpear la pelota.
     */
    private Player lastHitters;

    /**
     * Ubicación del altavoz del juego.
     */




    private final Map<UUID, Silverfish> bolas;
    private final AdministradorDeSaques tiros;
        /**
         * Constructor de la clase Arena.
         *
         * @param minigame      Instancia del juego FutballBola.
         * @param id            ID de la arena.
         * @param portero       Mapa concurrente que almacena las regiones de los porteros de cada equipo.
         * @param canchas       Mapa concurrente que almacena las regiones de las canchas de cada equipo.
         * @param ballSpawn     Ubicación de generación del balón.
         * @param spawn         Ubicación de generación de jugadores.

         */


        /*
            Futbal
            Time left 2sec4
            3
            Goals team1 02
            Goals team2 11
            0
         */

    public Arena(FutballBola minigame, int id, String name, ConcurrentHashMap<Team, Region> portero, ConcurrentHashMap<Team, Region> canchas, Location ballSpawn, Location spawn, ConcurrentHashMap<Team, Region> zones, HashMap<Team, String> teamNames, HashMap<Team, ChatColor> teamColors) {
        this.id = id;
        this.name = name;
        this.metodos = new Metodos(minigame, minigame.getFisicas());
        this.minigame = minigame;
        this.portero = portero;
        this.canchas = canchas;
        this.ballSpawn = ballSpawn;
        this.spawn = spawn;
        this.zones = zones;
        this.teamNames = teamNames;
        this.teamColors = teamColors;
        this.porteros = new ConcurrentHashMap<>();
        this.teams = new HashMap<>();

        this.lastHitters = getLastHitters();
        this.state = GameState.RECRUITING;
        this.game = new Game(this);
        this.countdown = new Countdown(minigame, this);
        this.countdownGame = new CountdownGame(minigame, this, game);
        this.countdownEnd = new CountdownEnd(minigame, this, game);
        this.scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective("Marcador", "dummy", ChatColor.GOLD.toString() + ChatColor.BOLD + "Futbal");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.habilidades = HabilidadesManager.getInstance();
        this.bolas = new HashMap<>();

        //Team1
        org.bukkit.scoreboard.Team team1 = objective.getScoreboard().registerNewTeam(getTeamName(Team.RED));
        team1.addEntry(ChatColor.GREEN.toString());
        team1.setPrefix(ChatColor.GREEN.toString() + "Goals " + getTeamName(Team.RED) + " ");
        team1.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "0  ");
        objective.getScore(ChatColor.GREEN.toString()).setScore(2);

        //Team2
        org.bukkit.scoreboard.Team team2 = objective.getScoreboard().registerNewTeam(getTeamName(Team.BLUE));
        team2.addEntry(ChatColor.GOLD.toString());
        team2.setPrefix(ChatColor.GREEN.toString() + "Goals " + getTeamName(Team.BLUE) + " ");
        team2.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "0  ");
        objective.getScore(ChatColor.GOLD.toString()).setScore(1);

        //Countdown
        org.bukkit.scoreboard.Team timeTeam = objective.getScoreboard().registerNewTeam("timeTeam");
        timeTeam.addEntry(ChatColor.YELLOW.toString());
        timeTeam.setPrefix(ChatColor.GREEN.toString() + "Time left ");
        timeTeam.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + String.valueOf(ConfigManager.getCountDownGameSeconds()));
        objective.getScore(ChatColor.YELLOW.toString()).setScore(4);

        Score space1 = objective.getScore(" ");
        Score space2 = objective.getScore("  ");

        space1.setScore(3);
        space2.setScore(5);

        this.tiros = new AdministradorDeSaques(this);
    }

    public void startCountdownGame() {
        countdownGame.start();
    }

    public void startCountdownEndGame() {
        countdownEnd.start();
    }

    public void Start() {
        game.start();
    }

    public void End() {
        game.end();
    }


    public void addPlayers(Player player) {
        players.add(player.getUniqueId());
        player.teleport(spawn);

        TreeMultimap<Integer, Team> count = TreeMultimap.create();
        for (Team team : Team.values()) {
            count.put(getTeamCount(team), team);
        }
        Team lowest = count.values().iterator().next();
        setTeam(player, lowest);
        player.sendMessage(ChatColor.AQUA + "Se te ha seleccionado al equipo " + getTeamName(lowest));

        if (state == GameState.RECRUITING && players.size() >= ConfigManager.getRequiredPlayers()) {
            countdown.start();
        }

        // Aquí verificas si ya hay un portero en el equipo del jugador
        Team playerTeam = getTeam(player);
        if (playerTeam != null) {
            if (!porteros.containsKey(playerTeam)) {
                // Si no hay un portero en el equipo, asignas al jugador actual como portero
                porteros.put(playerTeam, player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "¡Eres el portero del equipo " + getTeamName(playerTeam) + "!");
            }
        }
    }



    public void reset(boolean kickPlayers) {
        if (kickPlayers) {
            Location loc = ConfigManager.getLobbySpawn();
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.teleport(loc);
                    habilidades.eliminarTodasLasHabilidades(player.getUniqueId());
                    player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
                }
            }

            players.clear();
            teams.clear();
        }
        sendTitle("", "");
        state = GameState.RECRUITING;
        if (countdown != null) {
            countdown.cancel();
        }


        eliminarScoreboard("Marcador");
        objective = scoreboard.registerNewObjective("Marcador", "dummy", ChatColor.GOLD.toString() + ChatColor.BOLD + "Futbal");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        //Team1
        if (objective.getScoreboard().getTeam(getTeamName(Team.RED)) == null) {
            org.bukkit.scoreboard.Team team1 = objective.getScoreboard().registerNewTeam(getTeamName(Team.RED));
            team1.addEntry(ChatColor.GREEN.toString());
            team1.setPrefix(ChatColor.GREEN.toString() + "Goals " + getTeamName(Team.RED) + " ");
            team1.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "0  ");
            objective.getScore(ChatColor.GREEN.toString()).setScore(2);
        } else {
            org.bukkit.scoreboard.Team team1 = objective.getScoreboard().getTeam(getTeamName(Team.RED));
            team1.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "0  ");
        }

        //Team2
        if (objective.getScoreboard().getTeam(getTeamName(Team.BLUE)) == null) {
            org.bukkit.scoreboard.Team team2 = objective.getScoreboard().registerNewTeam(getTeamName(Team.BLUE));
            team2.addEntry(ChatColor.GOLD.toString());
            team2.setPrefix(ChatColor.GREEN.toString() + "Goals " + getTeamName(Team.BLUE) + " ");
            team2.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "0  ");
            objective.getScore(ChatColor.GOLD.toString()).setScore(1);
        } else {
            org.bukkit.scoreboard.Team team2 = objective.getScoreboard().getTeam(getTeamName(Team.BLUE));
            team2.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "0  ");
        }

        //Countdown
        if (objective.getScoreboard().getTeam("timeTeam") == null) {
            org.bukkit.scoreboard.Team timeTeam = objective.getScoreboard().registerNewTeam("timeTeam");
            timeTeam.addEntry(ChatColor.YELLOW.toString());
            timeTeam.setPrefix(ChatColor.GREEN.toString() + "Time left ");
            timeTeam.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + String.valueOf(ConfigManager.getCountDownGameSeconds()));
            objective.getScore(ChatColor.YELLOW.toString()).setScore(4);
        }

        Score space1 = objective.getScore(" ");
        Score space2 = objective.getScore("  ");

        space1.setScore(3);
        space2.setScore(5);


        countdownGame = new CountdownGame(minigame, this, game);
        countdownEnd = new CountdownEnd(minigame, this, game);
        countdown = new Countdown(minigame, this);
        game = new Game(this);

    }


    /**
     * Remove a player from the game and handle game state conditions.
     *
     * @param  player  the player to be removed
     */
    public void removePlayers(Player player) {
        players.remove(player.getUniqueId());
        player.teleport(ConfigManager.getLobbySpawn());
        player.sendTitle("", "");
        removeTeam(player);
        if (state == GameState.COUNTDOWN && players.size() < ConfigManager.getRequiredPlayers()) {
            sendmessage(ChatColor.RED + "No hay suficientes jugadores para jugar.");
            reset(false);
            return;
        }
        if (state == GameState.LIVE && players.size() < ConfigManager.getRequiredPlayers()) {
            sendmessage(ChatColor.RED + "Demasiados jugadores se han salido de la partida.");
            reset(false);
        }
    }

    public void sendmessage(String message) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendTitle(title, subtitle);
            }
        }
    }



    /**
     * Sets the team for a player and removes the player from any existing team.
     *
     * @param  player  the player whose team is being set
     * @param  team    the new team for the player
     */
    public void setTeam(Player player, Team team) {
        removeTeam(player);
        teams.put(player.getUniqueId(), team);
    }
    /**
     * Removes the team associated with the specified player.
     *
     * @param  player	the player whose team is to be removed
     */
    public void removeTeam(Player player) {
        teams.remove(player.getUniqueId());
    }
    /**
     * Removes the team associated with the specified player.
     *
     * @param  team	the player whose team is to be removed
     */
    public int getTeamCount(Team team) {
        int amount = 0;
        for (Team t : teams.values()) {
            if (t == team) {
                amount++;
            }
        }
        return amount;
    }
    /**
     * Retrieves the team associated with the given player.
     *
     * @param  player   the player for whom to retrieve the team
     * @return          the team associated with the player
     */
    public Team getTeam(Player player) {
        return teams.get(player.getUniqueId());
    }
    /**
     * Retrieves the zones mapping for teams to regions.
     *
     * @return  the ConcurrentHashMap containing the mapping of teams to regions
     */
    public ConcurrentHashMap<Team, Region> getZones() {
        return zones;
    }

    public void spawnearbola() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(minigame, () -> {
            Silverfish bola = metodos.spawnSilverfishAtLocation(ballSpawn);
            bolas.put(bola.getUniqueId(), bola);
            World world = ballSpawn.getWorld();
            assert world != null;
            world.spawnParticle(Particle.REDSTONE,
                    ballSpawn,
                    100, // Cantidad de partículas
                    1, 2, 1, // Desplazamiento en X, Y y Z
                    5, // Tamaño de la partícula
                    new Particle.DustOptions(
                            Color.fromRGB(0, 0, 0), // Color RGB (azul)
                            1 // Opacidad
                    )
            );
            world.playSound(ballSpawn, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
            players.stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(this::checkAndTeleportPlayer);


        }, 120);
    }
    /**
     * Gets the team of the specified entity within the canchas map,
     * based on certain conditions.
     *
     * @param  entity  the entity for which to find the team
     * @return         the team of the entity, or null if not found
     */

    public Team getTeamOfCancha(Entity entity) {
        if (entity instanceof Silverfish && Objects.equals(entity.getCustomName(), "Bola")) {
            for (Map.Entry<Team, Region> entry : this.canchas.entrySet()) {
                Region cancha = entry.getValue();
                if (cancha.contains(entity)) {

                    return entry.getKey();
                }
            }
        }

        return null;
    }

    /**
     * Get the team of the specified player within the defined zones.
     *
     * @param  player  the player for whom to find the team
     * @return         the team of the specified player within the defined zones, or null if not found
     */
    public Team getTeamOfZona(Player player) {
        Team playerTeam = teams.get(player.getUniqueId());
        for (Map.Entry<Team, Region> entry : this.zones.entrySet()) {
            Team teamZona = entry.getKey();
            Region zona = entry.getValue();
            if (playerTeam == teamZona && zona.contains(player)) {
                return  teamZona;
            }
        }
        return null;
    }

    /**
     * Determines if the player is the goalkeeper of the zone.
     *
     * @param  player   the player to check
     * @return         true if the player is the goalkeeper of the zone, false otherwise
     */
    public boolean getPorteroOfZona(Player player) {

        Team playerTeam = teams.get(player.getUniqueId());

        Team teamDeZona = getTeamOfZona(player);

        for (Map.Entry<Team, Region> entry : this.portero.entrySet()) {

            if (entry.getValue().contains(player) &&
                    playerTeam == teamDeZona &&
                    isPlayerPortero(player)) {

                return true;

            }

        }

        return false;

    }
    /**
     * Checks if the player is a goalkeeper in any team.
     *
     * @param  player   the player to check
     * @return         true if the player is a goalkeeper, false otherwise
     */
    public  boolean isPlayerPortero(Player player) {
        for (Team team : porteros.keySet()) {
            if (porteros.get(team) == player.getUniqueId()) {
                return true;
            }
        }
        return false;
    }
    /**
     * Retrieves the entity in the "cancha" (field) associated with a team, if present.
     *
     * @return         the entity in the "cancha" (field) associated with a team, if present; otherwise null
     */
    public Entity getEntityInCancha() {
        for (Map.Entry<Team, Region> entry : this.canchas.entrySet()) {
            Team team = entry.getKey();
            Region cancha = entry.getValue();
            for (Entity entity : Bukkit.getWorld("world").getEntities()) {
                if (entity instanceof Silverfish && entity.getCustomName().equals("Bola") && cancha.contains(entity)) {
                    System.out.println("La entidad está en la cancha del equipo " + getTeamName(team));
                    return entity;
                }
            }
        }
        return null;
    }

    public Region getCancha(Team team) {
        if (canchas.containsKey(team)) {
            return canchas.get(team);
        } else  {
            return null;
        }
    }

    /**
     * Shows the scoreboard to players within a certain radius.
     *
     * @param  radius  the radius within which to display the scoreboard
     */
    public void showScoreboardToPlayersInRadius(int radius) {
        System.out.println("Running");
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Player nearbyPlayer : player.getWorld().getPlayers()) {
                if (player != nearbyPlayer && player.getLocation().distance(nearbyPlayer.getLocation()) <= radius) {
                    System.out.println("setting scoreboard");
                    System.out.println(scoreboard != null);
                    System.out.println(scoreboard.getObjective("Marcador") != null);
                    System.out.println(scoreboard.getObjective("Marcador").getScoreboard().getTeam(getTeamName(Team.RED)) != null);
                    player.setScoreboard(scoreboard);
                    System.out.println("Set scoreboard for: " + player.getName());
                }
            }
        }
    }
    /**
     * Updates the scores for a given team.
     *
     * @param  team  the team whose score will be updated
     * @param  score the new score for the team
     */
    public void updateScores(Team team, int score) {
        org.bukkit.scoreboard.Team bukkitTeam = objective.getScoreboard().getTeam(getTeamName(team));
        bukkitTeam.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + score + "  ");
    }
    /**
     * Updates the scores based on the given time.
     *
     * @param  time  the time to update the scores with
     */
    public void updateScoresTime(int time) {
        org.bukkit.scoreboard.Team timeTeam = objective.getScoreboard().getTeam("timeTeam");
        timeTeam.setSuffix(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + String.valueOf(time) + "  ");
    }

    /**
     * Deletes the specified scoreboard if it exists.
     *
     * @param  nombreMarcador  the name of the scoreboard to be deleted
     */

    public void eliminarScoreboard(String nombreMarcador) {
        if (scoreboard.getObjective(nombreMarcador) != null) {
            scoreboard.getObjective(nombreMarcador).unregister();
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
    }




    /**
     * Teleports the player to an equivalent location in the target region based on the distances to the corners of the source region.
     *
     * @param  player         the player to be teleported
     * @param  sourceRegion   the region from which the player is being teleported
     * @param  targetRegion   the region to which the player is being teleported
     */
    public void teleportToEquivalentLocation(Player player, Region sourceRegion, Region targetRegion) {
        double[][] distancesToCorners = sourceRegion.getDistancesToCorners(player);

        double equivalentX = distancesToCorners[1][0] * (Math.max(targetRegion.getCorner1().getX(), targetRegion.getCorner2().getX()) - Math.min(targetRegion.getCorner1().getX(), targetRegion.getCorner2().getX())) + Math.min(targetRegion.getCorner1().getX(), targetRegion.getCorner2().getX());
        double equivalentY = distancesToCorners[1][1] * (Math.max(targetRegion.getCorner1().getY(), targetRegion.getCorner2().getY()) - Math.min(targetRegion.getCorner1().getY(), targetRegion.getCorner2().getY())) + Math.min(targetRegion.getCorner1().getY(), targetRegion.getCorner2().getY());
        double equivalentZ = distancesToCorners[1][2] * (Math.max(targetRegion.getCorner1().getZ(), targetRegion.getCorner2().getZ()) - Math.min(targetRegion.getCorner1().getZ(), targetRegion.getCorner2().getZ())) + Math.min(targetRegion.getCorner1().getZ(), targetRegion.getCorner2().getZ());

        System.out.println("ubicacion del jugador: " + player.getLocation());
        System.out.println("Calculando ubicación equivalente:");
        System.out.println("Distancias a esquinas: [" + distancesToCorners[1][0] + ", " + distancesToCorners[1][1] + ", " + distancesToCorners[1][2] + "]");
        System.out.println("Equivalente X: " + equivalentX);
        System.out.println("Equivalente Y: " + equivalentY);
        System.out.println("Equivalente Z: " + equivalentZ);

        Location equivalentLocation = new Location(player.getWorld(), equivalentX, equivalentY, equivalentZ);
        System.out.println("Teletransportando al jugador a la ubicación equivalente.");

        player.teleport(equivalentLocation);
        System.out.println("Jugador teletransportado con éxito.");
    }


    /**
     * Check and teleport the player if they are not in their team's region.
     *
     * @param  player  the player to check and potentially teleport
     */
    public void checkAndTeleportPlayer(Player player) {
        Team playerTeam = teams.get(player.getUniqueId());
        Region playerRegion = null;
        Region teamRegion = null;

        // Buscar la región del jugador y la región del equipo del jugador
        for (Map.Entry<Team, Region> entry : this.zones.entrySet()) {
            if (entry.getValue().contains(player)) {
                playerRegion = entry.getValue();
            }
            if (entry.getKey().equals(playerTeam)) {
                teamRegion = entry.getValue();
            }
        }

        System.out.println("Jugador: " + player.getName());
        System.out.println("Equipo del jugador: " + playerTeam);
        System.out.println("Región del jugador: " + playerRegion);
        System.out.println("Región del equipo del jugador: " + teamRegion);

        // Si el jugador está en una región que no es la de su equipo, teletransportarlo
        if (playerRegion != null && teamRegion != null && !playerRegion.equals(teamRegion)) {
            teleportToEquivalentLocation(player, playerRegion, teamRegion);
        }
    }
    /**
     * SoltarBola method releases the ball for the player after a delay of 60 ticks.
     *
     * @param  player   the player for whom the ball is released
     */
    public static void SoltarBola(Player player) {
        FutballBola plugin = FutballBola.getInstance();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player != null) {
                player.eject();
            }
        }, 60);
    }

    public void matarSilverfish() {
        for (Silverfish silverfish : bolas.values()) {
            silverfish.remove();
        }
        bolas.clear();
    }




    public void IniciarTirosLibres() {
        tiros.ejecutarAccion();
    }


    public AdministradorDeSaques getAdministradorDeSaques() {
        return tiros;
    };
    public Map<UUID, Silverfish> getBolas() {
        return bolas;
    }

    public Region getPorteroRegion(Team team) {
        return portero.get(team);
    }

    public ConcurrentHashMap<Team, UUID> getPorteros() {
        return porteros;
    }


    public void setLastHitters(Player lastHitters) {
        this.lastHitters = lastHitters;
    }

    public Player getLastHitters() {
        return lastHitters;
    }


    public List<UUID> getPlayers() {
        return players;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }



    public Region getRegionZona(Team team) {
        return zones.get(team);
    }








    public GameState getState() {
        return state;
    }
    public void setState(GameState state) {
        this.state = state;
    }


    public String getTeamName(Team team) {
        if (teamNames.containsKey(team)) {
            return teamNames.get(team);
        }
        return null;
    }

    public ChatColor getTeamColor(Team team) {
        if (teamColors.containsKey(team)) {
            return teamColors.get(team);
        }
        return ChatColor.WHITE;
    }

}
