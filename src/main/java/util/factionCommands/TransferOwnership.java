package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;
import util.other.BroadcastMessageToFactionMembers;

import java.sql.SQLException;
import java.util.UUID;

public record TransferOwnership (PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("Usage: /f transferOwnership <playerName>");
            return;
        }

        UUID playerId = player.getUniqueId();

        FactionPlayer factionPlayer = connection.selectFactionPlayerMember(playerId);
        if (factionPlayer == null) {
            sender.sendMessage(Component.text("You must be in a faction to transfer ownership.").color(NamedTextColor.RED));
            return;
        }

        if (factionPlayer.rank != PlayerRank.Owner) {
            sender.sendMessage(Component.text("Only the owner can transfer ownership.").color(NamedTextColor.RED));
            return;
        }

        String otherPlayerName = args[0];
        OfflinePlayer maybeOtherPlayer = Bukkit.getOfflinePlayer(otherPlayerName.toLowerCase());
        if (!maybeOtherPlayer.hasPlayedBefore()) {
            // TODO: Error?
            return;
        }
        UUID otherPlayerId = maybeOtherPlayer.getUniqueId();
        FactionPlayer otherFactionPlayer = connection.selectFactionPlayerMember(otherPlayerId);
        if (otherFactionPlayer == null || !otherFactionPlayer.factionId.equals(factionPlayer.factionId)) {
            sender.sendMessage(Component.text("That player is not in your faction.", NamedTextColor.RED));
            return;
        }
        connection.updateFactionMemberRank(otherPlayerId, PlayerRank.Owner);
        connection.updateFactionMemberRank(playerId, PlayerRank.CoOwner);
        BroadcastMessageToFactionMembers.run(connection, factionPlayer.factionId, Component.text(String.format("%s has transferred ownership of the faction to %s! ", player.getName(), maybeOtherPlayer.getName()), NamedTextColor.GREEN));
    }
}
