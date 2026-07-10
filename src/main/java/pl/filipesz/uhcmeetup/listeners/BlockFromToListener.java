package pl.filipesz.uhcmeetup.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

@SuppressWarnings("ALL")
public class BlockFromToListener implements Listener {

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        int id = e.getBlock().getTypeId();
        if (id == 10 || id == 11) {
            e.setCancelled(true);
        }
    }
}
