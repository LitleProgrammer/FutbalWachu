package com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.ClickEventBall;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.utils.ParticleSpawner;
import com.lubodi.futbollwachu.utils.SoundManager;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class onBolaLeftClickEvent implements Listener {

    private final FutballBola plugin;
    private final HabilidadesManager habilidadManager;

    public onBolaLeftClickEvent(FutballBola plugin, HabilidadesManager habilidadManager) {
        this.plugin = plugin;
        this.habilidadManager = habilidadManager;
    }

    private static final String NOMBRE_BOLA = "Bola";


    @EventHandler
    public void onSilverfishClickleft(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Silverfish)) return;

        Silverfish silverfish = (Silverfish) event.getEntity();
        if (esBola(silverfish)) {
            Player jugador = (Player) event.getDamager();
            Arena arena = plugin.getArenaManager().getArena(jugador);
            arena.setLastHitters(jugador);
            procesarHabilidadSilverfish(jugador);
            new ParticleSpawner().spawnParticle(Particle.END_ROD, silverfish.getLocation());
            new SoundManager().ballHitSound(jugador);
        }
    }

    private boolean esBola(Silverfish silverfish) {
        return NOMBRE_BOLA.equals(silverfish.getCustomName());
    }

    private void procesarHabilidadSilverfish(Player jugador) {
        Habilidad habilidadActiva = habilidadManager.obtenerHabilidadActiva(jugador.getUniqueId());
        if (habilidadActiva != null && !habilidadManager.estaEnCooldown(jugador.getUniqueId())) {
            habilidadActiva.usar(jugador.getUniqueId());
            habilidadManager.desactivarHabilidad(jugador.getUniqueId(), habilidadActiva);
        }
    }


    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking() && jugadorTieneBola(player)) {
            liberarBola(player);
        }
    }

    private boolean jugadorTieneBola(Player player) {
        return player.getPassenger() instanceof Silverfish
                && esBola((Silverfish) player.getPassenger());
    }

    private void liberarBola(Player jugador) {
        jugador.eject();
        iniciarCooldownAtrapar(jugador);
    }

    private void iniciarCooldownAtrapar(Player jugador) {
        habilidadManager.iniciarCooldownAtrapar(jugador.getUniqueId());
    }

}
