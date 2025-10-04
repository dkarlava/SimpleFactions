package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionChunk;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;
import util.other.BroadcastMessageToFactionMembers;

import java.sql.SQLException;

public record SetHome(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            return;
        }

        if (args.length != 0) {
            sender.sendMessage(Component.text("Usage: /f sethome", NamedTextColor.RED));
            return;
        }

        FactionPlayer factionPlayer =  connection.selectFactionPlayerMember(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.factionId == null) {
            player.sendMessage(Component.text("You must be in a faction to run this command.", NamedTextColor.RED));
            return;
        }

        Chunk chunk = player.getChunk();
        FactionChunk factionChunk = connection.selectFactionUsingChunk(chunk.getX(),  chunk.getZ());
        if (factionChunk == null || !factionChunk.factionId.equals(factionPlayer.factionId)) {
            sender.sendMessage(Component.text("You cannot set the faction home here.", NamedTextColor.RED));
            return;
        }

        if (factionPlayer.rank == PlayerRank.Owner || factionPlayer.rank == PlayerRank.CoOwner) {
            Location loc = player.getLocation();
            connection.updateFactionHome(factionPlayer.factionId, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            BroadcastMessageToFactionMembers.run(connection, factionPlayer.factionId, Component.text(String.format("%s just set the faction home!", player.getName()), NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("You must be faction owner or co-owner to run this command.", NamedTextColor.RED));
        }
    }
}