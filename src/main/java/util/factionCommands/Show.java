package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.Faction;
import types.PluginConfig;
import util.BaseFactionCommand;

import java.sql.SQLException;
import java.util.UUID;

public record Show (PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
        }
        if (args.length != 0) {
            sender.sendMessage("Usage: /f create <factionName>");
            return;
        }

        assert sender instanceof Player;
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        Faction factionDetails = connection.selectFactionPlayerIsIn(playerId);

        if (factionDetails == null) {
            sender.sendMessage(Component.text("That faction does not exist!"));
        } else {
            sender.sendMessage(Component.text(factionDetails.name));
        }
    }
}
