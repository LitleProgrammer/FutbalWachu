package com.lubodi.futbollwachu.utils;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.Manager.Region;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ParticleSpawner {

    private FutballBola minigame;

    public ParticleSpawner() {
    }

    public void spawnParticle(Particle particle, Region region) {
        int minX = Math.min(region.getCorner1().getBlockX(), region.getCorner2().getBlockX());
        int minY = Math.min(region.getCorner1().getBlockY(), region.getCorner2().getBlockY());
        int minZ = Math.min(region.getCorner1().getBlockZ(), region.getCorner2().getBlockZ());
        int maxX = Math.max(region.getCorner1().getBlockX(), region.getCorner2().getBlockX());
        int maxY = Math.max(region.getCorner1().getBlockY(), region.getCorner2().getBlockY());
        int maxZ = Math.max(region.getCorner1().getBlockZ(), region.getCorner2().getBlockZ());

        World world = region.getCorner1().getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location particleLoc = new Location(world, x + 0.5, y + 0.5, z + 0.5); // Add 0.5 to center the particle
                    world.spawnParticle(particle, particleLoc, 3); // Spawn particle at the calculated location
                }
            }
        }
    }

    public void spawnParticle(Particle particle, Location location) {
        location.getWorld().spawnParticle(particle, location, 5, 0.2, 0.2, 0.2, 0.1);
    }
}
