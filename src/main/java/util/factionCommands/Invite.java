package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.Faction;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;

import java.sql.SQLException;
import java.util.UUID;

public record Invite (PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("Usage: /f invite <factionName>");
            return;
        }

        UUID playerId = player.getUniqueId();

        FactionPlayer factionPlayer = connection.selectFactionPlayerMember(playerId);
        if (factionPlayer == null) {
            sender.sendMessage(Component.text("You must be in a faction to invite other players.").color(NamedTextColor.RED));
            return;
        }
        if (factionPlayer.rank != PlayerRank.Owner && factionPlayer.rank != PlayerRank.CoOwner) {
            sender.sendMessage(Component.text("Only faction owners and co-owners can invite other players").color(NamedTextColor.RED));
            return;
        }

        String otherPlayerName = args[0];
        Player otherPlayer = Bukkit.getPlayer(otherPlayerName);

        if (otherPlayer == null) {
            sender.sendMessage(Component.text(String.format("%s not found.", otherPlayerName), NamedTextColor.RED));
            return;
        }
        Faction factionDetails = connection.selectFactionPlayerIsIn(playerId);
        if (factionDetails == null) {
            sender.sendMessage(Component.text(String.format("Internal Error. Could not get faction details for faction id %s", factionPlayer.factionId), NamedTextColor.RED));
            return;
        }

        // TODO: Need to check for the following validations
        // If the receiving player already has an invite to the faction just update the timeout timestamp
        // TODO: Need to add config variable for invite timeout
        connection.insertPlayerInvite(factionPlayer.factionId, otherPlayer.getUniqueId());

        otherPlayer.sendMessage(Component.text(String.format("%s has invited you to join %s. The invite will be valid for the next %d minutes.", player.getName(), factionDetails.name, 5), NamedTextColor.GOLD));
        sender.sendMessage(Component.text(String.format("%s was invited.", otherPlayerName), NamedTextColor.GOLD));
    }
}