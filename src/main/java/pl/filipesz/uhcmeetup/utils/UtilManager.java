package pl.filipesz.uhcmeetup.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import pl.filipesz.uhcmeetup.Main;

import java.text.DecimalFormat;
import java.util.UUID;

import static pl.filipesz.uhcmeetup.listeners.GameManagerListener.inGame;

@SuppressWarnings("ALL")
public class UtilManager {

    //ChatUtil
    public static String fixColor(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void sendMessage(CommandSender commandSender, String string) {
        commandSender.sendMessage(fixColor(string));
    }

    private static void sendPacket(Player p, Packet packet) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendActionbar(Player p, String msg) {
        IChatBaseComponent cmp = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', msg) + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(cmp, (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(bar);
    }

    public static void sendTitle(Player p, String title) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(fixColor("{\"text\": \"" + title + "\"}"));
        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, iChatBaseComponent);
        PacketPlayOutTitle length = new PacketPlayOutTitle(40, 60, 40);
        sendPacket(p, packetPlayOutTitle);
        sendPacket(p, length);
    }

    public static void sendSubTitle(Player p, String subtitle) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(fixColor("{\"text\": \"" + subtitle + "\"}"));
        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChatBaseComponent);
        PacketPlayOutTitle length = new PacketPlayOutTitle(40, 60, 40);
        sendPacket(p, packetPlayOutTitle);
        sendPacket(p, length);
    }

    // Clear Items
    public static void clearItems() {
        org.bukkit.World[] worlds = Bukkit.getServer().getWorlds().toArray(new org.bukkit.World[0]);
        org.bukkit.World[] array;

        for (int length = (array = worlds).length, i = 0; i < length; i++) {
            World world = array[i];

            for (org.bukkit.entity.Entity e : world.getEntities()) {
                if (!(e instanceof Item)
                        && !(e instanceof FallingBlock)
                        && !(e instanceof Arrow)) {
                    continue;
                }
                e.remove();
            }
        }
    }

    // ScoreboardUtil
    private static final DecimalFormat dfBorder = new DecimalFormat("0");

    public static void createScoreboard(Player p) {
        ScoreboardManager m = Bukkit.getScoreboardManager();
        Scoreboard b = m.getNewScoreboard();
        Objective o = b.registerNewObjective("Stats", "dummy");
        o.setDisplayName(UtilManager.fixColor("&3&lUHCMEETUP &8[&3SOLO&8]"));
        o.setDisplaySlot(DisplaySlot.SIDEBAR);

        World world = Bukkit.getWorld("world");

        UUID uuid = p.getUniqueId();
        int players = inGame.size();
        int ping = ((CraftPlayer) p).getHandle().ping;

        Score s = o.getScore(UtilManager.fixColor(""));
        s.setScore(8);

        Score s2 = o.getScore(UtilManager.fixColor("&7Pozostali gracze:"));
        s2.setScore(7);

        Score s3 = o.getScore(UtilManager.fixColor("&8» &3" + players + "&7/&324"));
        s3.setScore(6);

        Score s4 = o.getScore(UtilManager.fixColor(" "));
        s4.setScore(5);

        Score s5 = o.getScore(UtilManager.fixColor("&7Border:"));
        s5.setScore(4);

        Score s6 = o.getScore(UtilManager.fixColor("&8» &3+" + UtilManager.dfBorder.format(world.getWorldBorder().getSize() / 2.0) + " -" + UtilManager.dfBorder.format(world.getWorldBorder().getSize() / 2.0)));
        s6.setScore(3);

        Score s7 = o.getScore(UtilManager.fixColor("   "));
        s7.setScore(2);

        Score s8 = o.getScore(UtilManager.fixColor("&7Faza testowa &3&lFILIPESZ-CORE"));
        s8.setScore(1);

        p.setScoreboard(b);
    }

    public static void updateScoreboard() {
        for (Player everyone : Bukkit.getOnlinePlayers()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    createScoreboard(everyone);
                }
            }.runTaskTimer(Main.getInstance(), 40L, 40L);
        }
    }
}
