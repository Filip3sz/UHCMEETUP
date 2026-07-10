package pl.filipesz.uhcmeetup.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import pl.filipesz.uhcmeetup.Main;

@SuppressWarnings("ALL")
public class BungeeListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String s, Player c, byte[] message) {
        if (!s.equals("BungeeCord"))
            return;

        ByteArrayDataInput i = ByteStreams.newDataInput(message);
        String subChannel = i.readUTF();

        if (subChannel.equalsIgnoreCase("Connect"))
            return;
    }

    public void connectToServer(Player p, String server) {
        ByteArrayDataOutput o = ByteStreams.newDataOutput();
        o.writeUTF("Connect");
        o.writeUTF(server);
        o.writeUTF(p.getDisplayName());
        p.sendPluginMessage(Main.getInstance(), "BungeeCord", o.toByteArray());
    }
}