package com.lubodi.futbollwachu.HabilidadesFutbol.GUI;


import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HotbarHabilidades {
    private static HotbarHabilidades instance;
    /**
     * Returns the singleton instance of HotbarHabilidades, creating it if necessary.
     *
     * @return the singleton instance of HotbarHabilidades
     */
    public static HotbarHabilidades getInstance() {
        if (instance == null) {
            instance = new HotbarHabilidades();
        }
        return instance;
    }

    /**
     * Agrega una habilidad a la barra de acceso rápido de un jugador, si hay espacio disponible.
     *
     * @param  jugador    el jugador al que se le agregará la habilidad
     * @param  habilidad  la habilidad que se agregará a la barra de acceso rápido
     */
    public void agregarHabilidadALaHotbar(Player jugador, Habilidad habilidad) {
        // Busca un espacio vacío en la barra de acceso rápido del jugador.
        int espacioVacio = jugador.getInventory().firstEmpty();

        // Verifica si hay un espacio vacío en la barra de acceso rápido.
        if (espacioVacio >= 0 && espacioVacio <= 8) { // Los espacios 0-8 corresponden a la barra de acceso rápido.
            // Crea un ítem que represente la habilidad.
            ItemStack itemHabilidad = new ItemStack(habilidad.getMaterial());
            ItemMeta meta = itemHabilidad.getItemMeta();
            meta.setDisplayName(habilidad.getNombre());

            // Agrega la etiqueta de la habilidad como un lore del ítem.
            List<String> lore = new ArrayList<>();
            lore.add(habilidad.getEtiqueta());
            meta.setLore(lore);

            itemHabilidad.setItemMeta(meta);

            // Agrega el ítem a la barra de acceso rápido del jugador.
            jugador.getInventory().setItem(espacioVacio, itemHabilidad);
        } else {
            jugador.sendMessage("No tienes espacio en tu barra de acceso rápido.");
        }
    }
    /**
     * Obtiene la habilidad asociada a un ítem.
     *
     * @param  item	el ítem del cual se desea obtener la habilidad
     * @return         	la habilidad asociada al ítem, o null si no se encuentra ninguna
     */
    public Habilidad obtenerHabilidadPorItem(ItemStack item) {
        // Obtiene el nombre de la habilidad del ítem.
        String nombreHabilidad = item.getItemMeta().getDisplayName();

        // Busca la habilidad en todas las habilidades disponibles.
        for (Habilidad habilidad : HabilidadesManager.getTodasLasHabilidades()) {
            if (habilidad.getNombre().equals(nombreHabilidad)) {
                return habilidad; // Retorna la habilidad si se encuentra.
            }
        }

        return null; // Retorna null si no se encuentra ninguna habilidad con ese nombre.
    }

    /**
     * Checks if the given ItemStack is a valid ability item.
     *
     * @param  item  the ItemStack to check
     * @return      true if the ability item is found, false otherwise
     */
    public boolean esHabilidad(ItemStack item) {
        // Obtiene el nombre del ítem.
        String nombreItem = item.getItemMeta().getDisplayName();

        // Busca el nombre del ítem en todas las habilidades disponibles.
        for (Habilidad habilidad : HabilidadesManager.getTodasLasHabilidades()) {
            if (habilidad.getNombre().equals(nombreItem)) {
                return true; // Retorna true si se encuentra una habilidad con ese nombre.
            }
        }

        return false; // Retorna false si no se encuentra ninguna habilidad con ese nombre.
    }

    /**
     * Recorre los espacios de la barra de acceso rápido del jugador.
     *
     * @param  jugador   el jugador cuya barra se va a modificar
     */
    public void eliminarHabilidadesDeLaHotbar(Player jugador) {
        // Recorre los espacios de la barra de acceso rápido del jugador.
        for (int i = 0; i <= 8; i++) { // Los espacios 0-8 corresponden a la barra de acceso rápido.
            ItemStack item = jugador.getInventory().getItem(i);

            // Verifica si el ítem es una habilidad.
            if (item != null && esHabilidad(item)) {
                // Elimina el ítem de la barra de acceso rápido del jugador.
                jugador.getInventory().clear(i);
            }
        }
    }

}
