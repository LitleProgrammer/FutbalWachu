package com.lubodi.futbollwachu.Listeners;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Instance.mecanicas.AdministradorDeSaques;
import com.lubodi.futbollwachu.Instance.mecanicas.AdministradorZonas;
import com.lubodi.futbollwachu.Instance.mecanicas.MecanicasSaque;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    private FutballBola minigame;
    public MoveListener(FutballBola minigame){
        this.minigame = minigame;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Arena arenas = minigame.getArenaManager().getArena(e.getPlayer());
        if(arenas == null){
            return;
        }
        AdministradorZonas administradorZonas = arenas.getAdministradorDeSaques().getAdministradorZonas();
        MecanicasSaque mecanicasSaque = arenas.getAdministradorDeSaques().getMecanicasSaque();

        if(!arenas.getCanchas().contains(e.getPlayer()) && administradorZonas.isEntityInZonaGeneralExterior(e.getPlayer())){
           arenas.getAdministradorDeSaques().encontrarUbicacionParaSaquedeDesdeJugadores(e.getPlayer(),1);
        }

    }
}
