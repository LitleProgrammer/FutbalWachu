package com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces;

import org.bukkit.Material;

import java.util.UUID;

public interface Habilidad {

    /**
     * Retrieves the nombre attribute.
     *
     * @return the nombre attribute
     */
    String getNombre();

    /**
     * Get the material of the object.
     *
     * @return  the material of the object
     */
    Material getMaterial();
    /**
     * Get the cooldown value.
     *
     * @return the cooldown value
     */
    int getCooldown();

    /**
     * A description of the entire Java function.
     *
     * @param  uuid	description of parameter
     * @return         	description of return value
     */
    void usar(UUID uuid);

    /**
     * Retrieves the etiqueta.
     *
     * @return         the etiqueta
     */
    String getEtiqueta();
}
