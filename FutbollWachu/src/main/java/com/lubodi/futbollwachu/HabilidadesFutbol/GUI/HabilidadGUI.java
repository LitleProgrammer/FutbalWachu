package com.lubodi.futbollwachu.HabilidadesFutbol.GUI;


import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Portero;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;



public class HabilidadGUI implements InventoryHolder, Listener {



    private FutballBola plugin;
    private Inventory inventario;
    private final HabilidadesManager manager;

    private HotbarHabilidades hotbarHabilidades;
    public HabilidadGUI(FutballBola plugin, HabilidadesManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.hotbarHabilidades = HotbarHabilidades.getInstance();
    }
    /**
     * SelectorDeHabilidades function to create inventory for selecting abilities.
     *
     * @param  player   the player for whom the inventory is being created
     * @return         void, no return value
     */
    public void SelectorDeHabilidades(Player player) {
        Arena arena = plugin.getArenaManager().getArena(player);
        this.inventario = Bukkit.createInventory(this, 18, "Selecciona tus habilidades");

        // Agrega las habilidades al inventario.
        for (Habilidad habilidad : HabilidadesManager.getTodasLasHabilidades()) {
            // Si la habilidad es de tipo Portero y el jugador no es portero, entonces continúa con la siguiente iteración
            if (habilidad instanceof Portero && !arena.isPlayerPortero(player)) {
                continue;
            }

            ItemStack item = new ItemStack(habilidad.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(habilidad.getNombre());
            item.setItemMeta(meta);

            this.inventario.addItem(item);
        }
    }
    /**
     * Open the inventory for a player, and perform necessary actions based on the player's current abilities.
     *
     * @param  jugador  the player for whom the inventory is being opened
     */
    public void abrir(Player jugador) {
        SelectorDeHabilidades(jugador);
        if (manager.getHabilidades(jugador.getUniqueId()).size() >= 5) {
            // Si el jugador ya tiene 4 habilidades, muestra un mensaje y no permite agregar más.
            manager.eliminarTodasLasHabilidades(jugador.getUniqueId());
            hotbarHabilidades.eliminarHabilidadesDeLaHotbar(jugador);
        }
        jugador.openInventory(this.inventario);
    }

    /**
     * Handle inventory click event and process player actions.
     *
     * @param  event  the InventoryClickEvent object
     * @return       void
     */

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player jugador = (Player) event.getWhoClicked();
        String titulo = event.getView().getTitle();

        if (event.isShiftClick()) {
            // El jugador ha hecho shift-click, así que cancelamos el evento y salimos del método.
            event.setCancelled(true);
            return;
        }

        if (titulo.equals("Selecciona tus habilidades") && event.getInventory().getType() == InventoryType.CHEST && event.getCurrentItem() != null) {
            ItemStack item = event.getCurrentItem();

            // Verifica si el ítem es una habilidad.
            if (hotbarHabilidades.esHabilidad(item)) {
                // Obtiene la habilidad correspondiente al ítem seleccionado.
                Habilidad habilidad = hotbarHabilidades.obtenerHabilidadPorItem(item);

                // Verifica si el jugador ya tiene esta habilidad.
                if (manager.getHabilidades(jugador.getUniqueId()).contains(habilidad)) {
                    jugador.sendMessage("Ya tienes esta habilidad.");
                    jugador.playSound(jugador.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0f, 1.0f);
                } else {
                    // Registra la habilidad para el jugador.
                    manager.registrarHabilidad(jugador.getUniqueId(), habilidad);
                    jugador.sendMessage("Has seleccionado la habilidad: " + habilidad.getNombre());
                    jugador.playSound(jugador.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 5.0f);
                    // Agrega la habilidad a la barra de acceso rápido del jugador.
                    hotbarHabilidades.agregarHabilidadALaHotbar(jugador, habilidad);
                }
            }

            // Cierra el inventario si el jugador ya ha seleccionado 4 habilidades.
            if (manager.getHabilidades(jugador.getUniqueId()).size() >= 5) {
                jugador.closeInventory();
            }

            event.setCancelled(true);
        }
    }
    /**
     * Retrieves the inventory.
     *
     * @return the inventory
     */
    @Override
    public Inventory getInventory() {
        return this.inventario;
    }
}
