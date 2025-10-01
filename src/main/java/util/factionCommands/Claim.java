package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.Faction;
import types.FactionChunk;
import types.FactionPlayer;
import types.PluginConfig;
import util.BaseFactionCommand;
import util.other.BroadcastMessageToFactionMembers;
import util.other.GetTotalLandData;
import java.sql.SQLException;
import java.util.Objects;

public record Claim(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }

        Chunk chunk = player.getChunk();
        int x = chunk.getX();
        int z = chunk.getZ();

        // TODO: support /f claim radius
        // Special handling for claiming land for warzone/ safezone
        if (args.length == 1) {
            if (!player.isOp()) {
                // only oped players can claim for warzone/ safezone
                return;
            }
            String safeZoneWarZoneName = args[0];
            if  (safeZoneWarZoneName.equalsIgnoreCase("safezone")) {
                try {
                    connection.createFactionSafezoneChunk(x, z);
                } catch (SQLException e) {
                    connection.updateFactionSafezoneChunk(x, z);
                }
                player.sendMessage(Component.text("You claimed land for the safe zone!", NamedTextColor.GOLD));
            } else if (safeZoneWarZoneName.equalsIgnoreCase("warzone")) {
                try {
                    connection.createFactionWarzoneChunk(x, z);
                } catch (SQLException e) {
                    connection.updateFactionWarzoneChunk(x, z);
                }
                player.sendMessage(Component.text("You claimed land for the safe zone!", NamedTextColor.GOLD));
            }
            return;
        }

        // TODO: support /f claim radius
        if (args.length != 0) {
            sender.sendMessage("Usage: /f claim");
            return;
        }

        FactionPlayer factionPlayer = connection.selectFactionPlayerMember(player.getUniqueId());
        if (factionPlayer == null) {
            sender.sendMessage(Component.text("You must be in a faction to claim land", NamedTextColor.RED));
            return;
        }

        FactionChunk factionChunk = connection.selectFactionUsingChunk(x, z);
        if (factionChunk != null) {
            if (factionChunk.factionName.equalsIgnoreCase("safezone")) {
                sender.sendMessage(Component.text("You cannot claim the safezone.", NamedTextColor.RED));
                return;
            }
            if (factionChunk.factionName.equalsIgnoreCase("warzone")) {
                sender.sendMessage(Component.text("You cannot claim the warzone.", NamedTextColor.RED));
                return;
            }
            if (Objects.equals(factionChunk.factionId, factionPlayer.factionId)) {
                sender.sendMessage(Component.text("Your faction already owns this land!", NamedTextColor.GREEN));
            } else {
                GetTotalLandData.GetTotalLandDataReturn claimData = GetTotalLandData.run(connection, factionChunk.factionId);
                if (claimData.totalLand > claimData.totalPower) {
                    Claim.claimLand(connection, factionPlayer.factionId, x, z, config.factionLandClaimCost, player, factionChunk.factionId);
                } else {
                    sender.sendMessage(Component.text(String.format("This land is already owned by %s", factionChunk.factionName), NamedTextColor.RED));
                }
            }
            return;
        }

        Claim.claimLand(connection, factionPlayer.factionId, x, z, config.factionLandClaimCost, player, null);
    }
    public static void claimLand (DataBaseHelper connection, String factionId, int x, int z, int claimCost, Player claimer, String overClaimedFactionId) throws SQLException {
        GetTotalLandData.GetTotalLandDataReturn claimData = GetTotalLandData.run(connection, factionId);
        if (claimData.totalPower - claimData.totalLand < claimCost) {
            claimer.sendMessage(Component.text("Your faction does not have enough power to claim this land", NamedTextColor.RED));
            return;
        }
        if (overClaimedFactionId != null) {
            connection.updateFactionChunk(factionId, x, z);
            Faction overclaimedFaction = connection.selectFaction(overClaimedFactionId);
            BroadcastMessageToFactionMembers.run(connection, factionId, Component.text(String.format("%s just claimed land from %s", claimer.getName(), overclaimedFaction.name), NamedTextColor.GREEN));
            Faction claimingFaction = connection.selectFactionByPlayerId(claimer.getUniqueId());
            BroadcastMessageToFactionMembers.run(connection, overclaimedFaction.id, Component.text(String.format("Your faction just lost a land claim at (%d, %d)! to %s", x, z, claimingFaction.name), NamedTextColor.GREEN));
        } else {
            BroadcastMessageToFactionMembers.run(connection, factionId, Component.text(String.format("%s just claimed land for the faction!", claimer.getName()), NamedTextColor.GREEN));
            connection.createFactionChunk(factionId, x, z);
        }
    }
}