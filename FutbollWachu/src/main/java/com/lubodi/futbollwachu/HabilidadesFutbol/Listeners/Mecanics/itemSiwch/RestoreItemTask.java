package com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.itemSiwch;

import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

 class RestoreItemTask implements Runnable {
    private final Player jugador;
    private final Habilidad habilidad;
    private final ItemStack itemReemplazo;
    private final HabilidadesManager habilidadManager;

     RestoreItemTask(Player jugador, Habilidad habilidad, ItemStack itemReemplazo, HabilidadesManager habilidadManager) {
        this.jugador = jugador;
        this.habilidad = habilidad;
        this.itemReemplazo = itemReemplazo;
        this.habilidadManager = habilidadManager;
    }

    @Override
    public void run() {
        Map<Habilidad, ItemStack> itemsOriginalesJugador = habilidadManager.itemsOriginales.get(jugador.getUniqueId());
        ItemStack itemOriginal = itemsOriginalesJugador.get(habilidad);

        if (itemOriginal != null) {
            PlayerInventory inventario = jugador.getInventory();
            inventario.setItem(inventario.first(itemReemplazo), itemOriginal);
            itemsOriginalesJugador.remove(habilidad);
            habilidadManager.itemsReemplazo.get(jugador.getUniqueId()).remove(habilidad);
        }
    }
}
