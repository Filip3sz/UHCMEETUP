package pl.filipesz.uhcmeetup.cmd;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.filipesz.uhcmeetup.utils.UtilManager;

import static pl.filipesz.uhcmeetup.listeners.GameManagerListener.inGame;

public class ListCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            ConsoleCommandSender p = (ConsoleCommandSender) sender;
            UtilManager.sendMessage(p, "&8[&e&l!&8] &8» &7Pozostalych graczy: &3" + inGame.size() + "&7/&324");
            return true;
        }
        Player p = (Player) sender;
        UtilManager.sendMessage(p, "&8[&e&l!&8] &8» &7Pozostalych graczy: &3" + inGame.size() + "&7/&324");
        return true;
    }
}
