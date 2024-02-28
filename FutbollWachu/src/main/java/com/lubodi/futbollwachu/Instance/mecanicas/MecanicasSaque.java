package com.lubodi.futbollwachu.Instance.mecanicas;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class MecanicasSaque {

    private Arena arena;

    private Cache<String, Long> preparacionCooldown; // Cache para el cooldown de preparación
    private Cache<String, Long> ejecucionCooldown;

    public MecanicasSaque(Arena arena) {
        preparacionCooldown = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build();
        ejecucionCooldown = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build();

        this.arena = arena;
    }


    /**
     * Obtiene un jugador aleatorio de un equipo que no sea portero.
     *
     * @param equipo el equipo del cual se quiere obtener un jugador aleatorio.
     * @return un jugador aleatorio del equipo especificado que no sea portero, o null si no se encuentra ninguno.
     */
    public Player obtenerJugadorAleatorioNoPortero(Team equipo) {
        // Obtener el UUID del portero del equipo especificado
        UUID idPortero = arena.getPorteros().get(equipo);
        List<UUID> candidatos = new CopyOnWriteArrayList<>();

        // Iterar sobre los jugadores de la arena
        for (UUID idJugador : arena.getPlayers()) {
            // Usar arena.getTeam(Player player) para obtener el equipo del jugador y compararlo con el equipo especificado
            if (arena.getTeam(Bukkit.getPlayer(idJugador)) == equipo && !idJugador.equals(idPortero)) {
                candidatos.add(idJugador);
            }
        }

        if (candidatos.isEmpty()) {
            return null;  // No se encontró ningún jugador que cumpla con los criterios.
        }

        // Seleccionar un jugador al azar de los candidatos
        int indiceAleatorio = ThreadLocalRandom.current().nextInt(candidatos.size());
        UUID idJugadorAleatorio = candidatos.get(indiceAleatorio);
        return Bukkit.getPlayer(idJugadorAleatorio);
    }
    /**
     * Teleporta a un jugador aleatorio (que no sea portero) de un equipo específico a una locación dada.
     *
     * @param equipo el equipo del cual se quiere obtener un jugador aleatorio.
     * @param locacion la locación a la cual el jugador será teleportado.
     * @return true si el jugador fue teletransportado exitosamente, false en caso contrario.
     */
    public boolean teleportarJugadorAleatorioNoPortero(Team equipo, Location locacion) {
        Player jugador = obtenerJugadorAleatorioNoPortero(equipo);
        if (jugador != null) {
            jugador.teleport(locacion);
            return true;
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
        new BukkitRunnable() {
            int tiempoRestante = tiempo;

            @Override
            public void run() {
                if (tiempoRestante > 0) {
                    // Envía un mensaje a todos los jugadores con el tiempo restante
                    arena.sendmessage("Saque en " + tiempoRestante + " segundos.");
                } else {
                    // Tiempo finalizado, envía un mensaje final y cancela el temporizador
                    arena.sendmessage("¡Saque ahora!");
                    this.cancel(); // Cancela esta tarea desde su ejecución en el futuro
                }

                tiempoRestante--; // Decrementa el tiempo restante
            }
            // El temporizador se inicia inmediatamente (0 ticks de delay) y se repite cada 20 ticks (1 segundo)
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("FutbollWachu"), 0L, 20L);
    }
    public void EjecuciondeSaque(Team team, int tiempo) {
        new BukkitRunnable() {
            int tiempoRestante = tiempo;

            @Override
            public void run() {
                if (tiempoRestante == 0) {

                } else {
                    // Tiempo finalizado, envía un mensaje final y cancela el temporizador
                    arena.sendmessage("¡Saque ahora!");
                    this.cancel(); // Cancela esta tarea desde su ejecución en el futuro
                }

                tiempoRestante--; // Decrementa el tiempo restante
            }
            // El temporizador se inicia inmediatamente (0 ticks de delay) y se repite cada 20 ticks (1 segundo)
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("FutbollWachu"), 0L, 20L);
    }



    public void activarCooldownPreparacion(Player jugador) {
        preparacionCooldown.put(jugador.getName(), System.currentTimeMillis());
    }

    public void activarCooldownEjecucion(Player jugador) {
        ejecucionCooldown.put(jugador.getName(), System.currentTimeMillis());
    }

    public boolean puedePrepararSaque(Player jugador) {
        return preparacionCooldown.getIfPresent(jugador.getName()) == null;
    }

    public boolean puedeEjecutarSaque(Player jugador) {
        return ejecucionCooldown.getIfPresent(jugador.getName()) == null;
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
