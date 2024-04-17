package com.lubodi.futbollwachu.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorManager {

    public void setArmor(ChatColor color, Player player) {
        ItemStack leatherHelmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack leatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leatherLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) leatherHelmet.getItemMeta();
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) leatherChestplate.getItemMeta();
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leatherLeggings.getItemMeta();
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) leatherBoots.getItemMeta();

        Color rgbColor = Color.fromRGB(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue());

        helmetMeta.setColor(rgbColor);
        chestplateMeta.setColor(rgbColor);
        leggingsMeta.setColor(rgbColor);
        bootsMeta.setColor(rgbColor);

        helmetMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        chestplateMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        leggingsMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        bootsMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);

        leatherHelmet.setItemMeta(helmetMeta);
        leatherChestplate.setItemMeta(chestplateMeta);
        leatherLeggings.setItemMeta(leggingsMeta);
        leatherBoots.setItemMeta(bootsMeta);

        player.getInventory().setHelmet(leatherHelmet);
        player.getInventory().setChestplate(leatherChestplate);
        player.getInventory().setLeggings(leatherLeggings);
        player.getInventory().setBoots(leatherBoots);
    }

    public void removeArmor(Player player) {
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

}
