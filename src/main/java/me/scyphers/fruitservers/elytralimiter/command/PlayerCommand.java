package me.scyphers.fruitservers.elytralimiter.command;

import me.scyphers.fruitservers.elytralimiter.ElytraLimiter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerCommand extends BaseCommand {

    public PlayerCommand(ElytraLimiter plugin, String permission, int minArgLength) {
        super(plugin, permission, minArgLength);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            m.msg(sender, "errorMessages.mustBePlayer");
            return true;
        }
        return onCommand(player, args);
    }

    public abstract boolean onCommand(Player player, String[] args);
}
