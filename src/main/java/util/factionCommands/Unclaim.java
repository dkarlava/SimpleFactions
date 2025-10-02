package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionChunk;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;
import util.other.BroadcastMessageToFactionMembers;
import java.sql.SQLException;
import java.util.Objects;

public record Unclaim(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length > 2) {
            sender.sendMessage("Usage: /f unclaim");
            return;
        }
        Chunk chunk = player.getChunk();
        int centerX = chunk.getX();
        int centerZ = chunk.getZ();

        if (args[0].equalsIgnoreCase("safezone") || args[0].equalsIgnoreCase("warzone")) {
            if (!player.isOp()) {
                // only oped players can unclaim for warzone/ safezone
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
                        connection.deleteFactionChunk(centerX, centerZ);
                    } else {
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                connection.deleteFactionChunk(x, z);
                            }
                        }
                    }
                } catch (SQLException e) {
                    if (radius == 0) {
                        connection.deleteFactionChunk(centerX, centerZ);
                    } else {
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                connection.deleteFactionChunk(x, z);
                            }
                        }
                    }
                }
                player.sendMessage(Component.text("You unclaimed land from the safe zone!", NamedTextColor.GOLD));
            } else if (safeZoneWarZoneName.equalsIgnoreCase("warzone")) {
                try {
                    if (radius == 0) {
                        connection.deleteFactionChunk(centerX, centerZ);
                    } else {
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                connection.deleteFactionChunk(x, z);
                            }
                        }
                    }
                } catch (SQLException e) {
                    if (radius == 0) {
                        connection.deleteFactionChunk(centerX, centerZ);
                    } else {
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                connection.deleteFactionChunk(x, z);
                            }
                        }
                    }
                }
                player.sendMessage(Component.text("You unclaimed land from the war zone!", NamedTextColor.DARK_RED));
            }
            return;
        }

        if (args.length > 1) {
            sender.sendMessage("Usage: /f unclaim");
            return;
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

        if (args[0].equalsIgnoreCase("all")) {
            connection.deleteAllFactionLand(factionPlayer.factionId);
            BroadcastMessageToFactionMembers.run(connection, factionPlayer.factionId, Component.text(String.format("%s just unclaimed all the factions land!", player.getName()), NamedTextColor.RED));
            return;
        }

        int radius = 0;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // no-op
        }

        FactionChunk factionChunk = connection.selectFactionUsingChunk(centerX, centerZ);
        if (factionChunk == null) {
            return;
        }
        if (factionChunk.factionName.equalsIgnoreCase("safezone")) {
            sender.sendMessage(Component.text("You cannot unclaim the safezone.", NamedTextColor.RED));
            return;
        }
        if (factionChunk.factionName.equalsIgnoreCase("warzone")) {
            sender.sendMessage(Component.text("You cannot unclaim the warzone.", NamedTextColor.RED));
            return;
        }
        if (Objects.equals(factionChunk.factionId, factionPlayer.factionId)) {
            if (radius == 0) {
                connection.deleteFactionChunk(centerX, centerZ);
            } else {
                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                        FactionChunk radiusChunk = connection.selectFactionUsingChunk(x, z);
                        if (Objects.equals(radiusChunk.factionId, factionPlayer.factionId)) {
                            connection.deleteFactionChunk(x, z);
                        }
                    }
                }
            }
            BroadcastMessageToFactionMembers.run(connection, factionPlayer.factionId, Component.text(String.format("%s just unclaimed land from the faction!", player.getName()), NamedTextColor.RED));
        } else {
            sender.sendMessage(Component.text("Your faction does not own this land.", NamedTextColor.RED));
        }
    }
}