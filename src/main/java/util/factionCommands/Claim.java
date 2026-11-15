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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

// TODO: claim and unclaim radius get weird if there are other claims in the radius
public record Claim(PluginConfig config, DataBaseHelper connection, Logger logger) implements BaseFactionCommand {
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
        UUID worldId = player.getWorld().getUID();

        if (args.length > 1) {
            String safeZoneWarZoneName = args[0];
            if (safeZoneWarZoneName.equalsIgnoreCase("safezone") || safeZoneWarZoneName.equalsIgnoreCase("warzone")) {
                if (!player.isOp()) {
                    // only oped players can claim for warzone/ safezone
                    return;
                }

                int radius = 0;
                try {
                    radius = Integer.parseInt(args[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // no-op
                }
                if (safeZoneWarZoneName.equalsIgnoreCase("safezone")) {
                    try {
                        if (radius == 0) {
                            connection.createFactionSafezoneChunk(centerX, centerZ, worldId);
                        } else {
                            for (int x = centerX - radius; x <= centerX + radius; x++) {
                                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                    connection.createFactionSafezoneChunk(x, z, worldId);
                                }
                            }
                        }
                        player.sendMessage(Component.text("You claimed land for the safe zone!", NamedTextColor.GOLD));
                    } catch (SQLException e) {
                        logger.warning(String.format("Failed to claim land for safezone. Failed with error: %s", e.getMessage()));
                    }
                } else if (safeZoneWarZoneName.equalsIgnoreCase("warzone")) {
                    try {
                        if (radius == 0) {
                            connection.createFactionWarzoneChunk(centerX, centerZ, worldId);
                        } else {
                            for (int x = centerX - radius; x <= centerX + radius; x++) {
                                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                    connection.createFactionWarzoneChunk(x, z, worldId);
                                }
                            }
                        }
                        player.sendMessage(Component.text("You claimed land for the warzone!", NamedTextColor.DARK_RED));
                    } catch (SQLException e) {
                        logger.warning(String.format("Failed to claim land for warzone. Failed with error: %s", e.getMessage()));
                    }
                }
                return;
            }
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

        FactionChunk factionChunk = connection.selectFactionUsingChunk(centerX, centerZ, worldId);
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

                    Map<String, FactionChunk> nearbyChunks = connection.selectNearbyChunks(centerX - 1, centerX + 1, centerZ - 1, centerZ + 1, worldId);
                    if (nearbyChunks.isEmpty()) {
                        sender.sendMessage(Component.text("Internal Error. Adjacent claim list is empty.", NamedTextColor.RED));
                        return;
                    }
                    // If nearby chunks does not equal 9 that means that at least one nearby chunk is unclaimed so we can allow the overclaim.
                    if (nearbyChunks.size() != 9) {
                        Claim.claimLand(connection, factionPlayer.factionId, centerX, centerZ, config.factionLandClaimCost, player, factionChunk.factionId, worldId);
                        return;
                    }

                    for (Map.Entry<String, FactionChunk> nearbyChunk : nearbyChunks.entrySet()) {
                        if  (!nearbyChunk.getValue().factionId.equalsIgnoreCase(factionChunk.factionId)) {
                            Claim.claimLand(connection, factionPlayer.factionId, centerX, centerZ, config.factionLandClaimCost, player, factionChunk.factionId, worldId);
                            return;
                        }
                    }
                    sender.sendMessage(Component.text(String.format("When overclaiming, there must be at least one adjacent claim that isn't owned by %s.", factionChunk.factionName), NamedTextColor.RED));
                    return;
                } else {
                    sender.sendMessage(Component.text(String.format("This land is already owned by %s", factionChunk.factionName), NamedTextColor.RED));
                }
            }
            return;
        }

        if (factionPlayer.rank != PlayerRank.Owner && factionPlayer.rank != PlayerRank.CoOwner) {
            sender.sendMessage(Component.text("Only owners and co-owners can execute this command.", NamedTextColor.RED));
            return;
        }

        if (radius == 0) {
            Claim.claimLand(connection, factionPlayer.factionId, centerX, centerZ, config.factionLandClaimCost, player, null, worldId);
        } else {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    FactionChunk radiusChunk = connection.selectFactionUsingChunk(centerX, centerZ, worldId);
                    if (radiusChunk == null) {
                        Claim.claimLand(connection, factionPlayer.factionId, x, z, config.factionLandClaimCost, player, null, worldId);
                    }
                }
            }
        }
    }
    public static void claimLand (DataBaseHelper connection, String factionId, int x, int z, int claimCost, Player claimer, String overClaimedFactionId, UUID worldId) throws SQLException {
        GetTotalLandData.GetTotalLandDataReturn claimData = GetTotalLandData.run(connection, factionId);
        if (claimData.totalPower - claimData.totalLand < claimCost) {
            claimer.sendMessage(Component.text("Your faction does not have enough power to claim this land", NamedTextColor.RED));
            return;
        }
        if (overClaimedFactionId != null) {
            connection.updateFactionChunk(factionId, x, z, claimer.getWorld().getUID());
            Faction overclaimedFaction = connection.selectFaction(overClaimedFactionId);
            BroadcastMessageToFactionMembers.run(connection, factionId, Component.text(String.format("%s just claimed land from %s", claimer.getName(), overclaimedFaction.name), NamedTextColor.GREEN));
            Faction claimingFaction = connection.selectFactionByPlayerId(claimer.getUniqueId());
            BroadcastMessageToFactionMembers.run(connection, overclaimedFaction.id, Component.text(String.format("Your faction just lost a land claim at (%d, %d)! to %s", x, z, claimingFaction.name), NamedTextColor.GREEN));
        } else {
            BroadcastMessageToFactionMembers.run(connection, factionId, Component.text(String.format("%s just claimed land for the faction!", claimer.getName()), NamedTextColor.GREEN));
            connection.createFactionChunk(factionId, x, z, worldId);
        }
    }
}