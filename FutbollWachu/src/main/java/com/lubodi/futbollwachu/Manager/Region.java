package com.lubodi.futbollwachu.Manager;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;


public class Region {
    private Location corner1;
    private Location corner2;

    public Region(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }
    /**
     * Returns the corner1 location.
     *
     * @return         	the corner1 location
     */
    public Location getCorner1() {
        return corner1;
    }
    /**
     * Returns the corner2 Location.
     *
     * @return  the corner2 Location
     */
    public Location getCorner2() {
        return corner2;
    }
    /**
     * Checks if the given entity is within the bounding box defined by corner1 and corner2.
     *
     * @param  entity   the entity to check
     * @return          true if the entity is within the bounding box, otherwise false
     */
    public boolean contains(Entity entity) {
        return entity.getLocation().getX() >= Math.min(corner1.getX(), corner2.getX()) &&
                entity.getLocation().getX() <= Math.max(corner1.getX(), corner2.getX()) &&
                entity.getLocation().getY() >= Math.min(corner1.getY(), corner2.getY()) &&
                entity.getLocation().getY() <= Math.max(corner1.getY(), corner2.getY()) &&
                entity.getLocation().getZ() >= Math.min(corner1.getZ(), corner2.getZ()) &&
                entity.getLocation().getZ() <= Math.max(corner1.getZ(), corner2.getZ());
    }


    /**
     * Gets the relative location of the player within the defined area.
     *
     * @param  player	the player for which to calculate the relative location
     * @return         	the relative location of the player, or null if the player is not within the defined area
     */
    public Location getRelativeLocation(Player player) {
        if (contains(player)) {
            double relativeX = (player.getLocation().getX() - Math.min(corner1.getX(), corner2.getX())) / (Math.max(corner1.getX(), corner2.getX()) - Math.min(corner1.getX(), corner2.getX()));
            double relativeY = (player.getLocation().getY() - Math.min(corner1.getY(), corner2.getY())) / (Math.max(corner1.getY(), corner2.getY()) - Math.min(corner1.getY(), corner2.getY()));
            double relativeZ = (player.getLocation().getZ() - Math.min(corner1.getZ(), corner2.getZ())) / (Math.max(corner1.getZ(), corner2.getZ()) - Math.min(corner1.getZ(), corner2.getZ()));

            return new Location(player.getWorld(), relativeX, relativeY, relativeZ);
        } else {
            return null;
        }
    }
    /**
     * Gets the relative location of the player within the defined area.
     *
     * @param  player	the player for which to calculate the relative location
     * @return         	the relative location of the player, or null if the player is not within the defined area
     */
    public double[][] getDistancesToCorners(Player player) {
        double distanceToCorner1X = Math.abs(player.getLocation().getX() - corner1.getX()) / (Math.abs(corner1.getX() - corner2.getX()) + 0.0001);
        double distanceToCorner1Y = Math.abs(player.getLocation().getY() - corner1.getY()) / (Math.abs(corner1.getY() - corner2.getY()) + 0.0001);
        double distanceToCorner1Z = Math.abs(player.getLocation().getZ() - corner1.getZ()) / (Math.abs(corner1.getZ() - corner2.getZ()) + 0.0001);

        double distanceToCorner2X = Math.abs(player.getLocation().getX() - corner2.getX()) / (Math.abs(corner1.getX() - corner2.getX()) + 0.0001);
        double distanceToCorner2Y = Math.abs(player.getLocation().getY() - corner2.getY()) / (Math.abs(corner1.getY() - corner2.getY()) + 0.0001);
        double distanceToCorner2Z = Math.abs(player.getLocation().getZ() - corner2.getZ()) / (Math.abs(corner1.getZ() - corner2.getZ()) + 0.0001);

        return new double[][] {{distanceToCorner1X, distanceToCorner1Y, distanceToCorner1Z}, {distanceToCorner2X, distanceToCorner2Y, distanceToCorner2Z}};
    }

    /**
        Uses math to split the current region in half.
        It always splits the long side, so if x would be 30 blocks and z be 10 blocks long.
        After splitting x will be 15 blocks long
        @return returns two new regions as an array
     */
    public Region[] splitInHalf() {
        double lengthX = Math.abs(corner1.getX() - corner2.getX());
        double lengthZ = Math.abs(corner1.getZ() - corner2.getZ());

        boolean splitAlongX = lengthX > lengthZ;

        Location newCorner1, newCorner2;

        if (splitAlongX) {
            newCorner1 = new Location(corner1.getWorld(), (corner1.getX() + corner2.getX()) / 2, corner2.getY(), corner2.getZ());
            newCorner2 = new Location(corner1.getWorld(), (corner1.getX() + corner2.getX()) / 2, corner1.getY(), corner1.getZ());
        } else {
            newCorner1 = new Location(corner1.getWorld(), corner2.getX(), corner2.getY(), (corner1.getZ() + corner2.getZ()) / 2);
            newCorner2 = new Location(corner1.getWorld(), corner1.getX(), corner1.getY(), (corner1.getZ() + corner2.getZ()) / 2);
        }

        return new Region[]{new Region(corner1, newCorner1), new Region(corner2, newCorner2)};
    }

    public Location getMiddle() {
        double x, y, z;
        x = (corner1.getX() + corner2.getX()) / 2;
        y = (corner1.getY() + corner2.getY()) / 2;
        z = (corner1.getZ() + corner2.getZ()) / 2;

        return new Location(corner1.getWorld(), x, y, z);
    }


    public Set<Location> getEsquinas() {
        Set<Location> esquinas = new HashSet<>();
        esquinas.add(corner1);
        esquinas.add(new Location(corner1.getWorld(), corner1.getX(), corner1.getY(), corner2.getZ()));
        esquinas.add(new Location(corner1.getWorld(), corner2.getX(), corner1.getY(), corner1.getZ()));
        esquinas.add(corner2);
        return esquinas;
    }

    public enum ZoneType {
        GENERAL,
        EXTERIOR,
        HORIZONTAL_SUPERIOR,
        HORIZONTAL_INFERIOR,
        VERTICAL_RIGHT,
        VERTICAL_LEFT,
        VERTICAL
    }

}
