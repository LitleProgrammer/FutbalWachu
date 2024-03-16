package com.lubodi.futbollwachu.Instance.mecanicas;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.Region;
import com.lubodi.futbollwachu.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AdministradorDeSaques {

    private MecanicasSaque mecanicas;

    private AdministradorZonas administradorZonas;
    private final Arena arena;

    public AdministradorDeSaques(Arena arena) {

        this.arena = arena;
        this.administradorZonas = new AdministradorZonas(arena);
        this.mecanicas = new MecanicasSaque(arena,this);

    }


    public boolean estaZonaGeneralExterior(Entity entity) {

        if (!administradorZonas.getZonaGeneral().contains(entity) && administradorZonas.getZonaGeneralExterior().contains(entity)) {

            return true;
        }
        return false;
    }


    public Entity encontrarPrimeraEntidadEnZonaGeneralExterior() {
        // Asegúrate de cambiar "world" por el nombre real de tu mundo.
        World world = Bukkit.getWorld("world");

        // Verificar que la zonaGeneralExterior está definida y el mundo existe.
        if (world == null) {

            return null;
        }

        // Iterar sobre todas las entidades en el mundo.
        for (Entity entity : world.getEntities()) {
            // Si encuentra una entidad dentro de la zonaGeneralExterior y es un Silverfish, la retorna.
            if (estaZonaGeneralExterior(entity) && entity instanceof Silverfish) {

                System.out.println("se encontro a " + entity.getName());
                return entity; // Retorna la primera entidad encontrada en la zonaGeneralExterior.
            }
        }
        return null; // No se encontró ninguna entidad que cumpla con los criterios.
    }


    public void ejecutarAccion() {
        Entity entity = encontrarPrimeraEntidadEnZonaGeneralExterior();
        if (entity != null) {
            if (arena.getEntityInCancha() == null) {
                ZonaTipo zona = encontrarTipoDeZonaParaEntidad(entity);
                Team equipo =  obtenerEquipoOpuestoUltimoGolpe();

                switch (zona) {
                    case HORIZONTAL:
                        System.out.println("horizontal");
                         mecanicas.teleportarJugadorAleatorioNoPortero(equipo, encontrarUbicacionParaSaqueDeBanda(entity));

                        break;
                    case VERTICAL:
                        mecanicas.teleportarJugadorAleatorioNoPortero(equipo, encontrarUbicacionParaTiroDeEsquina(entity));

                        ;
                        break;
                    case GENERAL_EXTERIOR:

                        break;
                }

            }
        }
    }


    public Team obtenerEquipoOpuestoUltimoGolpe() {
        // Obtener el jugador que hizo el último golpe
        Player ultimoGolpeador = arena.getLastHitters(); // Asumimos que este método devuelve el último golpeador

        // Verificar si el jugador existe y está en un equipo
        if (ultimoGolpeador != null && arena.getPlayers().contains(ultimoGolpeador.getUniqueId())) {
            // Obtener el equipo del jugador
            Team equipoGolpeador = arena.getTeam(ultimoGolpeador);
            System.out.println("El equipo del golpeador es " + equipoGolpeador);

            // Determinar y devolver el equipo opuesto
            if (equipoGolpeador != null) {
                if (equipoGolpeador == Team.BLUE) {
                    return Team.RED;
                } else if (equipoGolpeador == Team.RED) {
                    return Team.BLUE;
                }
            }
        }

        // Si no se encuentra al último golpeador, no está en un equipo, o si hay más de dos equipos y no se puede determinar un opuesto claro, devolver null
        return null;
    }


    public ZonaTipo encontrarTipoDeZonaParaEntidad(Entity entity) {
        System.out.println("la entidad esta en" + entity.getLocation());
        if (administradorZonas.getZonaVerticalIzquierda().contains(entity) || administradorZonas.getZonaVerticalDerecha().contains(entity)) {
            return ZonaTipo.VERTICAL;
        }
        if (administradorZonas.getZonaHorizontalSuperior().contains(entity) || administradorZonas.getZonaHorizontalInferior().contains(entity)) {
            return ZonaTipo.HORIZONTAL;
        }
        if (administradorZonas.getZonaGeneralExterior().contains(entity)) {
            System.out.println("Se encuentra en la zona general exterior");
            return ZonaTipo.GENERAL_EXTERIOR;
        }
        System.out.println("La entidad no está en ninguna zona especificada");
        return ZonaTipo.NO_ENCONTRADA;
    }

    public Location encontrarUbicacionParaSaqueDeBanda(Entity balon) {
        // Obtiene la ubicación actual del balón.
        Location ubicacionBalon = balon.getLocation();
        double zBalon = ubicacionBalon.getZ();

        // Obtiene las esquinas de la zona lateral de juego.
        Location esquinaSuperior = administradorZonas.getZonaGeneral().getCorner1(); // Asume que esta es la esquina superior.
        Location esquinaInferior = administradorZonas.getZonaGeneral().getCorner2(); // Asume que esta es la esquina inferior.

        // Imprime las esquinas de la zona para fines de depuración.


        // Imprime la ubicación actual del balón para fines de depuración.


        double zBordeCercano;

        // Determina cuál de los bordes superiores o inferiores del campo está más cerca y utiliza ese para la ubicación Z.
        if (Math.abs(zBalon - esquinaSuperior.getZ()) < Math.abs(zBalon - esquinaInferior.getZ())) {
            zBordeCercano = esquinaSuperior.getZ(); // El balón está más cerca del borde superior del campo.

        } else {
            zBordeCercano = esquinaInferior.getZ(); // El balón está más cerca del borde inferior del campo.

        }

        // Imprime la coordenada Z del borde más cercano para fines de depuración.


        // Crea y devuelve la nuevos ubicación para el saque de banda en base a los cálculos.
        Location ubicacionSaqueBanda = new Location(ubicacionBalon.getWorld(), ubicacionBalon.getX(), ubicacionBalon.getY(), zBordeCercano);


        return ubicacionSaqueBanda;
    }
    public Location encontrarUbicacionParaTiroDeEsquina(Entity balon) {
        // Obtiene la ubicación actual del balón.
        Location ubicacionBalon = balon.getLocation();

        // Obtiene las esquinas de las zonas verticales.
        Set<Location> esquinasVerticales = new HashSet<>();
        esquinasVerticales.addAll(administradorZonas.getEsquinasZonaVerticalIzquierda());
        esquinasVerticales.addAll(administradorZonas.getEsquinasZonaVerticalDerecha());

        // Encuentra la esquina más cercana de las zonas verticales.
        Location esquinaVerticalMasCercana = null;
        double distanciaMinimaVertical = Double.MAX_VALUE;

        for (Location esquina : esquinasVerticales) {
            double distancia = ubicacionBalon.distance(esquina);
            if (distancia < distanciaMinimaVertical) {
                distanciaMinimaVertical = distancia;
                esquinaVerticalMasCercana = esquina;
            }
        }

        // Ahora encuentras la correspondiente esquina más cercana en la zona general.
        Location esquinaGeneralMasCercana = null;
        double distanciaMinimaGeneral = Double.MAX_VALUE;

        for (Location esquina : administradorZonas.getEsquinasZonaGeneral()) {
            double distancia = esquinaVerticalMasCercana.distance(esquina);
            if (distancia < distanciaMinimaGeneral) {
                distanciaMinimaGeneral = distancia;
                esquinaGeneralMasCercana = esquina;
            }
        }

        // Imprime la esquina más cercana para fines de depuración.
        System.out.println("Esquina más cercana para el tiro de esquina: " + esquinaGeneralMasCercana);

        // Retorna la esquina más cercana de la zona general como ubicación para el tiro de esquina.
        return esquinaGeneralMasCercana;
    }

    public Location encontrarUbicacionParaSaquedeDesdeJugadores(Player player) {
        Location zBordeCercano = encontrarUbicacionParaSaqueDeBanda(player);
        double adelantamiento = administradorZonas.getZonaHorizontalSuperior().contains(player) ? -2.0 : 2.0;
        double nuevaZ = zBordeCercano.getZ() + adelantamiento;

        return new Location(zBordeCercano.getWorld(), zBordeCercano.getX(), zBordeCercano.getY(), nuevaZ);
    }

    public void obtenerSilverFish() {
        Silverfish silverfish = arena.getBolas().values().stream().findFirst().get();
    }

    public MecanicasSaque getMecanicasSaque() {
        return mecanicas;
    }

    public enum ZonaTipo {
        HORIZONTAL,
        VERTICAL,
        GENERAL_EXTERIOR,
        NO_ENCONTRADA // Este enum representa que la entidad no está en ninguna de las zonas específicas
    }
}


