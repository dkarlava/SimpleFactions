package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import org.bukkit.command.CommandSender;
import types.Faction;
import types.PluginConfig;
import util.BaseFactionCommand;

import java.sql.SQLException;
import java.util.List;

public record ListCommand(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (args.length != 0) {
            sender.sendMessage("Usage: /f list");
            return;
        }

        List<Faction> factions = connection.getAllFactions();
        sender.sendMessage("Faction List:");
        for (Faction faction : factions) {
            sender.sendMessage(faction.name);
        }
    }
}
