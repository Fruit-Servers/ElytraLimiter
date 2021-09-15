package me.scyphers.fruitservers.elytralimiter.command.commands;

import me.scyphers.scycore.BasePlugin;
import me.scyphers.scycore.command.BaseCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DebugCommand extends BaseCommand {

    public DebugCommand(BasePlugin plugin, String permission, int minArgLength) {
        super(plugin, permission, minArgLength);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
