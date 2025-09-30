package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionPlayer;
import types.PluginConfig;
import util.BaseFactionCommand;
import util.other.BroadcastMessageToFactionMembers;
import java.sql.SQLException;

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
                connection.createFactionSafeZoneChunk(x, z);
                player.sendMessage(Component.text("You claimed land for the safe zone!", NamedTextColor.GOLD));
            } else if (safeZoneWarZoneName.equalsIgnoreCase("warzone")) {
                connection.createFactionWarZoneChunk(x, z);
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
        // todo: ensure total power of faction is greater than the amount of currently claimed land
        connection.createFactionChunk(factionPlayer.factionId, x, z);
        BroadcastMessageToFactionMembers.run(connection, factionPlayer.factionId, Component.text(String.format("%s just claimed land for the faction!", player.getName()), NamedTextColor.GREEN));
    }
}