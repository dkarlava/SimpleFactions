package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.PluginConfig;
import util.BaseFactionCommand;
import java.sql.SQLException;
import java.util.UUID;

public record Create (PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("Usage: /f create <factionName>");
            return;
        }

        UUID playerId = player.getUniqueId();

        if (connection.selectFactionPlayerIsIn(playerId) != null) {
            sender.sendMessage(Component.text("You must leave your current faction before you can create a new one.").color(NamedTextColor.RED));
            return;
        }

        String factionName = args[0];
        if (factionName.length() < config.minFactionNameLength || factionName.length() > config.maxFactionNameLength) {
            sender.sendMessage(String.format("Faction name must be between %d and %d characters.", config.minFactionNameLength, config.maxFactionNameLength));
            return;
        }
        if (!config.factionNameRegex.matcher(factionName).matches()) {
            sender.sendMessage(String.format("Faction name must match the following regex: %s.", config.factionNameRegex));
            return;
        }
        connection.insertFaction(factionName, playerId);
        Bukkit.broadcast(Component.text(String.format("%s just created the faction: %s", player.getName(), factionName)).color(NamedTextColor.GREEN));
    }
}
