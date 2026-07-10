package pl.filipesz.uhcmeetup.listeners;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.filipesz.uhcmeetup.Main;
import pl.filipesz.uhcmeetup.manager.GameManager;
import pl.filipesz.uhcmeetup.structs.User;
import pl.filipesz.uhcmeetup.structs.UserFile;
import pl.filipesz.uhcmeetup.utils.UtilManager;

import java.io.Console;
import java.text.DecimalFormat;
import java.util.*;

public class GameManagerListener implements Listener {

    public static ArrayList<Player> inGame = new ArrayList<>();
    HashMap<Player, Integer> inGameStats = new HashMap<>();
    BukkitTask startGameTask;
    public static boolean isStarted = false;
    private static final DecimalFormat df = new DecimalFormat("0.0");

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        e.setJoinMessage("");

        // TITLE
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"§8* §3Filipesz §8*\"}"), 40, 60, 40);
        PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"§7Polaczono z trybem §3UHCMEETUP§7...\"}"), 40, 60, 40);
        ((CraftPlayer) e.getPlayer()).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) e.getPlayer()).getHandle().playerConnection.sendPacket(subtitle);

        if (p.isOp()) {
            p.teleport(new Location(p.getWorld(), 0, 100, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);

            p.setGameMode(GameMode.CREATIVE);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setFireTicks(0);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            for (PotionEffect effect : p.getActivePotionEffects()) {
                p.removePotionEffect(effect.getType());
            }
            p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
            UtilManager.sendMessage(p, "&8[&e&l!&8] &8» &eDolaczyles na serwer jako &3OPERATOR&7.");
        }
        if (!isStarted) {
            if (!p.isOp()) {
                if (!UserFile.get().contains("users." + uuid)) {
                    UserFile.get().set("users." + uuid + ".kills", 0);
                    UserFile.get().set("users." + uuid + ".killsPerGame", 0);
                    UserFile.get().set("users." + uuid + ".gamesPlayed", 0);
                    UserFile.get().set("users." + uuid + ".wins", 0);
                    UserFile.get().set("users." + uuid + ".winStreak", 0);
                    UserFile.save();
                }
                inGame.add(p);
                GameManager.prepareToStart(p);
                Bukkit.broadcastMessage(UtilManager.fixColor("&8[&e&l!&8] &8» &7" + p.getName() + " &edolaczyl do gry! &7(&3" + inGame.size() + "&7/&324&7)."));
                if (inGame.size() == 8) {
                    Bukkit.broadcastMessage(UtilManager.fixColor("&8[&e&l!&8] &8» &7Gra rozpocznie sie za &320 sekund!"));
                    startGameTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            isStarted = true;

                            World world = Bukkit.getWorld("world");
                            WorldBorder wb = world.getWorldBorder();
                            wb.setSize(wb.getSize() - 250, 9600L);

                            for (Player inGamePlayers : Bukkit.getOnlinePlayers()) {
                                if (!inGamePlayers.isOp()) {
                                    inGamePlayers.removePotionEffect(PotionEffectType.JUMP);
                                    inGamePlayers.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                                }
                            }
                            for (int i = 0; i < inGame.size(); i++) {
                                UUID uuid = inGame.get(i).getUniqueId();
                                UserFile.addGame(uuid);
                            }
                            for (Player everyone : Bukkit.getOnlinePlayers()) {
                                everyone.playSound(everyone.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
                                UtilManager.sendTitle(everyone, "&8* &3&lUHC &8*");
                                UtilManager.sendSubTitle(everyone, "&3Gra sie rozpoczela!");
                            }
                            Bukkit.broadcastMessage(UtilManager.fixColor("&8[&3&l!&8] &8» &3Gra sie rozpoczela!!!"));
                        }
                    }.runTaskLater(Main.getInstance(), 400L);
                }
                if (inGame.size() == 24) {
                    p.kickPlayer("§cSerwer osiagnal maksymalna ilosc osob na arenie!");
                }
            }
        }
    }

    /*private void cancelTask() {
        Main.getInstance().getServer().getScheduler().cancelTask(startGameTask);
    }*/

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);

        e.setQuitMessage("");

        if (p.isOp() || p.getGameMode() == GameMode.SPECTATOR) {
            e.setQuitMessage("");
        }

        if (!p.isOp()) {
            if (!isStarted) {
                inGame.remove(p);
                Bukkit.broadcastMessage(UtilManager.fixColor("&8[&c&l!&8] &8» &7" + p.getName() + " &cwyszedl. &7(&3" + inGame.size() + "&7/&324&7)"));
                if (inGame.size() == 3) {
                    startGameTask.cancel();
                    Bukkit.broadcastMessage(UtilManager.fixColor("&8[&c&l!&8] &8» &cNiestety jest za malo osob, aby rozpoczac arene :("));
                }
            } else {
                p.getWorld().strikeLightningEffect(e.getPlayer().getLocation());
                p.damage(50.0);
                p.getWorld().dropItemNaturally(p.getLocation(), head);
                inGame.remove(p);
                Bukkit.broadcastMessage(UtilManager.fixColor("&8[&c&l!&8] &8» &7" + p.getName() + " &cwyszedl. &7(&3" + inGame.size() + "&7/&324&7)"));
                if (inGame.size() == 1) {
                    Player winner = inGame.get(0);
                    UserFile.addWin(winner.getUniqueId());
                    for (Player everyone : Bukkit.getOnlinePlayers()) {
                        UtilManager.sendTitle(everyone, "&8* &3&lUHC &8*");
                        UtilManager.sendSubTitle(everyone, "&7" + everyone.getName() + " &3wygrywa arene!");
                    }
                    Bukkit.broadcastMessage(UtilManager.fixColor("&7&m----------&8[ &8* &3&lUHC &8* ]&7&m----------\n" +
                            "&7" + winner.getName() + " &8» &3wygrywa arene GRATULUJEMY!!!\n" +
                            "&7Ilosc zabojstw: " + inGameStats.get(winner)));
                    GameManager.endGameEffects();
                    GameManager.restartServer();
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Player t = e.getEntity().getKiller();

        e.setDeathMessage("");

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);

        if (isStarted) {
            if (p != null) {
                UUID pUuid = p.getUniqueId();
                if (t == null) {
                    p.getWorld().strikeLightningEffect(p.getLocation());
                    p.setGameMode(GameMode.SPECTATOR);
                    inGame.remove(p);
                    UserFile.lostWinStreak(pUuid);
                    Bukkit.broadcastMessage(UtilManager.fixColor("&8[&c&l!&8] &8» &7" + p.getName() + " &cumarl..."));

                    if (inGame.size() == 1) {
                        Player winner = inGame.get(0);
                        UserFile.addWin(winner.getUniqueId());
                        for (Player everyone : Bukkit.getOnlinePlayers()) {
                            UtilManager.sendTitle(everyone, "&8* &3&lUHC &8*");
                            UtilManager.sendSubTitle(everyone, "&7" + everyone.getName() + " &3wygrywa arene!");
                        }
                        Bukkit.broadcastMessage(UtilManager.fixColor("&7&m----------&8[ &8* &3&lUHC &8* ]&7&m----------\n" +
                                "&7" + winner.getName() + " &8» &3wygrywa arene GRATULUJEMY!!!\n" +
                                "&7Ilosc zabojstw: " + inGameStats.get(winner)));
                        GameManager.endGameEffects();
                        GameManager.restartServer();
                    }
                } else {
                    UUID tUuid = t.getUniqueId();
                    double tHealth20 = ((int) (t.getHealth() * 100)) / 100.0;
                    double tHealth10 = tHealth20 / 2;
                    p.getWorld().strikeLightningEffect(p.getLocation());
                    p.setGameMode(GameMode.SPECTATOR);
                    t.getWorld().dropItemNaturally(p.getLocation(), head);
                    inGame.remove(p);
                    UserFile.lostWinStreak(pUuid);
                    inGameStats.put(t, inGameStats.getOrDefault(t, 0) + 1);
                    UserFile.addKill(tUuid);
                    UtilManager.sendSubTitle(t, "&3Zabojstwo!");
                    UtilManager.sendMessage(p, "&8[&c&l!&8] &8» &cZostales wyeliminowany. Zabojcy zostalo &3" + df.format(tHealth10) + "HP");
                    Bukkit.broadcastMessage(UtilManager.fixColor("&8[&c&l!&8] &8» &7" + p.getName() + " &czostal zabity przez &7" + t.getName()));

                    if (inGame.size() == 1) {
                        Player winner = inGame.get(0);
                        UserFile.addWin(winner.getUniqueId());
                        for (Player everyone : Bukkit.getOnlinePlayers()) {
                            UtilManager.sendTitle(everyone, "&8* &3&lUHC &8*");
                            UtilManager.sendSubTitle(everyone, "&7" + everyone.getName() + " &3wygrywa arene!");
                        }
                        Bukkit.broadcastMessage(UtilManager.fixColor("&7&m----------&8[ &8* &3&lUHC &8* ]&7&m----------\n" +
                                "&7" + winner.getName() + " &8» &3wygrywa arene GRATULUJEMY!!!\n" +
                                "&7Ilosc zabojstw: " + inGameStats.get(winner)));
                        GameManager.endGameEffects();
                        GameManager.restartServer();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        if (isStarted) {
            if (!p.isOp()) {
                p.teleport(new Location(p.getWorld(), 0, 100, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);

                p.setGameMode(GameMode.SPECTATOR);
                for (PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                }
                p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
                UtilManager.sendMessage(p, "&8[&e&l!&8] &8» &eZmieniono tryb gry na &3SPECTATOR&7.");

                UUID uuid = p.getUniqueId();
                int kills = UserFile.getKills(uuid);
                double killsPerGame = UserFile.getKillsPerGame(uuid);
                int gamesPlayed = UserFile.getGamesPlayed(uuid);
                int wins = UserFile.getWins(uuid);
                int winStreak = UserFile.getWinStreak(uuid);
                UtilManager.sendMessage(p, "     &7&m----------&8[ &8* &3&lTwoje Statystyki UHC &8* ]&7&m----------");
                UtilManager.sendMessage(p, "&8» &7Zabojstwa (ogolnie): &3" + kills);
                UtilManager.sendMessage(p, "&8» &7Zabojstwa na gre kills/games: &3" + df.format(killsPerGame));
                UtilManager.sendMessage(p, "&8» &7Rozegrane gry &3" + gamesPlayed);
                UtilManager.sendMessage(p, "&8» &7Wygrane &3" + wins);
                UtilManager.sendMessage(p, "&8» &7Aktualny winstreak &3" + winStreak);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (!isStarted) {
            if (inGame.contains(p)) {
                if (e.getAction().equals(Action.RIGHT_CLICK_AIR)
                        || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                        || e.getAction().equals(Action.LEFT_CLICK_AIR)
                        || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    e.setCancelled(true);
                }
            }
        }

        if (p.getItemInHand().getType() == Material.SKULL_ITEM) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                e.setCancelled(true);
                p.removePotionEffect(PotionEffectType.SPEED);
                p.removePotionEffect(PotionEffectType.REGENERATION);
                p.removePotionEffect(PotionEffectType.ABSORPTION);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 140, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 3));
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                p.playSound(p.getLocation(), Sound.EAT, 1, 1);
                ItemStack hand = p.getInventory().getItemInHand();
                hand.setAmount(hand.getAmount() - 1);
                p.getInventory().setItemInHand(hand);
                UtilManager.sendMessage(p, "&8[&3&l!&8] &8» &3Zjadles glowe.");
            }
        }
        ItemStack getItems = new ItemStack(Material.BOOK);
        ItemMeta getItemsMeta = getItems.getItemMeta();
        getItemsMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        getItemsMeta.setDisplayName(UtilManager.fixColor("&3&lOdbierz zestaw!"));
        getItems.setItemMeta(getItemsMeta);

        if (p.getItemInHand().equals(getItems)) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                // RANDOMOWE ZESTAWY DLA KAZDEGO GRACZA
                double rand = Math.random();

                if (rand <= 0.10) { // 10%

                    ItemStack iSword = new ItemStack(Material.IRON_SWORD, 1);
                    ItemStack snowBall = new ItemStack(Material.SNOW_BALL, 8);
                    ItemStack gApple = new ItemStack(Material.GOLDEN_APPLE, 4);
                    ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
                    ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
                    ItemStack cobble = new ItemStack(Material.COBBLESTONE, 64);
                    ItemStack water = new ItemStack(Material.WATER_BUCKET, 1);
                    ItemStack anvil = new ItemStack(Material.ANVIL, 1);
                    ItemStack gNuggets = new ItemStack(Material.GOLD_NUGGET, 6);

                    p.getInventory().setItem(0, iSword);
                    p.getInventory().setItem(1, snowBall);
                    p.getInventory().setItem(2, gApple);
                    p.getInventory().setItem(4, anvil);
                    p.getInventory().setItem(5, pickaxe);
                    p.getInventory().setItem(6, steak);
                    p.getInventory().setItem(7, cobble);
                    p.getInventory().setItem(8, water);
                    p.getInventory().setItem(9, gNuggets);
                    p.setExp(10L);

                    // SET
                    ItemStack iHelmet = new ItemStack(Material.IRON_HELMET, 1);
                    ItemStack dChestPlate = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
                    dChestPlate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack iLeggings = new ItemStack(Material.IRON_LEGGINGS, 1);
                    ItemStack iBoots = new ItemStack(Material.IRON_BOOTS, 1);
                    iBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

                    ItemStack[] armor = new ItemStack[4];

                    armor[0] = (iBoots);
                    armor[1] = (iLeggings);
                    armor[2] = (dChestPlate);
                    armor[3] = (iHelmet);

                    p.getInventory().setArmorContents(armor);

                } else if (rand <= 0.25) { // 15%

                    ItemStack dSword = new ItemStack(Material.IRON_SWORD);
                    ItemStack snowBall = new ItemStack(Material.SNOW_BALL, 8);
                    ItemStack gApple = new ItemStack(Material.GOLDEN_APPLE, 4);
                    ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
                    ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
                    ItemStack cobble = new ItemStack(Material.COBBLESTONE, 64);
                    ItemStack water = new ItemStack(Material.WATER_BUCKET, 1);
                    ItemStack anvil = new ItemStack(Material.ANVIL, 1);
                    ItemStack gNuggets = new ItemStack(Material.GOLD_NUGGET, 4);

                    p.getInventory().setItem(0, dSword);
                    p.getInventory().setItem(1, snowBall);
                    p.getInventory().setItem(2, gApple);
                    p.getInventory().setItem(4, anvil);
                    p.getInventory().setItem(5, pickaxe);
                    p.getInventory().setItem(6, steak);
                    p.getInventory().setItem(7, cobble);
                    p.getInventory().setItem(8, water);
                    p.getInventory().setItem(9, gNuggets);
                    p.setExp(7L);

                    // SET
                    ItemStack iHelmet = new ItemStack(Material.IRON_HELMET, 1);
                    iHelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack iChestPlate = new ItemStack(Material.IRON_CHESTPLATE, 1);
                    ItemStack dLeggings = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
                    dLeggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack iBoots = new ItemStack(Material.IRON_BOOTS, 1);

                    ItemStack[] armor = new ItemStack[4];

                    armor[0] = (iBoots);
                    armor[1] = (dLeggings);
                    armor[2] = (iChestPlate);
                    armor[3] = (iHelmet);

                    p.getInventory().setArmorContents(armor);

                } else if (rand <= 0.60) { // 35%

                    ItemStack iSword = new ItemStack(Material.DIAMOND_SWORD, 1);
                    ItemStack snowBall = new ItemStack(Material.SNOW_BALL, 10);
                    ItemStack gApple = new ItemStack(Material.GOLDEN_APPLE, 5);
                    ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
                    ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
                    ItemStack cobble = new ItemStack(Material.COBBLESTONE, 64);
                    ItemStack water = new ItemStack(Material.WATER_BUCKET, 1);
                    ItemStack anvil = new ItemStack(Material.ANVIL, 1);
                    ItemStack gNuggets = new ItemStack(Material.GOLD_NUGGET, 3);

                    p.getInventory().setItem(0, iSword);
                    p.getInventory().setItem(1, snowBall);
                    p.getInventory().setItem(2, gApple);
                    p.getInventory().setItem(4, anvil);
                    p.getInventory().setItem(5, pickaxe);
                    p.getInventory().setItem(6, steak);
                    p.getInventory().setItem(7, cobble);
                    p.getInventory().setItem(8, water);
                    p.getInventory().setItem(9, gNuggets);
                    p.setExp(5L);

                    // SET
                    ItemStack iHelmet = new ItemStack(Material.IRON_HELMET, 1);
                    ItemStack iChestPlate = new ItemStack(Material.IRON_CHESTPLATE, 1);
                    iChestPlate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack iLeggings = new ItemStack(Material.IRON_LEGGINGS, 1);
                    ItemStack dBoots = new ItemStack(Material.DIAMOND_BOOTS, 1);
                    dBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

                    ItemStack[] armor = new ItemStack[4];

                    armor[0] = (dBoots);
                    armor[1] = (iLeggings);
                    armor[2] = (iChestPlate);
                    armor[3] = (iHelmet);

                    p.getInventory().setArmorContents(armor);

                } else { // 40%

                    ItemStack dSword = new ItemStack(Material.DIAMOND_SWORD, 1);
                    ItemStack snowBall = new ItemStack(Material.SNOW_BALL, 12);
                    ItemStack gApple = new ItemStack(Material.GOLDEN_APPLE, 5);
                    ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
                    ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
                    ItemStack cobble = new ItemStack(Material.COBBLESTONE, 64);
                    ItemStack water = new ItemStack(Material.WATER_BUCKET, 1);
                    ItemStack anvil = new ItemStack(Material.ANVIL, 1);
                    ItemStack gNuggets = new ItemStack(Material.GOLD_NUGGET, 2);

                    p.getInventory().setItem(0, dSword);
                    p.getInventory().setItem(1, snowBall);
                    p.getInventory().setItem(2, gApple);
                    p.getInventory().setItem(4, anvil);
                    p.getInventory().setItem(5, pickaxe);
                    p.getInventory().setItem(6, steak);
                    p.getInventory().setItem(7, cobble);
                    p.getInventory().setItem(8, water);
                    p.getInventory().setItem(9, gNuggets);
                    p.setExp(3L);

                    // SET
                    ItemStack dHelmet = new ItemStack(Material.DIAMOND_HELMET, 1);
                    dHelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack iChestPlate = new ItemStack(Material.IRON_CHESTPLATE, 1);
                    ItemStack iLeggings = new ItemStack(Material.IRON_LEGGINGS, 1);
                    iLeggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack iBoots = new ItemStack(Material.IRON_BOOTS, 1);

                    ItemStack[] armor = new ItemStack[4];

                    armor[0] = (iBoots);
                    armor[1] = (iLeggings);
                    armor[2] = (iChestPlate);
                    armor[3] = (dHelmet);

                    p.getInventory().setArmorContents(armor);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        World w = Bukkit.getWorld("world");
        WorldBorder wb = w.getWorldBorder();

        if (p.isOp()) {
            e.setCancelled(false);
        }

        if (!isStarted) {
            if (inGame.contains(p)) {
                if (e.getTo().getBlockX() != e.getFrom().getBlockX() ||
                        e.getTo().getBlockY() != e.getFrom().getBlockY() ||
                        e.getTo().getBlockZ() != e.getFrom().getBlockZ()) {
                    p.teleport(p.getLocation());
                    e.setCancelled(true);
                }
            }
        } else {
            if (inGame.contains(p)) {
                if (loc.getBlockX() > wb.getSize() / 2 - 10 || loc.getBlockX() < wb.getSize() / 2 - wb.getSize() + 10 || loc.getBlockZ() > wb.getSize() / 2 - 10 || loc.getBlockZ() < wb.getSize() / 2 - wb.getSize() + 10) {
                    p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                    UtilManager.sendActionbar(p, "&cJestes bardzo blisko borderu. UCIEKAJ!!!");
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Player p = (Player) e.getEntity();
        if (!isStarted) {
            if (p != null) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(false);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String cmd = e.getMessage();

        String[] args = cmd.split(" ");
        if (args[0].equalsIgnoreCase("/op")) {
            e.setCancelled(true);
            UtilManager.sendMessage(p, "&8[&c&l!&8] &8» &cNie mozesz dac opa graczowi w trakcie gry!");
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();

        if (!p.isOp()) {
            if (isStarted) {
                e.setKickMessage("§cGra juz wystartowala. Za niedlugo rozpocznie sie nowa gra. Nie spoznij sie ;)");
            }
            if (inGame.size() == 24) {
                e.setKickMessage("§cSerwer osiagnal maksymalna ilosc osob na arenie!");
            }
        }
        if (e.getResult() == PlayerLoginEvent.Result.KICK_BANNED) {
            e.setKickMessage("§cZostales zbanowany.");
        }
        if (e.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
            e.setKickMessage("§cSerwer jest zajety!");
        }
        if (e.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            e.setKickMessage("§cSerwer jest pelny!");
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        Player p = e.getPlayer();

        if (p.isOp()) {
            e.setFormat(UtilManager.fixColor("&4&lOP &4%s &8» &4%s")); // OP PREFIX
        } else if (p.isOp() && p.getGameMode() == GameMode.SPECTATOR) {
            e.setFormat(UtilManager.fixColor("&fSPECTATOR &4&lOP &4%s &8» &4%s")); // OP PREFIX
        } else if (!p.isOp() && p.getGameMode() == GameMode.SPECTATOR) {
            e.setFormat(UtilManager.fixColor("&fSPECTATOR &7Na arenie %s &8» &7%s")); // NA ARENIE PREFIX
        }
        if (inGame.contains(p)) {
            e.setFormat(UtilManager.fixColor("&7Na arenie %s &8» &7%s")); // NA ARENIE PREFIX
        }
    }
}