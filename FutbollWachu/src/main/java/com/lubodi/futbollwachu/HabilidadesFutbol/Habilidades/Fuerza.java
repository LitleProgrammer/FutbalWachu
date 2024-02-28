package com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades;


import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public enum Fuerza implements Habilidad {
    Fuerza10(ChatColor.RED + "Fuerza 10", Material.ORANGE_WOOL, 10, 15, "Fuerza"),  // Cambiado "Fuerza 10" a "Tiro"
    FUERZA20(ChatColor.RED+"Fuerza 20", Material.ORANGE_CONCRETE_POWDER, 80, 50, "Fuerza"),
    FUERZA30(ChatColor.RED +"Fuerza 30", Material.ORANGE_CONCRETE, 30, 100, "Fuerza");

    private String nombre;
    private Material material;
    private int cooldown;
    private double potencia;
    private String etiqueta;  // Nuevo campo

    Fuerza(String nombre, Material material, int cooldown, double potencia, String etiqueta) {
        this.nombre = nombre;
        this.material = material;
        this.cooldown = cooldown;
        this.potencia = potencia;
        this.etiqueta = etiqueta;  // Inicializar el nuevo campo
    }

    public double getPotencia() {
        return potencia;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }
    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }


    /**
     * Method to use a UUID to perform various actions on a player in the game world, such as playing sounds,
     * spawning particles, and interacting with nearby entities.
     *
     * @param  uuid  the UUID of the player to perform the actions on
     * @return       void, no return value
     */
    @Override
    public void usar(UUID uuid) {
        Player jugador = Bukkit.getOfflinePlayer(uuid).getPlayer();
        if (jugador != null) {
            Location jugadorLocation = jugador.getLocation();
            World world = jugador.getWorld();
            world.playSound(jugadorLocation, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
            world.spawnParticle(Particle.REDSTONE,
                    jugadorLocation,
                    100,
                    1, 1, 1,
                    1,
                    new Particle.DustOptions(
                            Color.fromRGB(255, 0, 0), // Color RGB (verde)
                            1 // Opacidad
                    )
            );
            List<Silverfish> silverfishEntities = jugador.getNearbyEntities(5, 5, 5).stream()
                    .filter(entity -> entity instanceof Silverfish)
                    .map(entity -> (Silverfish) entity)
                    .collect(Collectors.toList());

            for (Silverfish silverfish : silverfishEntities) {
                if (silverfish.getCustomName().equals("Bola")) {
                    Vector direccion = jugadorLocation.getDirection();
                    direccion.multiply(this.potencia);

                    Vector finalVelocity = direccion.clone().add(new Vector(0, 0.5 * potencia, 0));
                    silverfish.setVelocity(finalVelocity);
                }
            }
        }
    }


}

