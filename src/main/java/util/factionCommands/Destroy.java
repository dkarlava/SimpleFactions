package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.*;
import util.BaseFactionCommand;
import java.sql.SQLException;

/**
 * This is an op only utility command that will instantly delete everything related to a faction.
 * This can be helpful if a faction gets into a weird state and needs to just be removed,
 * but can be dangerous since there is no conformation, it will just do it
 */
public record Destroy(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (!player.isOp()) {
            sender.sendMessage(Component.text("You do not have permission to execute this command!", NamedTextColor.RED));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("Usage: /f destroy <factionName>");
            return;
        }

        Faction faction = connection.selectFactionByName(args[0]);
        if (faction == null) {
            player.sendMessage(Component.text("That faction does not exist!", NamedTextColor.RED));
            return;
        }

        String factionName = connection.deleteFaction(faction.id);
        Bukkit.broadcast(Component.text(String.format("%s just destroyed the faction: %s", player.getName(), factionName)).color(NamedTextColor.RED));
    }
}
