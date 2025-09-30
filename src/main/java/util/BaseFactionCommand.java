package util;

import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public interface BaseFactionCommand {
    void execute(CommandSender sender, String[] args) throws SQLException;
}
