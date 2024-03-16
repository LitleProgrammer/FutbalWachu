package com.lubodi.futbollwachu.Listeners;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Instance.mecanicas.AdministradorDeSaques;
import com.lubodi.futbollwachu.Instance.mecanicas.MecanicasSaque;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

public class SaqueListener implements Listener {

    private FutballBola minigame;
    public SaqueListener(FutballBola minigame){
        this.minigame = minigame;
    };


    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player jugador = event.getPlayer();
        Entity entidad = event.getRightClicked();
        if (entidad instanceof Silverfish && entidad.getCustomName() != null && entidad.getCustomName().equals("Bola"));{
            Arena arena = minigame.getArenaManager().getArena(jugador);
            AdministradorDeSaques administradorDeSaques = arena.getAdministradorDeSaques();
            MecanicasSaque mecanicasSaque = administradorDeSaques.getMecanicasSaque();
            if(mecanicasSaque.isSaqueVigente()){
                if(mecanicasSaque.isSaqueEnProgreso()){
                    System.out.println("esta en progreso empuje cancelado");
                    entidad.setVelocity(new Vector(0, 0, 0));
                    event.setCancelled(true);
                    return;
                }
                System.out.println("saque vigente");
                if (!mecanicasSaque.isSaqueEnProgreso() && !jugador.getUniqueId().equals(mecanicasSaque.getCobradorDeTiro())) {
                    System.out.println("cobrador es " + mecanicasSaque.getCobradorDeTiro());
                    entidad.setVelocity(new Vector(0, 0, 0));
                    event.setCancelled(true);

                }
            }else if(!mecanicasSaque.isSaqueEnProgreso() && jugador.getUniqueId().equals(mecanicasSaque.getCobradorDeTiro())){
                mecanicasSaque.setSaqueRealizado(true);
            }
        }
    }
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!(damager instanceof Player) || !(entity instanceof Silverfish)) {
            return;
        }

        Player player = (Player) damager;
        Silverfish silverfish = (Silverfish) entity;

        if (silverfish.getCustomName() == null || !silverfish.getCustomName().equals("Bola")) {
            return;
        }

        Arena arena = minigame.getArenaManager().getArena(player);
        AdministradorDeSaques administradorDeSaques = arena.getAdministradorDeSaques();
        MecanicasSaque mecanicasSaque = administradorDeSaques.getMecanicasSaque();
        if(mecanicasSaque.isSaqueVigente()){
            System.out.println("saque vigente");
            if(mecanicasSaque.isSaqueEnProgreso()){
                System.out.println("esta en progreso golpe cancelado");
                event.setCancelled(true);
                return;
            }
            if (!mecanicasSaque.isSaqueEnProgreso() && !player.getUniqueId().equals(mecanicasSaque.getCobradorDeTiro())) {
                event.setCancelled(true);

            }
        }else if(!mecanicasSaque.isSaqueEnProgreso() && player.getUniqueId().equals(mecanicasSaque.getCobradorDeTiro())){
            System.out.println();
            mecanicasSaque.setSaqueRealizado(true);
        }
    }
}
