package com.lubodi.futbollwachu.HabilidadesFutbol.Listeners;


import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Portero;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Velocidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.Instance.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class HabilidadHandClickListener implements Listener {
    private final FutballBola plugin;
    private final HabilidadesManager habilidadManager;

    public HabilidadHandClickListener(FutballBola plugin, HabilidadesManager habilidadManager) {
        this.plugin = plugin;
        this.habilidadManager = habilidadManager;
    }

    /**
     * A method that handles the PlayerInteractEvent for a specific ability.
     *
     * @param  event   the PlayerInteractEvent to handle
     */
    @EventHandler
    public void onHabilidadHandClick(PlayerInteractEvent event) {
        Player jugador = event.getPlayer();
        ItemStack itemEnMano = jugador.getInventory().getItemInMainHand();

        // Verifica si el jugador tiene un ítem en la mano.
        if (itemEnMano == null) {
            return;
        }

            // Obtén la habilidad asociada al ítem en la mano del jugador.
        Habilidad habilidad = habilidadManager.obtenerHabilidadDeItem(itemEnMano);

        if (habilidad instanceof Portero) {
            Arena arena = plugin.getArenaManager().getArena(jugador);

            if (!arena.getPorteroOfZona(jugador)) {
                jugador.sendMessage("Debes estar en tu zona de portería para usar esta habilidad");
                return;
            }
        }
            // Verifica si el ítem tiene meta y lore.
        if (!itemEnMano.hasItemMeta() || !itemEnMano.getItemMeta().hasLore() || habilidad == null) {
            return;
        }

                    event.setCancelled(true);
                    if (!habilidadManager.estaEnCooldown(jugador.getUniqueId())) {
                        habilidadManager.activarHabilidad(jugador.getUniqueId(), habilidad);
                        jugador.playSound(jugador.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                        jugador.sendMessage(ChatColor.GREEN + "Selecionaste la habilidad" + habilidad.getNombre());
                        // Guarda el ítem original y el ítem de reemplazo.
                        Map<Habilidad, ItemStack> itemsOriginalesJugador = habilidadManager.itemsOriginales.computeIfAbsent(jugador.getUniqueId(), k -> new HashMap<>());
                        itemsOriginalesJugador.put(habilidad, itemEnMano);
                        ItemStack itemReemplazo = new ItemStack(Material.GRAY_DYE);
                        Map<Habilidad, ItemStack> itemsReemplazoJugador = habilidadManager.itemsReemplazo.computeIfAbsent(jugador.getUniqueId(), k -> new HashMap<>());
                        itemsReemplazoJugador.put(habilidad, itemReemplazo);

                        jugador.getInventory().setItemInMainHand(itemReemplazo);


                        // Programa una tarea para restaurar el ítem original después del cooldown.
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {

                            if (itemsOriginalesJugador != null && itemsReemplazoJugador != null) {

                                ItemStack itemOriginal = itemsOriginalesJugador.get(habilidad);


                                if (itemOriginal != null && itemReemplazo != null) {

                                    PlayerInventory inventario = jugador.getInventory();

                                    for (int i = 0; i < inventario.getSize(); i++) {

                                        ItemStack item = inventario.getItem(i);

                                        if (item != null && item.equals(itemReemplazo)) {
                                            inventario.setItem(i, itemOriginal);

                                            itemsOriginalesJugador.remove(habilidad);
                                            itemsReemplazoJugador.remove(habilidad);

                                            break;
                                        }
                                    }
                                }
                            }

                        }, habilidad.getCooldown() * 20L);

                    }


                            // El ítem tiene la etiqueta "Velocidad", por lo que realizas la acción deseada.
                            Habilidad habilidadActiva = habilidadManager.obtenerHabilidadActiva(jugador.getUniqueId());
                            if (habilidadActiva != null && !habilidadManager.estaEnCooldown(jugador.getUniqueId()) && habilidadActiva instanceof Velocidad) {
                                habilidadActiva.usar(jugador.getUniqueId());
                                habilidadManager.desactivarHabilidad(jugador.getUniqueId(), habilidadActiva);
                            }else if (habilidadActiva != null && !habilidadManager.estaEnCooldown(jugador.getUniqueId()) && habilidadActiva instanceof Portero) {
                                habilidadActiva.usar(jugador.getUniqueId());
                                habilidadManager.desactivarHabilidad(jugador.getUniqueId(), habilidadActiva);
                            }
                           }







    /**
     * Handle the event of a player clicking on a Silverfish.
     *
     * @param  event   the EntityDamageByEntityEvent representing the event
     * @return         void, no return value
     */
    @EventHandler
    public void onSilverfishClickleft(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Silverfish) {
            Player jugador = (Player) event.getDamager();
            Silverfish silverfish = (Silverfish) event.getEntity();

            if (silverfish.getCustomName() != null && silverfish.getCustomName().equals("Bola")) {
                // Verifica si el ítem en la mano del jugador tiene lore (etiquetas).



                    Habilidad habilidad = habilidadManager.obtenerHabilidadActiva(jugador.getUniqueId());

                    if (habilidad != null && !habilidadManager.estaEnCooldown(jugador.getUniqueId())) {
                        habilidad.usar(jugador.getUniqueId());
                        habilidadManager.desactivarHabilidad(jugador.getUniqueId(), habilidad);
                    }
                }
            }
        }

    /**
     * Handle the event of a player interacting with a Silverfish entity.
     *
     * @param  event   the PlayerInteractEntityEvent triggering the function
     * @return         void, no return value
     */
    @EventHandler
    public void onSilverfishClick(PlayerInteractEntityEvent event) {
        Player jugador = event.getPlayer();
        Entity entidad = event.getRightClicked();
        Habilidad habilidad = habilidadManager.obtenerHabilidadActiva(jugador.getUniqueId());

        // Verifica si la entidad es un Silverfish.
        if (entidad instanceof Silverfish && entidad.getCustomName() != null && entidad.getCustomName().equals("Bola")) {
            Silverfish silverfish = (Silverfish) entidad;
            Arena arena = plugin.getArenaManager().getArena(jugador);



            if (habilidad != null && !habilidadManager.estaEnCooldown(jugador.getUniqueId())) {
                habilidad.usar(jugador.getUniqueId());
                habilidadManager.desactivarHabilidad(jugador.getUniqueId(), habilidad);
                return;
            }
            if (arena.isPlayerPortero(jugador) && arena.getPorteroOfZona(jugador)) {
                if (habilidadManager.estaEnCooldownAtrapar(jugador.getUniqueId())) {
                    // El jugador está en cooldown para atrapar la bola, no hace nada.
                    jugador.sendMessage("No puedes atrapar la bola ahora.");
                    return;
                }

                jugador.addPassenger(silverfish);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // Libera la bola después del período de tiempo.
                    jugador.eject();

                    // Inicia el cooldown para atrapar la bola.
                    habilidadManager.iniciarCooldownAtrapar(jugador.getUniqueId());
                }, 5L * 20);  // 10 segundos

            }else{
                // Mueve el Silverfish un bloque en la dirección en la que el jugador mira.
                Vector dirrection = jugador.getLocation().getDirection();
                silverfish.setVelocity(dirrection.multiply(1.5));
            }
        }
    }
    /**
     * Handles the PlayerToggleSneakEvent.
     *
     * @param  event   the PlayerToggleSneakEvent to handle
     * @return        void
     */
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking() && player.getPassenger() instanceof Silverfish) {

            // Libera la bola.
            player.eject();

            // Inicia el cooldown para atrapar la bola.
            habilidadManager.iniciarCooldownAtrapar(player.getUniqueId());
        }
    }

    /**
     * Verifica si el item que se está tirando es un item de habilidad.
     *
     * @param  event   the PlayerDropItemEvent
     * @return        void
     */

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // Verifica si el item que se está tirando es un item de habilidad.
        ItemStack itemTirado = event.getItemDrop().getItemStack();
        if (habilidadManager.obtenerHabilidadDeItem(itemTirado) != null || itemTirado.getType() == Material.GRAY_DYE) {
            // Cancela el evento para evitar que el item se tire al suelo.
            event.setCancelled(true);
        }
    }
}
