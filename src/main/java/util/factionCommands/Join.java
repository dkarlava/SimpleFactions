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
import java.util.List;
import java.util.UUID;

public record Join(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        // TODO: Infer join the faction that invited most recently if no faction name is provided
        if (args.length != 1) {
            sender.sendMessage("Usage: /f join <factionName>");
            return;
        }
        String factionName = args[0];
        UUID playerId = player.getUniqueId();

        if (connection.selectFactionByPlayerId(playerId) != null) {
            sender.sendMessage(Component.text("You must leave your current faction to join another one.").color(NamedTextColor.RED));
            return;
        }

        Faction factionDetails = connection.selectFactionByName(factionName);

        FactionInvite isInvitePrimed = connection.selectFactionInvitePrimed(factionDetails.id, playerId);

        // Attempting to filter out every case in which /f join command should be ignored
        if (isInvitePrimed == null) {
            sender.sendMessage(Component.text("You do not have a pending invite from that faction.").color(NamedTextColor.RED));
            return;
        }
        if (System.currentTimeMillis() - isInvitePrimed.timestamp > (long) config.factionInviteAutoTimeout * 60 * 1000) {
            sender.sendMessage(Component.text(String.format("Your invite to %s has expired", factionDetails.name), NamedTextColor.RED));
            return;
        }

        connection.deleteFactionInvite(playerId, factionDetails.id);
        connection.insertFactionMember(factionDetails.id, playerId);

        sender.sendMessage(Component.text(String.format("You have joined %s!", factionDetails.name), NamedTextColor.GREEN));
        List<FactionPlayer> otherMembers = connection.selectAllFactionMembersUsingFactionId(factionDetails.id);
        for (FactionPlayer otherPlayers : otherMembers) {
            if (!otherPlayers.playerId.equals(playerId)) {
                Player otherPlayer = Bukkit.getPlayer(otherPlayers.playerId);
                if (otherPlayer != null) {
                    otherPlayer.sendMessage(Component.text(String.format("%s has joined the faction!", player.getName()), NamedTextColor.GREEN));
                }
            }
        }
    }
}