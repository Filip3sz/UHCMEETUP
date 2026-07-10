package pl.filipesz.uhcmeetup.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.filipesz.uhcmeetup.utils.UtilManager;

import java.text.DecimalFormat;

public class EntityDamageByEntityListener implements Listener {

    private static final DecimalFormat df = new DecimalFormat("0.0");

    @EventHandler
    public void onSnowballArrowHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Snowball) {
            Snowball snowball = (Snowball) e.getDamager();
            Entity hitBySnowball = e.getEntity();
            LivingEntity p = (LivingEntity) snowball.getShooter();

            if (hitBySnowball instanceof Player) {
                Player t = (Player) hitBySnowball;
                double tHealth20 = ((int) (t.getHealth() * 100)) / 100.0;
                double tHealth10 = tHealth20 / 2;

                UtilManager.sendActionbar((Player) p, "&7Ten gracz posiada &3" + df.format(tHealth10) + "HP");
            }
        }
        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            Entity hitByArrow = e.getEntity();
            LivingEntity p = (LivingEntity) arrow.getShooter();

            if (hitByArrow instanceof Player) {
                Player t = (Player) hitByArrow;
                double tHealth20 = ((int) (t.getHealth() * 100)) / 100.0;
                double tHealth10 = tHealth20 / 2;

                UtilManager.sendActionbar((Player) p, "&7Ten gracz posiada &3" + df.format(tHealth10) + "HP");
            }
        }
    }
}
