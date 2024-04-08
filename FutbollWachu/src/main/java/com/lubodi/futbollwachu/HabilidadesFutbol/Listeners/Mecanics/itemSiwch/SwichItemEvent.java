package com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.itemSiwch;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Portero;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Velocidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.itemSiwch.RestoreItemTask;
import com.lubodi.futbollwachu.Instance.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SwichItemEvent implements Listener {

    private final FutballBola plugin;
    private final HabilidadesManager habilidadManager;

    public SwichItemEvent(FutballBola plugin, HabilidadesManager habilidadManager) {
        this.plugin = plugin;
        this.habilidadManager = habilidadManager;
    }


    @EventHandler
    public void onHabilidadHandClick(PlayerInteractEvent event) {
        Player jugador = event.getPlayer();
        ItemStack itemEnMano = jugador.getInventory().getItemInMainHand();

        if (itemEnMano.hasItemMeta() && itemEnMano.getItemMeta().hasLore()) {
            Habilidad habilidad = habilidadManager.obtenerHabilidadDeItem(itemEnMano);
            if (habilidad != null && (habilidad instanceof Portero && canUseGoalkeeper(jugador))) {
                manejarActivacionHabilidad(jugador, habilidad);
                event.setCancelled(true);
                return;
            }
            if((habilidad instanceof Velocidad)) {
                manejarActivacionHabilidad(jugador, habilidad);
                event.setCancelled(true);
            }
        }
    }

    private boolean canUseGoalkeeper(Player player) {
        Arena arena = plugin.getArenaManager().getArena(player);
        if (!arena.getPorteroOfZona(player)) {
            player.sendMessage(ChatColor.RED + "solo puedes usar esta habilidad dentro del area");
            return false;
        }

        return true;
    }

    private void notificarActivacionHabilidad(Player jugador, Habilidad habilidad) {
        jugador.playSound(jugador.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        jugador.sendMessage(ChatColor.GREEN + "Seleccionaste la habilidad " + habilidad.getNombre());
    }

    private void manejarActivacionHabilidad(Player jugador, Habilidad habilidad) {
        if (!habilidadManager.estaEnCooldown(jugador.getUniqueId())) {
            notificarActivacionHabilidad(jugador, habilidad);
            usarHabilidad(jugador, habilidad);
            habilidadManager.activarHabilidad(jugador.getUniqueId(), habilidad);
            reemplazarItemYProgramarRestauracion(jugador, habilidad);
        }
    }

    private void usarHabilidad(Player jugador, Habilidad habilidad) {
        habilidad.usar(jugador.getUniqueId());
        habilidadManager.desactivarHabilidad(jugador.getUniqueId(), habilidad);
    }


    private void reemplazarItemYProgramarRestauracion(Player jugador, Habilidad habilidad) {
        ItemStack itemEnMano = jugador.getInventory().getItemInMainHand();
        ItemStack itemReemplazo = new ItemStack(Material.GRAY_DYE);

        Map<Habilidad, ItemStack> itemsOriginalesJugador =
                habilidadManager.itemsOriginales.computeIfAbsent(jugador.getUniqueId(), k -> new HashMap<>());
        itemsOriginalesJugador.put(habilidad, itemEnMano);

        Map<Habilidad, ItemStack> itemsReemplazoJugador =
                habilidadManager.itemsReemplazo.computeIfAbsent(jugador.getUniqueId(), k -> new HashMap<>());
        itemsReemplazoJugador.put(habilidad, itemReemplazo);

        jugador.getInventory().setItemInMainHand(itemReemplazo);

        Bukkit.getScheduler().runTaskLater(plugin, new RestoreItemTask(jugador, habilidad, itemReemplazo, habilidadManager), habilidad.getCooldown() * 20L);
    }
}
