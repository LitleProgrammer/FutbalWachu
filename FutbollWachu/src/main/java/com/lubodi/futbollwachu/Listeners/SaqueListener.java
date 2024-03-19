package com.lubodi.futbollwachu.Listeners;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Instance.mecanicas.AdministradorDeSaques;
import com.lubodi.futbollwachu.Instance.mecanicas.MecanicasSaque;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class SaqueListener implements Listener {

    private FutballBola minigame;
    public SaqueListener(FutballBola minigame){
        this.minigame = minigame;

    };


    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player jugador = event.getPlayer();
        Entity entidad = event.getRightClicked();
        if (entidad instanceof Silverfish && entidad.getCustomName() != null && entidad.getCustomName().equals("Bola"));{
            Arena arena = minigame.getArenaManager().getArena(jugador);
            AdministradorDeSaques administradorDeSaques = arena.getAdministradorDeSaques();
            MecanicasSaque mecanicasSaque = administradorDeSaques.getMecanicasSaque();
            if(mecanicasSaque.isSaqueVigente()){
                if(mecanicasSaque.isSaqueEnProgreso()){
                    entidad.setVelocity(new Vector(0, 0, 0));
                    event.setCancelled(true);
                    return;
                }
                if (!mecanicasSaque.isSaqueEnProgreso() && !jugador.getUniqueId().equals(mecanicasSaque.getCobradorDeTiro())) {

                    entidad.setVelocity(new Vector(0, 0, 0));
                    event.setCancelled(true);

                }
                else if(jugador.getUniqueId().equals(mecanicasSaque.getCobradorDeTiro())){
                    mecanicasSaque.setCollidable(true);
                    mecanicasSaque.setSaqueRealizado(true);
            }
            }
        }
    }
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!(damager instanceof Player) || !(entity instanceof Silverfish)) {
            return;
        }

        Player player = (Player) damager;
        Silverfish silverfish = (Silverfish) entity;

        if (silverfish.getCustomName() == null || !silverfish.getCustomName().equals("Bola")) {
            return;
        }

        Arena arena = minigame.getArenaManager().getArena(player);
        AdministradorDeSaques administradorDeSaques = arena.getAdministradorDeSaques();
        MecanicasSaque mecanicasSaque = administradorDeSaques.getMecanicasSaque();
        if(mecanicasSaque.isSaqueVigente()) {

            if (mecanicasSaque.isSaqueEnProgreso()) {

                event.setCancelled(true);
                return;
            }
            if (!mecanicasSaque.isSaqueEnProgreso() && !player.getUniqueId().equals(mecanicasSaque.getCobradorDeTiro())) {
                event.setCancelled(true);

            }
            else if (player.getUniqueId().equals(mecanicasSaque.getCobradorDeTiro())) {
                mecanicasSaque.setCollidable(true);
                mecanicasSaque.setSaqueRealizado(true);
            }
        }

    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Arena arena = minigame.getArenaManager().getArena(event.getPlayer());
        if (arena == null) return;

        AdministradorDeSaques administradorDeSaques = arena.getAdministradorDeSaques();
        MecanicasSaque mecanicasSaque = administradorDeSaques.getMecanicasSaque();


        if(mecanicasSaque.isSaqueVigente()){
            if (mecanicasSaque.getCobradorDeTiro() != null && event.getPlayer().getUniqueId().equals(mecanicasSaque.getCobradorDeTiro())) {
                Location ubicacionInicial = administradorDeSaques.encontrarUbicacionParaSaqueDeBanda(mecanicasSaque.obtenerSilverFish());
                Location to = event.getTo();
                if(to == null) return;

                // Obtener diferencias en las coordenadas X y Z
                double diffX = Math.abs(ubicacionInicial.getX() - to.getX());
                double diffZ = Math.abs(ubicacionInicial.getZ() - to.getZ());

                // Evaluamos si el movimiento es primariamente hacia adelante/atras (eje Z)
                if (diffZ > diffX) {
                    // Si el movimiento es hacia adelante o hacia atrás en más de 0, lo consideramos significativo, y teleportamos de vuelta
                    if (diffZ > 0) {
                        event.getPlayer().teleport(ubicacionInicial);
                    }
                } else {
                    // Si el movimiento es lateral (eje X), permitimos hasta 5 pasos de movimiento
                    if (diffX > 2) {
                        event.getPlayer().teleport(ubicacionInicial);
                    }
                }
            }
        }



    }
    }

