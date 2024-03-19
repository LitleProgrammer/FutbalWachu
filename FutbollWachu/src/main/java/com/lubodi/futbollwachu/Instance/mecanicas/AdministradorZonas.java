package com.lubodi.futbollwachu.Instance.mecanicas;

import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.Region;
import com.lubodi.futbollwachu.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Silverfish;

import java.util.*;

public class AdministradorZonas {

    private Set<Location> esquinasZonaGeneralExterior; // Variable para almacenar las esquinas
    private Set<Location> esquinasZonaGeneral;
    private Region zonaGeneral;
    private Region zonaGeneralExterior;
    private Region zonaHorizontalSuperior;
    private Region zonaHorizontalInferior;
    private Region zonaVerticalIzquierda;
    private Region zonaVerticalDerecha;
    private Region zonaHorizontales;
    private Region zonaVerticales;

    AdministradorZonas(Arena arena) {
        this.zonaGeneral = crearZonaGeneral(arena.getRegionZona(Team.BLUE), arena.getRegionZona(Team.RED));
        this.zonaGeneralExterior = crearZonaFueraDeCampo(zonaGeneral);
        this.esquinasZonaGeneral = obtenerEsquinasDeRegion(this.zonaGeneral);
        this.esquinasZonaGeneralExterior = obtenerEsquinasDeRegion(this.zonaGeneralExterior);
        this.zonaHorizontalSuperior = crearZonaHorizontalSuperior(zonaGeneralExterior, 6);
        this.zonaHorizontalInferior = crearZonaHorizontalInferior(zonaGeneralExterior, 6);
        this.zonaHorizontales = unificarZonasHorizontales(zonaHorizontalSuperior, zonaHorizontalInferior, 2);
        this.zonaVerticalIzquierda = crearZonaVerticalIzquierda(zonaGeneralExterior,6);
        this.zonaVerticalDerecha = crearZonaVerticalDerecha(zonaGeneralExterior,6);
        this.zonaVerticales = unificarZonasVerticales(zonaVerticalIzquierda, zonaVerticalDerecha, 4);


    }

    /**
     * Create a general zone based on the given regions and courts.
     *
     * @param region1 the first region
     * @param region2 the second region
     * @return the new general region, or null if there is no valid space
     */
    public Region crearZonaGeneral(Region region1, Region region2) {
        // Determinar los límites exteriores combinando las dos regiones
        Location corner1 = new Location(region1.getCorner1().getWorld(),
                Math.min(region1.getCorner1().getX(), region2.getCorner1().getX()),
                Math.min(region1.getCorner1().getY(), region2.getCorner1().getY()),
                Math.min(region1.getCorner1().getZ(), region2.getCorner1().getZ()));

        Location corner2 = new Location(region1.getCorner1().getWorld(),
                Math.max(region1.getCorner2().getX(), region2.getCorner2().getX()),
                Math.max(region1.getCorner2().getY(), region2.getCorner2().getY()),
                Math.max(region1.getCorner2().getZ(), region2.getCorner2().getZ()));

        // Crear la zona general como una nueva Region que abarca ambos sets de coordenadas
        return new Region(corner1, corner2);
    }


    // Create a method to create a region outside of the general zone with a specified horizontal margin
    public Region crearZonaFueraDeCampo(Region generalZone) {
        // Define the horizontal margin
        double horizontalMargin = 2;

        // Get the corners of the general zone
        Location corner1 = generalZone.getCorner1();
        Location corner2 = generalZone.getCorner2();

        // Determine the minimum and maximum coordinates of the general zone
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        // Create new corners for the zone outside the field with the specified margin
        Location newCorner1 = new Location(corner1.getWorld(), minX - horizontalMargin, minY, minZ - horizontalMargin);
        Location newCorner2 = new Location(corner2.getWorld(), maxX + horizontalMargin, maxY, maxZ + horizontalMargin);

        // Return a new region representing the zone outside the field
        return new Region(newCorner1, newCorner2);
    }
    // ... Resto de la clase AdministradorZonas ...

    // Método para obtener las cuatro esquinas de la región proporcionada
    // Método optimizado para obtener las cuatro esquinas de la región proporcionada
    public Set<Location> obtenerEsquinasDeRegion(Region region) {
        Location esquina1 = region.getCorner1();
        Location esquina2 = region.getCorner2();

        double minX = Math.min(esquina1.getX(), esquina2.getX());
        double maxX = Math.max(esquina1.getX(), esquina2.getX());
        double minY = Math.min(esquina1.getY(), esquina2.getY());
        double minZ = Math.min(esquina1.getZ(), esquina2.getZ());
        double maxZ = Math.max(esquina1.getZ(), esquina2.getZ());

        World world = esquina1.getWorld();

        // Añadiendo las cuatro esquinas con la altura media Y
        Set<Location> esquinas = new HashSet<>();
        esquinas.add(new Location(world, minX, minY, minZ)); // Esquina inferior izquierda
        esquinas.add(new Location(world, minX, minY, maxZ)); // Esquina superior izquierda
        esquinas.add(new Location(world, maxX, minY, minZ)); // Esquina inferior derecha
        esquinas.add(new Location(world, maxX, minY, maxZ)); // Esquina superior derecha

        return esquinas;
    }



    public Region crearZonaHorizontalSuperior(Region zonaGeneralExterior, double grosorZ) {
        Location esquinaSuperiorMinX = esquinasZonaGeneralExterior.stream()
                .filter(esquina -> esquina.getZ() == zonaGeneralExterior.getCorner2().getZ())
                .min(Comparator.comparingDouble(Location::getX))
                .orElse(null);

        Location esquinaSuperiorMaxX = esquinasZonaGeneralExterior.stream()
                .filter(esquina -> esquina.getZ() == zonaGeneralExterior.getCorner2().getZ())
                .max(Comparator.comparingDouble(Location::getX))
                .orElse(null);

        double minY = esquinaSuperiorMinX.getY();

        double maxZ = esquinaSuperiorMinX.getZ() + grosorZ / 2;
        double minZ = esquinaSuperiorMinX.getZ() - grosorZ / 2;

        return new Region(
                new Location(zonaGeneralExterior.getCorner1().getWorld(), esquinaSuperiorMinX.getX(), minY, maxZ), // Esquina superior izquierda
                new Location(zonaGeneralExterior.getCorner2().getWorld(), esquinaSuperiorMaxX.getX(), minY, minZ)); // Esquina superior derecha

    }

    public Region crearZonaHorizontalInferior(Region zonaGeneralExterior, double grosorZ) {
        Location esquinaInferiorMinX = esquinasZonaGeneralExterior.stream()
                .filter(esquina -> esquina.getZ() == zonaGeneralExterior.getCorner1().getZ())
                .min(Comparator.comparingDouble(Location::getX))
                .orElse(null);

        Location esquinaInferiorMaxX = esquinasZonaGeneralExterior.stream()
                .filter(esquina -> esquina.getZ() == zonaGeneralExterior.getCorner1().getZ())
                .max(Comparator.comparingDouble(Location::getX))
                .orElse(null);

        double maxY = esquinaInferiorMinX.getY();

        double maxZ = esquinaInferiorMinX.getZ() + grosorZ / 2;
        double minZ = esquinaInferiorMinX.getZ() - grosorZ / 2;

        return new Region(
                new Location(zonaGeneralExterior.getCorner1().getWorld(), esquinaInferiorMinX.getX(), maxY, minZ),
                new Location(zonaGeneralExterior.getCorner1().getWorld(), esquinaInferiorMaxX.getX(), maxY, maxZ)
        );
    }



        // Establecer minY en el tope superior del rango





    public Region crearZonaVerticalIzquierda(Region zonaGeneralExterior, double grosorX) {
        // El grosor adicional a cada lado de la región

        // Ajustar las coordenadas 'X' para añadir el grosor
        double minX = zonaGeneralExterior.getCorner1().getX() - grosorX; // Se mueve hacia la izquierda
        double maxX = zonaGeneralExterior.getCorner1().getX() + grosorX; // Se mueve hacia la derecha

        double minY = zonaGeneralExterior.getCorner1().getY();
        double maxY = zonaGeneralExterior.getCorner2().getY();

        double minZ = zonaGeneralExterior.getCorner1().getZ();
        double maxZ = zonaGeneralExterior.getCorner2().getZ();

        World world = zonaGeneralExterior.getCorner1().getWorld();

        return new Region(new Location(world, minX, minY, minZ),
                new Location(world, maxX, maxY, maxZ));
    }


    public Region crearZonaVerticalDerecha(Region zonaGeneralExterior,double grosorX) {

        // Buscamos las esquinas superiores e inferiores en el lado derecho
        double minX = zonaGeneralExterior.getCorner2().getX() - grosorX;
        double maxX = zonaGeneralExterior.getCorner2().getX() + grosorX;

        double minY = zonaGeneralExterior.getCorner1().getY();
        double maxY = zonaGeneralExterior.getCorner2().getY();

        double minZ = zonaGeneralExterior.getCorner1().getZ();
        double maxZ = zonaGeneralExterior.getCorner2().getZ();

        World world = zonaGeneralExterior.getCorner1().getWorld();

        return new Region(new Location(world, minX, minY, minZ),
                new Location(world, maxX, maxY, maxZ));
    }


    public Region unificarZonasHorizontales(Region zonaHorizontalSuperior, Region zonaHorizontalInferior, double grosorZ) {
        double minX = Math.min(zonaHorizontalInferior.getCorner1().getX(), zonaHorizontalSuperior.getCorner1().getX());
        double maxX = Math.max(zonaHorizontalInferior.getCorner2().getX(), zonaHorizontalSuperior.getCorner2().getX());
        // La Y mínima vendría de la zona inferior y la máxima de la superior.
        double minY = zonaHorizontalInferior.getCorner1().getY(); // Suponiendo que la Y de la esquina es la menor
        double maxY = zonaHorizontalSuperior.getCorner2().getY(); // Suponiendo que la Y de la esquina es la mayor

        // Expandir en Z sumando y restando la mitad del grosor deseado a Z actual.
        // Asumiendo que ambas zonas horizontales estarían en la misma altura de Z, podríamos tomar cualquiera
        double baseZ = zonaHorizontalInferior.getCorner1().getZ();
        double minZ = baseZ - grosorZ / 2;
        double maxZ = baseZ + grosorZ / 2;

        World world = zonaHorizontalSuperior.getCorner1().getWorld();

        return new Region(new Location(world, minX, minY, minZ),
                new Location(world, maxX, maxY, maxZ));
    }






    public Region unificarZonasVerticales(Region zonaVerticalIzquierda, Region zonaVerticalDerecha, double grosorX) {
        double minZ = zonaVerticalIzquierda.getCorner1().getZ();
        double maxZ = zonaVerticalDerecha.getCorner2().getZ();
        double minY = zonaVerticalIzquierda.getCorner1().getY();
        double maxY = zonaVerticalDerecha.getCorner2().getY();
        // Expandir en X sumando y restando la mitad del grosor deseado a maxX y minX respectivamente
        double minX = zonaVerticalIzquierda.getCorner1().getX() - grosorX / 2;
        double maxX = zonaVerticalDerecha.getCorner2().getX() + grosorX / 2;

        World world = zonaVerticalIzquierda.getCorner1().getWorld();

        return new Region(new Location(world, minX, minY, minZ),
                new Location(world, maxX, maxY, maxZ));
    }


    // Tus definiciones de zonas actuales...

        // Método para imprimir los rangos de las zonas
        public void imprimirRangosDeZonas() {
            System.out.println("Rangos de Zonas:");
            imprimirRangoDeZona("General Exterior", zonaGeneralExterior);
            imprimirRangoDeZona("Horizontal Superior", zonaHorizontalSuperior);
            imprimirRangoDeZona("Horizontal Inferior", zonaHorizontalInferior);
            imprimirRangoDeZona("Vertical Izquierda", zonaVerticalIzquierda);
            imprimirRangoDeZona("Vertical Derecha", zonaVerticalDerecha);
            imprimirRangoDeZona("Unificación Horizontal", zonaHorizontales);
            imprimirRangoDeZona("Unificación Vertical", zonaVerticales);
        }

        private void imprimirRangoDeZona(String nombreZona, Region zona) {
            if (zona != null && zona.getCorner1() != null && zona.getCorner2() != null) {
                Location esquina1 = zona.getCorner1();
                Location esquina2 = zona.getCorner2();
                System.out.println(nombreZona + ":");
                System.out.println("  Esquina 1: X=" + esquina1.getX() + " Y=" + esquina1.getY() + " Z=" + esquina1.getZ());
                System.out.println("  Esquina 2: X=" + esquina2.getX() + " Y=" + esquina2.getY() + " Z=" + esquina2.getZ());
            } else {
                System.out.println(nombreZona + ": [Información no disponible o zona no definida]");
            }
        }



    public double calcularAreaBaseDeRegion(Region region) {
        Location esquina1 = region.getCorner1();
        Location esquina2 = region.getCorner2();

        double longitud = Math.abs(esquina2.getX() - esquina1.getX());
        double ancho = Math.abs(esquina2.getZ() - esquina1.getZ());

        return longitud * ancho;
    }



    public Region getZonaGeneral() {
        return zonaGeneral;
    }

    public Region getZonaGeneralExterior() {
        return zonaGeneralExterior;
    }

    public Region getZonaHorizontales() {
        return zonaHorizontales;
    }

    public Region getZonaVerticales() {
        return zonaVerticales;
    }

    public Region getZonaHorizontalSuperior() {
        return zonaHorizontalSuperior;
    }

    public Region getZonaHorizontalInferior() {
        return zonaHorizontalInferior;
    }

    public Region getZonaVerticalIzquierda() {
        return zonaVerticalIzquierda;
    }

    public Region getZonaVerticalDerecha() {
        return zonaVerticalDerecha;
    }

    public Set<Location> getEsquinasZonaGeneralExterior() {
        return esquinasZonaGeneralExterior;
    }

    public Set<Location> getEsquinasZonaGeneral() {
        return esquinasZonaGeneral;
    }
    public Set<Location> getEsquinasZonaVerticalIzquierda() {
        return obtenerEsquinasDeRegion(getZonaVerticalIzquierda());
    }

    public Set<Location> getEsquinasZonaVerticalDerecha() {
        return obtenerEsquinasDeRegion(getZonaVerticalDerecha());
    }

    public boolean isEntityInZonaGeneralExterior(Entity entity) {
        return zonaGeneralExterior.contains(entity);
    }
}
