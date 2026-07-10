package pl.filipesz.uhcmeetup.structs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UserFile {

    private static File file;
    private static FileConfiguration config;

    public static void setup(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "users.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return config;
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getKills(UUID uuid) {
        return config.getInt("users." + uuid + ".kills");
    }

    public static void addKill(UUID uuid) {
        int kill = getKills(uuid);
        config.set("users." + uuid + ".kills", kill + 1);
        save();
    }

    public static double getKillsPerGame(UUID uuid) {
        return config.getInt("users." + uuid + ".killsPerGame");
    }

    public static int getGamesPlayed(UUID uuid) {
        return config.getInt("users" + uuid + ".gamesPlayed");
    }

    public static void addGame(UUID uuid) {
        int game = getGamesPlayed(uuid);
        config.set("users." + uuid + ".gamesPlayed", game + 1);
        save();
    }

    public static int getWins(UUID uuid) {
        return config.getInt("users." + uuid + ".wins");
    }

    public static void addWin(UUID uuid) {
        int win = getWins(uuid);
        config.set("users." + uuid + ".wins", win + 1);
        save();
    }

    public static int getWinStreak(UUID uuid) {
        return config.getInt("users." + uuid + ".winStreak");
    }

    public static void lostWinStreak(UUID uuid) {
        config.set("users." + uuid + ".winStreak", 0);
    }
}
