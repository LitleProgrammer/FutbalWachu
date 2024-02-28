package com.lubodi.futbollwachu.Listeners;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import com.lubodi.futbollwachu.team.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {
    private FutballBola minigame;
    public GameListener(FutballBola minigame){
        this.minigame = minigame;
    }
    /**
     * Handles the inventory click event, performing team selection logic.
     *
     * @param  event The InventoryClickEvent object representing the click event
     */ @EventHandler
    public void  onInventoryClick(InventoryClickEvent event){
        String titulo = event.getView().getTitle();
        Player player =  (Player) event.getWhoClicked();
        if(titulo.equals("Team Selection") && event.getInventory().getType() == InventoryType.CHEST && event.getCurrentItem() != null){
            event.setCancelled(true);
            if (event.isShiftClick()) {
                // El jugador ha hecho shift-click, así que cancelamos el evento y salimos del método.
                event.setCancelled(true);
                return;
            }

            Team team = Team.valueOf(event.getCurrentItem().getItemMeta().getLocalizedName());
            Arena arenas = minigame.getArenaManager().getArena(player);
            if(arenas != null){
                if(arenas.getTeam(player) == team){
                    player.sendMessage(ChatColor.RED + "Ya estas en este equipo");
                }else {
                    player.sendMessage(ChatColor.AQUA + " estas en el equipo " + team.getDisplay());
                    arenas.setTeam(player, team);
                }
            }
            player.closeInventory();
        }
    }

    /**
     * An event handler for when a Silverfish hits another entity.
     *
     * @param  event  the event representing the entity damage by another entity
     */
    @EventHandler
    public void onSilverfishHit(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Silverfish && event.getDamager() instanceof Player) {
            if(event.getEntity().getCustomName().equals("Bola")){
                Arena arenas = minigame.getArenaManager().getArena(((Player) event.getDamager()).getPlayer());
                arenas.setLastHitters(((Player) event.getDamager()).getPlayer());
            }


        }

    }
}
