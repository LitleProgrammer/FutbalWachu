package com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.DropItem;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class DropItemEvent implements Listener {
    private final HabilidadesManager habilidadManager;

    private final FutballBola plugin;

    public DropItemEvent(FutballBola plugin, HabilidadesManager habilidadManager) {
        this.plugin = plugin;
        this.habilidadManager = habilidadManager;
    }

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
