package pl.filipesz.uhcmeetup.systems;

import org.bukkit.scheduler.BukkitRunnable;
import pl.filipesz.uhcmeetup.Main;
import pl.filipesz.uhcmeetup.utils.UtilManager;

public class RefreshAPISystem {

    public static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                UtilManager.updateScoreboard();
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }
}

