package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.*;
import util.BaseFactionCommand;
import util.other.BroadcastMessageToFactionMembers;
import util.other.GetTotalLandData;
import java.sql.SQLException;
import java.util.Objects;

// TODO: claim and unclaim radius get weird if there are other claims in the radius
public record Claim(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length > 2) {
            sender.sendMessage("Usage: /f claim");
            return;
        }

        Chunk chunk = player.getChunk();
        int centerX = chunk.getX();
        int centerZ = chunk.getZ();

        if (args[0].equalsIgnoreCase("safezone") || args[0].equalsIgnoreCase("warzone")) {
            if (!player.isOp()) {
                // only oped players can claim for warzone/ safezone
                return;
            }
            String safeZoneWarZoneName = args[0];
            int radius = 0;
            try {
                radius = Integer.parseInt(args[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // no-op
            }
            if  (safeZoneWarZoneName.equalsIgnoreCase("safezone")) {
                try {
                    if (radius == 0) {
                        connection.createFactionSafezoneChunk(centerX, centerZ);
                    } else {
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                connection.createFactionSafezoneChunk(x, z);
                            }
                        }
                    }
                } catch (SQLException e) {
                    if (radius == 0) {
                        connection.updateFactionSafezoneChunk(centerX, centerZ);
                    } else {
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                connection.updateFactionSafezoneChunk(x, z);
                            }
                        }
                    }
                }
                player.sendMessage(Component.text("You claimed land for the safe zone!", NamedTextColor.GOLD));
            } else if (safeZoneWarZoneName.equalsIgnoreCase("warzone")) {
                try {
                    if (radius == 0) {
                        connection.createFactionWarzoneChunk(centerX, centerZ);
                    } else {
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                connection.createFactionWarzoneChunk(x, z);
                            }
                        }
                    }
                } catch (SQLException e) {
                    if (radius == 0) {
                        connection.updateFactionWarzoneChunk(centerX, centerZ);
                    } else {
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                connection.updateFactionWarzoneChunk(x, z);
                            }
                        }
                    }
                }
                player.sendMessage(Component.text("You claimed land for the warzone!", NamedTextColor.DARK_RED));
            }
            return;
        }

        if (args.length > 1) {
            sender.sendMessage("Usage: /f claim");
            return;
        }

        int radius = 0;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // no-op
        }

        FactionPlayer factionPlayer = connection.selectFactionPlayerMember(player.getUniqueId());
        if (factionPlayer == null) {
            sender.sendMessage(Component.text("You must be in a faction to claim land", NamedTextColor.RED));
            return;
        }

        if (factionPlayer.rank != PlayerRank.Owner && factionPlayer.rank != PlayerRank.CoOwner) {
            sender.sendMessage(Component.text("Only owners and co-owners can execute this command.", NamedTextColor.RED));
            return;
        }

        FactionChunk factionChunk = connection.selectFactionUsingChunk(centerX, centerZ);
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
                    if (radius != 0) {
                        sender.sendMessage(Component.text("Radius claim not allowed when claiming over other faction's land. Use /f claim", NamedTextColor.RED));
                        return;
                    }
                    Claim.claimLand(connection, factionPlayer.factionId, centerX, centerZ, config.factionLandClaimCost, player, factionChunk.factionId);
                } else {
                    sender.sendMessage(Component.text(String.format("This land is already owned by %s", factionChunk.factionName), NamedTextColor.RED));
                }
            }
            return;
        }

        if (radius == 0) {
            Claim.claimLand(connection, factionPlayer.factionId, centerX, centerZ, config.factionLandClaimCost, player, null);
        } else {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    FactionChunk radiusChunk = connection.selectFactionUsingChunk(centerX, centerZ);
                    if (radiusChunk == null) {
                        Claim.claimLand(connection, factionPlayer.factionId, x, z, config.factionLandClaimCost, player, null);
                    }
                }
            }
        }
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