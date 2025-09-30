package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;
import util.other.BroadcastMessageToFactionMembers;

import java.sql.SQLException;

public record Unclaim(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        Chunk chunk = player.getChunk();
        int x = chunk.getX();
        int z = chunk.getZ();

        // Special handling for unclaiming land for warzone/ safezone
        if (args.length == 1) {
            if (!player.isOp()) {
                // only oped players can unclaim for warzone/ safezone
                return;
            }
            String safeZoneWarZoneName = args[0];
            if  (safeZoneWarZoneName.equalsIgnoreCase("safezone") && connection.selectFactionUsingChunk(x, z) != null) {
                connection.deleteFactionChunk(x, z);
                player.sendMessage(Component.text("You unclaimed land from the safe zone!", NamedTextColor.GOLD));
            } else if (safeZoneWarZoneName.equalsIgnoreCase("warzone") && connection.selectFactionUsingChunk(x, z) != null) {
                connection.deleteFactionChunk(x, z);
                player.sendMessage(Component.text("You unclaimed land from the war zone!", NamedTextColor.DARK_RED));
            }
            return;
        }

        // TODO: support /f unclaim all
        if (args.length != 0) {
            sender.sendMessage("Usage: /f claim");
            return;
        }

        FactionPlayer factionPlayer = connection.selectFactionPlayerMember(player.getUniqueId());
        if (factionPlayer == null) {
            sender.sendMessage(Component.text("You must be in a faction to unclaim land", NamedTextColor.RED));
            return;
        }
        if (factionPlayer.rank == PlayerRank.CoOwner || factionPlayer.rank == PlayerRank.Owner) {
            // todo: add power back to the faction

            connection.deleteFactionChunk(x, z);
            BroadcastMessageToFactionMembers.run(connection, factionPlayer.factionId, Component.text(String.format("%s just unclaimed land from the faction!", player.getName()), NamedTextColor.RED));
        } else {
            sender.sendMessage(Component.text("You must be at least an owner or co-owner to claim land.", NamedTextColor.RED));
        }
    }
}