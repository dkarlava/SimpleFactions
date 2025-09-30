package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.PluginConfig;
import util.BaseFactionCommand;
import java.sql.SQLException;
import java.util.UUID;

public record Leave (PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
        }
        if (args.length != 0) {
            sender.sendMessage("Usage: /f leave");
            return;
        }

        assert sender instanceof Player;
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (connection.selectFactionByPlayerId(playerId) == null) {
            sender.sendMessage(Component.text("You must be in a faction to leave.").color(NamedTextColor.RED));
            return;
        }

        connection.deleteFactionMember(playerId);
        sender.sendMessage(Component.text("You have left the faction.").color(NamedTextColor.GRAY));
    }
}
