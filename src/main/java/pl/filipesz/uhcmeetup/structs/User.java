package pl.filipesz.uhcmeetup.structs;

import org.bukkit.entity.Player;

import java.util.UUID;

public class User {

    private UUID uuid;
    private int kills;
    private double killsPerGame;
    private int gamesPlayed;
    private int wins;
    private int winStreak;

    public User(Player p, int kills, int gamesPlayed, int wins) {
        this.uuid = p.getUniqueId();
        this.kills = kills;
        this.killsPerGame = (double) kills/gamesPlayed;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.winStreak = wins;
    }

    public int getKills() {
        return kills;
    }

    public double getKillsPerGame() {
        return killsPerGame;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public int getWinStreak() {
        return winStreak;
    }
}
