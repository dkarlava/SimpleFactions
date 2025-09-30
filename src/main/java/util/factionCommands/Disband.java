package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionDisband;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;

import java.sql.SQLException;
import java.util.UUID;

public record Disband (PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length != 0) {
            sender.sendMessage("Usage: /f create <factionName>");
            return;
        }

        UUID playerId = player.getUniqueId();

        if (connection.selectFactionPlayerIsIn(playerId) == null) {
            sender.sendMessage(Component.text("You must be in a faction to disband one.").color(NamedTextColor.RED));
            return;
        }

        FactionPlayer factionPlayer = connection.selectFactionPlayerMember(playerId);
        if (factionPlayer == null || factionPlayer.rank != PlayerRank.Owner) {
            sender.sendMessage(Component.text("Only the owner can disband a faction.").color(NamedTextColor.RED));
            return;
        }

        FactionDisband isDisbandedPrimed = connection.selectFactionDisbandedPrimed(factionPlayer.factionId);

        // Attempting to filter out every case in which the first /f disband command should either be created or reset
        if (isDisbandedPrimed == null || !isDisbandedPrimed.playerId.equals(playerId) || System.currentTimeMillis() - isDisbandedPrimed.timestamp > config.factionDisbandAutoTimeout * 60 * 1000) {
            if (isDisbandedPrimed == null) {
                connection.insertFactionDisbandedPrimed(playerId, factionPlayer.factionId);
            } else {
                connection.updateFactionDisbandTimestampAndPlayerId(isDisbandedPrimed.factionDisbandId, playerId);
            }

            sender.sendMessage(
                Component.text("WARNING: Disbanding a faction will remove all members and unclaim all lands. Please confirm you want to disband the faction by typing ", NamedTextColor.GOLD)
                .append(Component.text("/f disband", NamedTextColor.RED))
                .append(Component.text(String.format(". This action will auto cancel in %d minutes.", config.factionDisbandAutoTimeout), NamedTextColor.GOLD)));
        } else {
            String factionName = connection.deleteFactionDisband(isDisbandedPrimed.factionDisbandId);
            Bukkit.broadcast(Component.text(String.format("%s just disbanded the faction: %s", player.getName(), factionName)).color(NamedTextColor.RED));
        }
    }
}
