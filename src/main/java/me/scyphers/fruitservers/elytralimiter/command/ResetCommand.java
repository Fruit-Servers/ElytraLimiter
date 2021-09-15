package me.scyphers.fruitservers.elytralimiter.command;

import me.scyphers.fruitservers.elytralimiter.api.ElytraTracker;
import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ResetCommand extends BaseCommand {

    private final ElytraTracker tracker;

    public ResetCommand(BasePlugin plugin, String permission, ElytraTracker tracker) {
        super(plugin, permission, 2);
        this.tracker = tracker;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore() && !player.isOnline()) {
            m.msg(sender, "errorMessages.playerNotFound", "%player%", args[1]);
            return true;
        }

        tracker.setElytraAmount(player.getUniqueId(), 0);
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return null;
    }
}
