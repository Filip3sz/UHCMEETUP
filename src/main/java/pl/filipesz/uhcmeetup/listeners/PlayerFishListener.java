package pl.filipesz.uhcmeetup.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import pl.filipesz.uhcmeetup.utils.UtilManager;

import java.text.DecimalFormat;

public class PlayerFishListener implements Listener {

    private static final DecimalFormat df = new DecimalFormat("0.0");

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        Player p = e.getPlayer();

        if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Player t = e.getPlayer();
            double tHealth20 = ((int) (t.getHealth() * 100)) / 100.0;
            double tHealth10 = tHealth20 / 2;

            UtilManager.sendActionbar(p, "&7Ten gracz posiada &3" + df.format(tHealth10) + "HP");
        }
    }
}
