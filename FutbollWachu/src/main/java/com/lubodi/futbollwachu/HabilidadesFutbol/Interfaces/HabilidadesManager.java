package com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces;
import java.util.*;
import java.util.concurrent.*;


// Clase que administra las habilidades disponibles
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Fuerza;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Portero;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Regate;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Velocidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import com.lubodi.futbollwachu.Instance.Arena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class HabilidadesManager {
    public final Map<UUID, Map<Habilidad, ItemStack>> itemsReemplazo = new ConcurrentHashMap<>();
    public final Map<UUID, Map<Habilidad, ItemStack>> itemsOriginales = new ConcurrentHashMap<>();

    private static class SingletonHelper {
        private static final HabilidadesManager INSTANCE = new HabilidadesManager();
    }

    private static volatile HabilidadesManager instance;

    private Map<UUID, List<Habilidad>> habilidadesJugador = new ConcurrentHashMap<>();
    private Map<UUID, Map<Habilidad, Boolean>> habilidadesActivas = new ConcurrentHashMap<>();

    private Cache<UUID, Long> cooldowns = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();
    private Cache<UUID, Long> cooldownatrapar = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();


    /**
     * iniciarCooldownAtrapar method initiates a cooldown for catching with the given player ID.
     *
     * @param  jugadorId  the ID of the player for whom to initiate the cooldown
     * @return           void
     */
    public void iniciarCooldownAtrapar(UUID jugadorId) {
        cooldownatrapar.put(jugadorId, System.currentTimeMillis());
    }


    /**
     * Checks if the player is in cooldown for catching a Pokemon.
     *
     * @param  jugadorId  the UUID of the player
     * @return            true if the player is in cooldown, false otherwise
     */
    public boolean estaEnCooldownAtrapar(UUID jugadorId) {
        return cooldownatrapar.getIfPresent(jugadorId) != null;
    }


    /**
     * Registers a new ability for a player.
     *
     * @param  jugador    the UUID of the player
     * @param  habilidad  the ability to register
     */
    public void registrarHabilidad(UUID jugador, Habilidad habilidad) {
        habilidadesJugador.computeIfAbsent(jugador, k -> new ArrayList<>());

        List<Habilidad> habilidades = habilidadesJugador.get(jugador);

        if (!habilidades.contains(habilidad)) {
            habilidades.add(habilidad);
        }
    }

    /**
     * Get the instance of HabilidadesManager.
     *
     * @return the instance of HabilidadesManager
     */

    public static HabilidadesManager getInstance() {
        return SingletonHelper.INSTANCE;
    }


    /**
     * Retrieves the list of abilities for a given player.
     *
     * @param  jugador  the UUID of the player
     * @return          the list of abilities for the player, or an empty list if none are found
     */
    public List<Habilidad> getHabilidades(UUID jugador) {
        return habilidadesJugador.getOrDefault(jugador, Collections.emptyList());
    }


    /**
     * Retrieves all the abilities available.
     *
     * @return         	the list of all abilities
     */
    public static List<Habilidad> getTodasLasHabilidades() {
        List<Habilidad> todasLasHabilidades = new ArrayList<>();
        todasLasHabilidades.addAll(Arrays.asList(Fuerza.values()));
        todasLasHabilidades.addAll(Arrays.asList(Velocidad.values()));
        todasLasHabilidades.addAll(Arrays.asList(Regate.values()));
        todasLasHabilidades.addAll(Arrays.asList(Portero.values()));

        return todasLasHabilidades;
    }


    /**
     * Removes a specific ability from a player's list of abilities.
     *
     * @param  jugador   the UUID of the player
     * @param  habilidad the ability to be removed
     */
    public void eliminarHabilidad(UUID jugador, Habilidad habilidad) {
        habilidadesJugador.getOrDefault(jugador, Collections.emptyList()).remove(habilidad);
    }
    /**
     * Elimina todas las habilidades asociadas a un jugador.
     *
     * @param  jugador  el identificador del jugador
     */
    public void eliminarTodasLasHabilidades(UUID jugador) {
        habilidadesJugador.remove(jugador);
    }
    /**
     * Activates a specific ability for a player.
     *
     * @param  jugadorId   the ID of the player
     * @param  habilidad   the ability to activate
     */
    public void activarHabilidad(UUID jugadorId, Habilidad habilidad) {
        Map<Habilidad, Boolean> habilidadesJugador = habilidadesActivas.get(jugadorId);
        if (habilidadesJugador == null) {
            habilidadesJugador = new HashMap<>();
            habilidadesActivas.put(jugadorId, habilidadesJugador);
        } else {
            for (Map.Entry<Habilidad, Boolean> entry : habilidadesJugador.entrySet()) {
                if (entry.getValue()) {
                    entry.setValue(false);
                }
            }
        }
        habilidadesJugador.put(habilidad, true);
    }
    /**
     * Returns the active ability for a given player.
     *
     * @param  jugadorId  the ID of the player
     * @return            the active ability, or null if none is active
     */
    public Habilidad obtenerHabilidadActiva(UUID jugadorId) {
        Map<Habilidad, Boolean> habilidadesJugador = habilidadesActivas.get(jugadorId);
        if (habilidadesJugador != null) {
            for (Map.Entry<Habilidad, Boolean> entry : habilidadesJugador.entrySet()) {
                if (entry.getValue()) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
    /**
     * Checks if the given player is currently in cooldown.
     *
     * @param  jugadorId  the UUID of the player to check
     * @return            true if the player is in cooldown, false otherwise
     */
    public boolean estaEnCooldown(UUID jugadorId) {
        Long tiempoFinCooldown = cooldowns.getIfPresent(jugadorId);
        if (tiempoFinCooldown != null) {
            return System.currentTimeMillis() < tiempoFinCooldown;
        }
        return false;
    }
    /**
     * Obtains the ability of a given item.
     *
     * @param  item  the ItemStack to obtain the ability from
     * @return      the ability of the item, or null if none is found
     */
    public Habilidad obtenerHabilidadDeItem(ItemStack item) {
        Material material = item.getType();
        for (Habilidad habilidad : HabilidadesManager.getTodasLasHabilidades()) {
            if (habilidad.getMaterial() == material) {
                return habilidad;
            }
        }
        return null;
    }

    /**
     * Method to deactivate a specific ability for a player.
     *
     * @param  jugadorId  the UUID of the player
     * @param  habilidad  the ability to deactivate
     */
    public void desactivarHabilidad(UUID jugadorId, Habilidad habilidad) {
        Map<Habilidad, Boolean> habilidadesJugador = habilidadesActivas.get(jugadorId);
        if (habilidadesJugador != null) {
            habilidadesJugador.put(habilidad, false);
        }
    }
}
