package com.lubodi.futbollwachu.Instance.mecanicas;

import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.Region;
import com.lubodi.futbollwachu.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;


import java.util.Map;
import java.util.Set;

public class AdministradorDeSaques {

    private final MecanicasSaque mecanicas;

    private final AdministradorZonas administradorZonas;
    private final Arena arena;

    public AdministradorDeSaques(Arena arena) {

        this.arena = arena;
        this.administradorZonas = new AdministradorZonas(arena);
        this.mecanicas = new MecanicasSaque(arena,this);

    }


    public boolean estaZonaGeneralExterior(Entity entity) {

        return !administradorZonas.getZonaGeneral().contains(entity) && administradorZonas.getZonaExterior().contains(entity);
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
        if (entity != null && arena.getEntityInCancha() == null && !mecanicas.isSaqueVigente()) {
            procesarSaque(entity);
        }
    }
    private void procesarSaque(Entity entity) {
        ZonaTipo zona = encontrarTipoDeZonaParaEntidad(entity);
        Team equipo = obtenerEquipoOpuestoUltimoGolpe();

        if (equipo == null) {
            // Manejo de caso de error o equipo nulo
            return;
        }

        switch (zona) {
            case HORIZONTAL:
                mecanicas.teleportarJugadorAleatorioNoPortero(equipo, encontrarUbicacionParaSaqueDeBanda(entity));
                System.out.println("horizontal");
                break;
            case VERTICAL:
                if (equipo == determinateNearTeamZone(entity)) {
                    mecanicas.teleportarPortero(equipo, encontrarUbicacionParaSaqueDeMeta(equipo));
                } else {
                    mecanicas.teleportarJugadorAleatorioNoPortero( equipo, encontrarUbicacionParaTiroDeEsquina(entity));
                }
                break;
            // Potencialmente otros casos...
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
            } else {
                return null;
            }
        }

        // Si no se encuentra al último golpeador, no está en un equipo, o si hay más de dos equipos y no se puede determinar un opuesto claro, devolver null
        return null;
    }


    public ZonaTipo encontrarTipoDeZonaParaEntidad(Entity entity) {
        System.out.println("la entidad esta en" + entity.getLocation());
        if (administradorZonas.getZonaVerticalIzquierda().contains(entity) || administradorZonas.getZonaVerticalDerecha().contains(entity)) {
            System.out.println("Se encuentra en la zona vertical");
            return ZonaTipo.VERTICAL;

        }
        if (administradorZonas.getZonaHorizontalSuperior().contains(entity) || administradorZonas.getZonaHorizontalInferior().contains(entity)) {
            System.out.println("Se encuentra en la zona horizontal");
            return ZonaTipo.HORIZONTAL;
        }
        if (administradorZonas.getZonaExterior().contains(entity)) {
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
        Location esquinaSuperior = administradorZonas.getZonaGeneral().getCorner1();
        Location esquinaInferior = administradorZonas.getZonaGeneral().getCorner2();

        // Determina cuál de los bordes superiores o inferiores del campo está más cerca y utiliza ese para la ubicación Z.
        double zBordeCercano = Math.abs(zBalon - esquinaSuperior.getZ()) < Math.abs(zBalon - esquinaInferior.getZ()) ? esquinaSuperior.getZ() : esquinaInferior.getZ();

        // Crea y devuelve la nueva ubicación para el saque de banda en base a los cálculos.
        return new Location(ubicacionBalon.getWorld(), ubicacionBalon.getX(), ubicacionBalon.getY(), zBordeCercano);
    }
    public Location encontrarUbicacionParaTiroDeEsquina(Entity balon) {
        // Obtiene la ubicación actual del balón.
        Location ubicacionBalon = balon.getLocation();

        // Obtiene las esquinas de las zonas generales.
        Set<Location> esquinasGenerales = administradorZonas.getEsquinasZonaGeneral();

        // Encuentra la esquina más cercana dentro de las generales.
        Location esquinaGeneralMasCercana = null;
        double distanciaMinimaGeneral = Double.MAX_VALUE;

        for (Location esquina : esquinasGenerales) {
            double distancia = ubicacionBalon.distance(esquina);
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




    public Location encontrarUbicacionParaSaqueDeMeta(Team team) {
        Region region = arena.getPorteroRegion(team);
        System.out.println(region.getMiddle());
        return region.getMiddle();
    }


    public Location encontrarUbicacionParaSaquedeDesdeJugadores(Player player, double distancia) {
        Location zBordeCercano = encontrarUbicacionParaSaqueDeBanda(player);
        double nuevaZ = zBordeCercano.getZ();

        if(administradorZonas.getZonaHorizontalSuperior().contains(player)) {
            System.out.println("Se encuentra en la zona horizontal superior");
            nuevaZ += distancia;
        }

        if(administradorZonas.getZonaHorizontalInferior().contains(player)) {
            System.out.println("Se encuentra en la zona horizontal inferior");
            nuevaZ -= distancia;
        }

        return new Location(zBordeCercano.getWorld(), zBordeCercano.getX(), zBordeCercano.getY(), nuevaZ);
    }
    public Team determinateNearTeamZone(Entity balon) {
        Location ubicacionBalon = balon.getLocation();

        double distanciaMinimaEsquina = Double.MAX_VALUE;
        Team zonaMasCercana = null;

        for (Map.Entry<Team, Region> entry : arena.getZones().entrySet()) {
            Region zona = entry.getValue();
            for (Location esquina : zona.getEsquinas()) {
                double distancia = ubicacionBalon.distance(esquina);
                if (distancia < distanciaMinimaEsquina) {
                    distanciaMinimaEsquina = distancia;
                    zonaMasCercana = entry.getKey();
                }
            }
        }

        System.out.println("equipo de zona mas cercana: " + zonaMasCercana);
        return zonaMasCercana;
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


