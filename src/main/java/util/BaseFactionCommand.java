package util;

import org.bukkit.command.CommandSender;

public interface BaseFactionCommand {
    void execute(CommandSender sender, String[] args);
}
