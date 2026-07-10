package pl.filipesz.uhcmeetup;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.filipesz.uhcmeetup.cmd.ListCMD;
import pl.filipesz.uhcmeetup.listeners.*;
import pl.filipesz.uhcmeetup.manager.GameManager;
import pl.filipesz.uhcmeetup.structs.UserFile;
import pl.filipesz.uhcmeetup.systems.RefreshAPISystem;

public class Main extends JavaPlugin {

    public static Main instance;

    public static Main getInstance() {
        return Main.instance;
    }

    private void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void onEnable() {

        //getConfig().options().copyDefaults(true);
        //saveConfig();
        UserFile.setup(this);

        Main.instance = this;

        // START SYSTEMS
        GameManager.generateMap();
        RefreshAPISystem.start();

        // SET WORLD BORDER
        World world = Bukkit.getWorld("world");
        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(0.0, 0.0);
        wb.setSize(250.0);

        getCommand("list").setExecutor(new ListCMD());
        registerEvent(new BlockFromToListener());
        registerEvent(new EntityDamageByEntityListener());
        registerEvent(new GameManagerListener());
        registerEvent(new PlayerFishListener());
    }
}


