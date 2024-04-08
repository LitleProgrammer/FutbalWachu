package com.lubodi.futbollwachu.Instance.mecanicas;

import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.Region;
import com.lubodi.futbollwachu.team.Team;

import org.bukkit.Location;
import org.bukkit.World;


import java.util.*;

public class AdministradorZonas {


    private final Map<Region.ZoneType, Region> zonas = new HashMap<>();
    private final Map<Region.ZoneType, Set<Location>> esquinas = new HashMap<>();

    private final Arena arena;
    AdministradorZonas(Arena arena) {

        this.arena = arena;
        crearZonas(arena);



    }


    private void crearZonas(Arena arena) {
        Region regionAzul = arena.getRegionZona(Team.BLUE);
        Region regionRoja = arena.getRegionZona(Team.RED);
        if (regionAzul == null || regionRoja == null) {
            return;
        }

        Region zonaGeneral = crearZonaGeneral(regionAzul, regionRoja);
        asignarZona(Region.ZoneType.GENERAL, crearZonaGeneral(regionAzul, regionRoja));
        asignarZona(Region.ZoneType.EXTERIOR, crearZonaFueraDeCampo(zonaGeneral));
        if(zonas.containsKey(Region.ZoneType.EXTERIOR)){
            Region zonaExterior = zonas.get(Region.ZoneType.EXTERIOR);
            asignarZona(Region.ZoneType.HORIZONTAL_SUPERIOR, crearZonaHorizontalSuperior(zonaExterior,6));
            asignarZona(Region.ZoneType.HORIZONTAL_INFERIOR, crearZonaHorizontalInferior(zonaExterior,6));
            asignarZona(Region.ZoneType.VERTICAL_RIGHT, crearZonaVerticalDerecha(zonaExterior,3));
            asignarZona(Region.ZoneType.VERTICAL_LEFT, crearZonaVerticalIzquierda(zonaExterior,3));
        }

     imprimirRangosDeZona();
    }
    private void asignarZona(Region.ZoneType tipo, Region zona) {
        zonas.put(tipo, zona);
        esquinas.put(tipo, obtenerEsquinasDeRegion(zona));
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
        double horizontalMargin = 6;

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
        // Identificar el punto más alto de la zona exterior como punto de referencia para la zona superior.
        double maxY = Math.max(zonaGeneralExterior.getCorner1().getY(), zonaGeneralExterior.getCorner2().getY());
        double minY = Math.min(zonaGeneralExterior.getCorner1().getY(), zonaGeneralExterior.getCorner2().getY());
        // Calcular las nuevas coordenadas Z basadas en el grosor dado y el extremo superior (maxY) de la zona.
        double maxZ = zonaGeneralExterior.getCorner1().getZ(); // Usamos el mismo Z que la esquina 1.
        double minZ = maxZ + grosorZ; // Reducimos el grosor desde el Z actual para obtener el borde inferior (minZ).

        // Calculamos las coordenadas X manteniendo las esquinas de la zona exterior.
        double minX = Math.min(zonaGeneralExterior.getCorner1().getX(), zonaGeneralExterior.getCorner2().getX());
        double maxX = Math.max(zonaGeneralExterior.getCorner1().getX(), zonaGeneralExterior.getCorner2().getX());

        // Las nuevas coordenadas Y serán un poco más altas que maxY para garantizar que se encuentran por encima.


        return new Region(
                new Location(zonaGeneralExterior.getCorner1().getWorld(), minX, minY, minZ),
                new Location(zonaGeneralExterior.getCorner1().getWorld(), maxX, maxY, maxZ)
        );
    }

    public Region crearZonaHorizontalInferior(Region zonaGeneralExterior, double grosorZ) {
        // Identificar el punto más bajo de la zona exterior como punto de referencia para la inferior.
        double minY = Math.min(zonaGeneralExterior.getCorner1().getY(), zonaGeneralExterior.getCorner2().getY());
        double maxY = Math.max(zonaGeneralExterior.getCorner1().getY(), zonaGeneralExterior.getCorner2().getY());

        // Calcular las nuevas coordenadas Z basadas en el grosor dado y el extremo inferior (minY) de la zona.
        double minZ = zonaGeneralExterior.getCorner2().getZ(); // Usamos el mismo Z que la esquina 2.
        double maxZ = minZ - grosorZ; // Añadimos el grosor al Z actual para obtener el borde superior (maxZ).

        // Calculamos las coordenadas X manteniendo las esquinas de la zona exterior.
        double minX = Math.min(zonaGeneralExterior.getCorner1().getX(), zonaGeneralExterior.getCorner2().getX());
        double maxX = Math.max(zonaGeneralExterior.getCorner1().getX(), zonaGeneralExterior.getCorner2().getX());

        // Las nuevas coordenadas Y serán un poco más bajas que minY para asegurar que se encuentran por debajo.


        return new Region(
                new Location(zonaGeneralExterior.getCorner1().getWorld(), minX, minY, minZ),
                new Location(zonaGeneralExterior.getCorner1().getWorld(), maxX, maxY, maxZ)
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





    public void imprimirRangosDeZona() {
        for (Map.Entry<Region.ZoneType, Region> entrada : zonas.entrySet()) {
            String nombreZona = entrada.getKey().toString();
            Region zona = entrada.getValue();

            imprimirRangoDeZona(nombreZona, zona);
        }
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




    public Set<Location> getEsquinasZonaVerticalIzquierda() {
       Region zonaVerticalIzquierda = zonas.get(Region.ZoneType.VERTICAL_LEFT);
       return zonaVerticalIzquierda != null ? obtenerEsquinasDeRegion(zonaVerticalIzquierda) : Collections.emptySet();
    }

    public Set<Location> getEsquinasZonaVerticalDerecha() {
        Region zonaVerticalDerecha = zonas.get(Region.ZoneType.VERTICAL_RIGHT);
        return zonaVerticalDerecha != null ? obtenerEsquinasDeRegion(zonaVerticalDerecha) : Collections.emptySet();
    }
    public Region getZonaGeneral() {
        return zonas.get(Region.ZoneType.GENERAL);
    }
    public Region getZonaExterior() {
        return zonas.get(Region.ZoneType.EXTERIOR);
    }
    public Region getZonaHorizontalSuperior()  {
        return zonas.get(Region.ZoneType.HORIZONTAL_SUPERIOR);
    }
    public Region getZonaHorizontalInferior() {
        return zonas.get(Region.ZoneType.HORIZONTAL_INFERIOR);
    }
    public Region getZonaVerticalIzquierda() {
        return zonas.get(Region.ZoneType.VERTICAL_LEFT);
    }
    public Region getZonaVerticalDerecha() {
        return zonas.get(Region.ZoneType.VERTICAL_RIGHT);
    }

    public Set<Location> getEsquinasZonaGeneral() {
        // Obtiene la región general del mapa de zonas y calcula sus esquinas.
        Region zonaGeneral = zonas.get(Region.ZoneType.GENERAL);
        return obtenerEsquinasDeRegion(zonaGeneral);
    }
    public Set<Location> getEsquinasZonaExterior() {
        // Obtiene la región exterior del mapa de zonas y calcula sus esquinas.
        Region zonaExterior = zonas.get(Region.ZoneType.EXTERIOR);
        return obtenerEsquinasDeRegion(zonaExterior);
    }


}
