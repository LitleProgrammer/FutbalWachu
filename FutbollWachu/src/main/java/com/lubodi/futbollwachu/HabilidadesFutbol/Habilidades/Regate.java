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

public enum Regate implements Habilidad {
    REGATE_IZQUIERDA(ChatColor.YELLOW + "Regate Izquierda", Material.YELLOW_CONCRETE, 5, "Regate"),
    REGATE_DERECHA(ChatColor.YELLOW + "Regate Derecha", Material.YELLOW_CONCRETE_POWDER, 5, "Regate"),
    REGATE_ARRIBA(ChatColor.YELLOW + "Regate Arriba", Material.YELLOW_WOOL, 10, "Regate");

    private String nombre;
    private Material material;
    private int cooldown;
    private String etiqueta;

    Regate(String nombre, Material material, int cooldown, String etiqueta) {
        this.nombre = nombre;
        this.material = material;
        this.cooldown = cooldown;
        this.etiqueta = etiqueta;
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

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }


    /**
     * Method to use a UUID to perform various actions such as playing sounds and spawning particles.
     *
     * @param  uuid  the UUID of the player
     */
    @Override
    public void usar(UUID uuid) {
        Player jugador = Bukkit.getPlayer(uuid);
        Location jugadorLocations = jugador.getLocation();
        World world = jugador.getWorld();
        world.playSound(jugadorLocations, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
        world.spawnParticle(Particle.REDSTONE,
                jugadorLocations,
                100, // Cantidad de partículas
                1, 1, 1, // Desplazamiento en X, Y y Z
                1, // Tamaño de la partícula
                new Particle.DustOptions(
                        Color.fromRGB(255, 255, 0), // Color RGB (verde)
                        1 // Opacidad
                )
        );
        List<Silverfish> silverfishEntities = jugador.getNearbyEntities(5, 5, 5).stream()
                .filter(entity -> entity instanceof Silverfish)
                .map(entity -> (Silverfish) entity)
                .collect(Collectors.toList());

        for (Silverfish silverfish : silverfishEntities) {
            if (silverfish.getCustomName().equals("Bola")) {
                Location jugadorLocation = jugador.getLocation();
                Vector direccion = jugadorLocation.getDirection();


                switch (this) {
                    case REGATE_IZQUIERDA:
                        // Calcula un vector perpendicular a la dirección del jugador.
                        Vector izquierda = new Vector(-direccion.getZ(), 0, direccion.getX());

                        // Aplica los movimientos.
                        silverfish.setVelocity(izquierda.multiply(2));
                        silverfish.teleport(silverfish.getLocation().add(izquierda));
                        silverfish.setVelocity(izquierda);
                        break;

                    case REGATE_DERECHA:
                        // Calcula un vector perpendicular a la dirección del jugador.
                        Vector derecha = new Vector(direccion.getZ(), 0, -direccion.getX());

                        // Aplica los movimientos.
                        silverfish.setVelocity(derecha.multiply(2));
                        silverfish.teleport(silverfish.getLocation().add(derecha));
                        silverfish.setVelocity(derecha);
                        break;

                    case REGATE_ARRIBA:
                        double velocidad = 3; // La magnitud de la velocidad.
                        double angulo = Math.toRadians(60); // Convierte el ángulo a radianes.
                        Vector direccionHorizontal = new Vector(direccion.getX(), 0, direccion.getZ()).normalize(); // Ignora la componente vertical de la dirección del jugador.
                        Vector arriba = direccionHorizontal.multiply(velocidad * Math.cos(angulo)).add(new Vector(0, velocidad * Math.sin(angulo), 0));

                        // Aplica los movimientos.
                        silverfish.setVelocity(arriba);
                        silverfish.teleport(silverfish.getLocation().add(arriba));
                        silverfish.setVelocity(arriba);

                }
            }
        }
    }
}

