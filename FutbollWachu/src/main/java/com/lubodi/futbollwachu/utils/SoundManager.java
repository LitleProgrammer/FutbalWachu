package com.lubodi.futbollwachu.utils;

import com.lubodi.futbollwachu.Instance.Arena;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {

    public void ballHitSound(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 0.2f, 1f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 0.6f, 1f);
    }

    public void scoreGoalSound(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
    }

    public void countDownSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
    }

}
