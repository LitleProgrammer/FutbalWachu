package com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.ClickEventBall;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.utils.ParticleSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

public class onBolaRigthClickEvent implements Listener {
    private final FutballBola plugin;
    private final HabilidadesManager habilidadManager;

    public onBolaRigthClickEvent(FutballBola plugin, HabilidadesManager habilidadManager) {
        this.plugin = plugin;
        this.habilidadManager = habilidadManager;
    }

    private static final String NOMBRE_BOLA = "Bola";
    private static final long COOLDOWN_A_TRAPAR_TICKS = 5L * 20; // 5 segundos en ticks

    @EventHandler
    public void onSilverfishClick(PlayerInteractEntityEvent event) {
        Player jugador = event.getPlayer();
        Entity entidad = event.getRightClicked();

        if (!esBola(entidad)) {
            return;
        }

        Silverfish silverfish = (Silverfish) entidad;
        Habilidad habilidadActiva = habilidadManager.obtenerHabilidadActiva(jugador.getUniqueId());
        Arena arena = plugin.getArenaManager().getArena(jugador);

        if (habilidadPuedeUsarse(habilidadActiva, jugador)) {
            usarHabilidad(jugador, habilidadActiva);
        } else if (jugadorEsPorteroYPuedeAtrapar(arena, jugador)) {
            atraparBola(jugador, silverfish);
        } else {
            manejarMovimientoBola(jugador, silverfish);
        }

        new ParticleSpawner().spawnParticle(Particle.END_ROD, entidad.getLocation());
    }

    private boolean esBola(Entity entidad) {
        return entidad instanceof Silverfish && NOMBRE_BOLA.equals(entidad.getCustomName());
    }

    private boolean habilidadPuedeUsarse(Habilidad habilidad, Player jugador) {
        return habilidad != null && !habilidadManager.estaEnCooldown(jugador.getUniqueId());
    }

    private void usarHabilidad(Player jugador, Habilidad habilidad) {
        habilidad.usar(jugador.getUniqueId());
        habilidadManager.desactivarHabilidad(jugador.getUniqueId(), habilidad);
    }

    private boolean jugadorEsPorteroYPuedeAtrapar(Arena arena, Player jugador) {
        return arena.isPlayerPortero(jugador) && arena.getPorteroOfZona(jugador)
                && !habilidadManager.estaEnCooldownAtrapar(jugador.getUniqueId());
    }

    private void atraparBola(Player jugador, Silverfish silverfish) {
        jugador.sendMessage("No puedes atrapar la bola ahora.");
        jugador.addPassenger(silverfish);
        Bukkit.getScheduler().runTaskLater(plugin, () -> liberarBola(jugador), COOLDOWN_A_TRAPAR_TICKS);
    }

    private void liberarBola(Player jugador) {
        jugador.eject();
        habilidadManager.iniciarCooldownAtrapar(jugador.getUniqueId());
    }

    private void manejarMovimientoBola(Player jugador, Silverfish silverfish) {
        Vector direccion = jugador.getLocation().getDirection();
        silverfish.setVelocity(direccion.multiply(1.5));
        Arena arena = plugin.getArenaManager().getArena(jugador);
        arena.setLastHitters(jugador);
    }

}
