package com.lubodi.futbollwachu.Instance.mecanicas;

import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MecanicasSaque {

    private Arena arena;

    private Boolean saqueRealizado;
    private Boolean SaqueEnProgreso;
   private Boolean SaqueVigente;
    private UUID CobradorDeTiro;
    private AdministradorDeSaques administradorDeSaques;


    public MecanicasSaque(Arena arena, AdministradorDeSaques administradorDeSaques) {

        this.SaqueEnProgreso = false;
        this.SaqueVigente = false;

        saqueRealizado = false;
        this.arena = arena;
        this.CobradorDeTiro = null;
        this.administradorDeSaques = administradorDeSaques;
    }


    public Player teleportarJugadorAleatorioConPortero(Team equipo) {
        List<UUID> jugadoresEquipo = arena.getPlayers().stream()
                .filter(idJugador -> arena.getTeam(Bukkit.getPlayer(idJugador)) == equipo)
                .collect(Collectors.toList());

        if (jugadoresEquipo.isEmpty()) {
            return null;  // No se encontró ningún jugador en el equipo.
        }

        // Seleccionar un jugador al azar del equipo
        UUID idJugadorAleatorio = jugadoresEquipo.get(ThreadLocalRandom.current().nextInt(jugadoresEquipo.size()));
        setCobradorDeTiro(idJugadorAleatorio);
        return Bukkit.getPlayer(idJugadorAleatorio);
    }
    /**
     * Teleporta a un jugador aleatorio (que no sea portero) de un equipo específico a una locación dada.
     *
     * @param equipo el equipo del cual se quiere obtener un jugador aleatorio.
     * @param locacion la locación a la cual el jugador será teleportado.
     * @return true si el jugador fue teletransportado exitosamente, false en caso contrario.
     */
    /**
     * Teleports a random player (excluding goalkeepers) from a specific team to a given location.
     *
     * @param team the team from which to get a random player.
     * @param location the location to which the player will be teleported.
     * @return true if the player was teleported successfully, false otherwise.
     */
    public boolean teleportarJugadorAleatorioNoPortero(Team team, Location location) {
        Player player = teleportarJugadorAleatorioConPortero(team);
        if (player != null) {
            player.teleport(location);
            iniciarPreparacionSaque(10);
            teletransportarBola();
            return true;
        }
        return false;
    }

    public void teletransportarBola() {
        Location loc = administradorDeSaques.encontrarUbicacionParaSaquedeDesdeJugadores(Bukkit.getPlayer(CobradorDeTiro),2.5);
        obtenerSilverFish().teleport(loc);
    }

    private boolean verificarCountdownCorriendo() {
        for (BukkitTask task : Bukkit.getScheduler().getPendingTasks()) {
            if (task.getOwner().equals(Bukkit.getPluginManager().getPlugin("FutballBola"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inicia una cuenta regresiva para la preparación de un saque, enviando mensajes a todos los jugadores
     * en el juego hasta que se alcanza el tiempo dado, momento en el cual el temporizador se cancela.
     *
     * @param tiempo El tiempo en segundos para la cuenta regresiva.
     */
    public void iniciarPreparacionSaque(int tiempo) {
        setCollidable(false);
        setSaqueEnProgreso(true);
        setSaqueVigente(true);
        System.out.println("saque en vigencia");
        new BukkitRunnable() {

            int tiempoRestante = tiempo;

            @Override
            public void run() {
                if (tiempoRestante > 0) {
                    arena.sendmessage("Saque en " + tiempoRestante + " segundos.");
                    tiempoRestante--;
                } else {
                    EjecuciondeSaque(10);
                    arena.sendmessage("¡Saque ahora!");
                    cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("FutballBola"), 0L, 20L);
    }
    public void EjecuciondeSaque( int tiempo) {
        setSaqueEnProgreso(false);
        new BukkitRunnable() {


            int tiempoRestante = tiempo;

            @Override
            public void run() {

                if(getSaqueRealizado()){
                    arena.sendmessage("¡Saque realizado!");
                    setSaqueVigente(false);
                    setSaqueRealizado(false);

                    this.cancel();
                }

                if (tiempoRestante == 0){
                    Team equipo = arena.getTeam(Bukkit.getPlayer(CobradorDeTiro)) == Team.RED ? Team.BLUE : Team.RED;
                    arena.sendmessage("¡Saque el saque no fue realizado, cambio de equipo! a equipo " + equipo);
                    Location loc = administradorDeSaques.encontrarUbicacionParaSaquedeDesdeJugadores(Bukkit.getPlayer(CobradorDeTiro),8);
                    Bukkit.getPlayer(CobradorDeTiro).teleport(loc);
                    teleportarJugadorAleatorioNoPortero(equipo, administradorDeSaques.encontrarUbicacionParaSaqueDeBanda(Bukkit.getPlayer(CobradorDeTiro)));
                    // Tiempo finalizado, envía un mensaje final y cancela el temporizador
                    this.cancel(); // Cancela esta tarea desde su ejecución en el futuro
                }

                tiempoRestante--; // Decrementa el tiempo restante
            }
            // El temporizador se inicia inmediatamente (0 ticks de delay) y se repite cada 20 ticks (1 segundo)
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("FutballBola"), 0L, 20L);
    }

  public Silverfish obtenerSilverFish() {
      Silverfish silverfish = arena.getBolas().values().stream().findFirst().get();
      return silverfish;
  }

  public void setCollidable(boolean collidable) {
      Silverfish silverfish = obtenerSilverFish();
      silverfish.setCollidable(collidable);
  }
    public void saqueRealizado(Boolean saqueRealizado) {
        this.saqueRealizado = saqueRealizado;
    }


    public void setCobradorDeTiro(UUID cobradorDeTiro) {
        CobradorDeTiro = cobradorDeTiro;
    }

    public UUID getCobradorDeTiro() {
        return CobradorDeTiro;
    }

    public Boolean getSaqueRealizado() {
        return saqueRealizado;
    }

    public void setSaqueRealizado(Boolean saqueRealizado) {
        this.saqueRealizado = saqueRealizado;
    }

    public Boolean getSaqueEnProgreso() {
        return SaqueEnProgreso;
    }

    public void setSaqueEnProgreso(Boolean saqueEnProgreso) {
        SaqueEnProgreso = saqueEnProgreso;
    }

    public Boolean getSaqueVigente() {
        return SaqueVigente;
    }

    public void setSaqueVigente(Boolean saqueVigente) {
        SaqueVigente = saqueVigente;
    }
    public Boolean isSaqueVigente(){
        return SaqueVigente;
    }
    public Boolean isSaqueEnProgreso(){
        return SaqueEnProgreso;
    }
}


/*
    usaremos la clase lasthitter para ver quien golpeo la pelota y extraer su equipo
    tambien veremos en que zona salio en las partes del rectangulo y con un switch decidiremos que caso es
        creemos un rectangulo completo usando region
        tendremos un cooldown para prepararse y para realizar la acción
        al realizar el tiro de esquina, tiro libre o saque de meta se le aplicara un efecto de particulas
        al realizar el tiro de esquina, tiro libre o saque de meta se le aplicara un efecto de sonido
        al realizar el tiro de esquina, tiro libre o saque de meta teleportaremos a un jugador aleatorio que no sea el portero para realizar la acción
        usando un commandexucutor podemos cambiar el que cobrará el tiro libre o tiro de esquina
        */
