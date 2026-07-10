package pl.filipesz.uhcmeetup.manager;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.filipesz.uhcmeetup.Main;
import pl.filipesz.uhcmeetup.utils.UtilManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("ALL")
public class GameManager {

    public static void generateMap() {
        Bukkit.setWhitelist(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.setWhitelist(false);
            }
        }.runTaskLater(Main.getInstance(), 300L);
    }

    public static void prepareToStart(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        ItemStack getItems = new ItemStack(Material.BOOK);
        ItemMeta getItemsMeta = getItems.getItemMeta();
        getItemsMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        getItemsMeta.setDisplayName(UtilManager.fixColor("&3&lOdbierz zestaw!"));
        getItems.setItemMeta(getItemsMeta);
        p.getInventory().setItem(0, getItems);

        int x = ThreadLocalRandom.current().nextInt(0, 125);
        int z = ThreadLocalRandom.current().nextInt(0, 125);
        World worldLoc = p.getWorld();
        Location randomLocation = new Location(p.getWorld(), x, worldLoc.getHighestBlockYAt(x, z), z);
        p.teleport(randomLocation);

        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setFireTicks(0);
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }

        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
        p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
    }

    public static void endGameEffects() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player everyone : Bukkit.getOnlinePlayers()) {
                    everyone.playSound(everyone.getLocation(), Sound.FIREWORK_BLAST, 1, 1);
                    everyone.playEffect(everyone.getLocation(), Effect.FIREWORKS_SPARK, 1);
                }
            }
        }.runTaskTimer(Main.getInstance(), 100L, 100L);
    }

    public static void restartServer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                UtilManager.clearItems();
                Bukkit.unloadWorld("world", true);
                File worldFolder = new File("world");
                try {
                    FileUtils.deleteDirectory(worldFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (Player everyone : Bukkit.getOnlinePlayers()) {
                    everyone.kickPlayer("Zaraz Wracamy!");
                }
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }.runTaskLater(Main.getInstance(), 200L);
    }
}
