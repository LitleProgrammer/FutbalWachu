package com.lubodi.futbollwachu;




import com.lubodi.futbollwachu.BolaFisicasYMetodos.Fisicas;

import com.lubodi.futbollwachu.BolaFisicasYMetodos.Metodos;
import com.lubodi.futbollwachu.Commands.AdminArenaTab;
import com.lubodi.futbollwachu.Commands.ArenaCommand;
import com.lubodi.futbollwachu.Commands.AdminArenaCommand;
import com.lubodi.futbollwachu.HabilidadesFutbol.Commands.ComandoFutbol;
import com.lubodi.futbollwachu.HabilidadesFutbol.GUI.HabilidadGUI;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.HabilidadHandClickListener;
import com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.ClickEventBall.onBolaLeftClickEvent;
import com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.ClickEventBall.onBolaRigthClickEvent;
import com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.DropItem.DropItemEvent;
import com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.Mecanics.itemSiwch.SwichItemEvent;
import com.lubodi.futbollwachu.Instance.mecanicas.MecanicasSaque;
import com.lubodi.futbollwachu.Listeners.ConnectListener;
import com.lubodi.futbollwachu.Listeners.GameListener;
import com.lubodi.futbollwachu.Listeners.SaqueListener;
import com.lubodi.futbollwachu.Manager.ArenaManager;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class FutballBola extends JavaPlugin {
    private static FutballBola instance;
    private Fisicas fisicas;
    private Metodos metodos;
    private ArenaManager arenaManager;
    private MecanicasSaque mecanicasSaque;
    // A map containing arenas, that are being set up <String arenaName, Integer arenaID>
    private HashMap<String, Integer> setupArenas = new HashMap<>();




    /**
     * The onEnable function is called when the plugin is enabled. It initializes
     * the instance, sets up the configuration, registers events, and sets the
     * executor for the "futbol" and "arena" commands.
     *
     */
    @Override
    public void onEnable() {


        ConfigManager.setupConfig(this, this);

        fisicas = new Fisicas();
        arenaManager = new ArenaManager(this);
        instance = this;

        fisicas = new Fisicas();
        metodos = new Metodos(this, getFisicas());
        // No need to create instances here, as they are injected through the constructor
        getServer().getPluginManager().registerEvents(new Metodos(this, getFisicas()), this);
        getCommand("futbol").setExecutor(new ComandoFutbol(this, metodos));
        getCommand("adminArena").setExecutor(new AdminArenaCommand(this));
        getCommand("adminArena").setTabCompleter(new AdminArenaTab(this));

        // Register events.
        getServer().getPluginManager().registerEvents(new ConnectListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new SwichItemEvent(this, HabilidadesManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new onBolaLeftClickEvent(this, HabilidadesManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new onBolaRigthClickEvent(this, HabilidadesManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new DropItemEvent(this, HabilidadesManager.getInstance()), this);

        getServer().getPluginManager().registerEvents(new HabilidadGUI(this, HabilidadesManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new SaqueListener(this), this);
        getCommand("arena").setExecutor(new ArenaCommand(this));
    }

    public Fisicas getFisicas() {
        return fisicas;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public static FutballBola getInstance() {
        return instance;
    }

    public static MecanicasSaque getMecanicasSaque() {
        return getInstance().mecanicasSaque;
    }

    public void setArenaManager(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    public HashMap<String, Integer> getSetupArenas() {
        return setupArenas;
    }
}
