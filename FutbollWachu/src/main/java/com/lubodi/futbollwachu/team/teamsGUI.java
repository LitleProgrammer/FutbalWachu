package com.lubodi.futbollwachu.team;



;
import com.lubodi.futbollwachu.Instance.Arena;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class teamsGUI implements Listener {
    public teamsGUI(Arena arena, Player player){
        Inventory gui = Bukkit.createInventory(null, 9, "Team Selection");

        for (Team team: Team.values()){
            ItemStack item = new ItemStack(team.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(team.getDisplay() + " " + ChatColor.GRAY + "(" + arena.getTeamCount(team) + "jugadores");
            meta.setLocalizedName(team.name());
            item.setItemMeta(meta);
            gui.addItem(item);
        }
        player.openInventory(gui);
    }

}
