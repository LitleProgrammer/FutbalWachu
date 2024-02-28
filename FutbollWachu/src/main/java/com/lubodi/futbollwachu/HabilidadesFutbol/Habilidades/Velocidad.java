package com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades;


import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public enum Velocidad implements Habilidad {
    VELOCIDAD1(ChatColor.GREEN + "Velocidad 1", Material.GREEN_WOOL, 20, 30, 1, "Velocidad"),
    VELOCIDAD2(ChatColor.GREEN +"Velocidad 2", Material.GREEN_CONCRETE_POWDER, 20, 20, 2, "Velocidad"),
    VELOCIDAD3(ChatColor.GREEN +"Velocidad 3", Material.GREEN_CONCRETE, 30, 10, 3, "Velocidad");

    private String nombre;
    private Material material;
    private int cooldown;
    private int duracion; // Duración en segundos
    private int nivel;    // Nivel de velocidad
    private String etiqueta;

    Velocidad(String nombre, Material material, int cooldown, int duracion, int nivel, String etiqueta) {
        this.nombre = nombre;
        this.material = material;
        this.cooldown = cooldown;
        this.duracion = duracion;
        this.nivel = nivel;
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

    public int getDuracion() {
        return duracion;
    }

    public int getNivel() {
        return nivel;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }


    /**
     * Usar the given UUID to play sound and spawn particle effects.
     *
     * @param  uuid   the UUID of the player
     * @return        void
     */
    @Override
    public void usar(UUID uuid) {
        // Obtén el jugador a partir del UUID.
        Player jugador = Bukkit.getPlayer(uuid);

        Location jugadorLocation = jugador.getLocation();
        World world = jugador.getWorld();
        world.playSound(jugadorLocation, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
        world.spawnParticle(Particle.REDSTONE,
                jugadorLocation,
                100, // Cantidad de partículas
                1, 1, 1, // Desplazamiento en X, Y y Z
                1, // Tamaño de la partícula
                new Particle.DustOptions(
                        Color.fromRGB(0, 255, 0), // Color RGB (verde)
                        1 // Opacidad
                )
        );
        // Comprueba si el jugador existe (es decir, está en línea).
        {
            // Crea un objeto PotionEffectType para el efecto de velocidad.

            // Define la duración del efecto en ticks (1 segundo = 20 ticks).
            int duracion = this.duracion * 20;

            // Define el nivel del efecto. En este caso, usamos el campo 'nivel' de la habilidad.
            int nivelEfecto = this.nivel - 1; // Los niveles de efecto en Minecraft empiezan desde 0.

            // Crea un objeto PotionEffect con el tipo, la duración y el nivel del efecto.
            PotionEffect efectoVelocidad = new PotionEffect(PotionEffectType.SPEED, duracion, nivelEfecto);

            // Aplica el efecto al jugador.
            jugador.addPotionEffect(efectoVelocidad);
        }
    }
}
