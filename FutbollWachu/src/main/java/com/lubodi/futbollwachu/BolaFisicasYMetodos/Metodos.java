package com.lubodi.futbollwachu.BolaFisicasYMetodos;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


import com.lubodi.futbollwachu.FutballBola;





public class Metodos implements Listener {


    private final FutballBola plugin;
    private final Fisicas fisicas;
    public Metodos(FutballBola plugin, Fisicas fisicas) {
        this.plugin = plugin;
       this.fisicas = fisicas;
    }
    /**
     * Spawns a Silverfish at the specified location and applies various effects and settings to it.
     *
     * @param location the location where the Silverfish will be spawned
     * @return
     */
    public Silverfish spawnSilverfishAtLocation(Location location) {
        if (location.getWorld() == null) {
            throw new IllegalArgumentException("Location world cannot be null");
        }

        Silverfish silverfish = (Silverfish) location.getWorld().spawnEntity(location, EntityType.SILVERFISH);

        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9000, 200));
        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9000, 200));
        silverfish.setCustomName("Bola");
        silverfish.setCustomNameVisible(true);
        silverfish.setSilent(true);
        fisicas.agregarSilverfishVelocidadCaida(silverfish);
        fisicas.agregarSilverfishVelocidadVertical(silverfish);

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                fisicas.aplicarFriccion(silverfish);
                fisicas.calcularFallVelocity(silverfish);
                fisicas.calcularVelocidadHorizontal(silverfish);
                fisicas.detectarColisionYAplicarRebote(silverfish);
                fisicas.aplicarRebote(silverfish);
            }
        };
        task.runTaskTimer(plugin, 0, 4);
        fisicas.agregarTask(silverfish, task);
        return silverfish;
    }
    /**
     * Removes the entity if it has a custom name "Bola".
     *
     * @param  entity   the entity to be checked and possibly removed
     */
    public void matarBola(Entity entity) {
        if (entity.getCustomName() != null && entity.getCustomName().equals("Bola")) {
            entity.remove();
        }
    }

    /**
     * Handles the entity target event and cancels it if the target is a Silverfish with
     * the custom name "Bola".
     *
     * @param  event  the entity target event
     * @return       void
     */

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntityType() == EntityType.SILVERFISH && event.getTarget() != null) {
            Silverfish silverfish = (Silverfish) event.getEntity();
            if ("Bola".equals(silverfish.getCustomName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.SILVERFISH && event.getEntity().getCustomName() != null) {
            if ("Bola".equals(event.getEntity().getCustomName())) {
                fisicas.quitarTask(event.getEntity());
                fisicas.quitarSilverfishVelocidadCaida(event.getEntity());
                fisicas.quitarAgregarSilverfishVelocidadVertical(event.getEntity());
            }
        }
    }

    /**
     * onEntityDeath method is triggered when an entity dies. It checks if the
     * entity is a SILVERFISH with a custom name "Bola" and performs some
     * actions if the condition is met.
     *
     * @param  event  the EntityDeathEvent that triggered the method
     * @return       void, no return value
     */

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.SILVERFISH && event.getEntity().getCustomName() != null) {
            if ("Bola".equals(event.getEntity().getCustomName())) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
