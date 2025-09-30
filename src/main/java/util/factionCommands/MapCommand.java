package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionChunk;
import types.PluginConfig;
import util.BaseFactionCommand;
import java.sql.SQLException;
import java.util.Map;

public record MapCommand(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        // TODO: support /f map on.... erg
        if (args.length != 0) {
            sender.sendMessage("Usage: /f map");
            return;
        }
        Chunk chunk = player.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int minX = chunkX - 12;
        int maxX = chunkX + 12;
        int minZ = chunkZ - 5;
        int maxZ = chunkZ + 5;



        Map<String, FactionChunk> nearbyFactionChunks = connection.selectNearbyChunks(minX, maxX, minZ, maxZ);

        // TODO: allow for the max rows and columns to be configurable?
        // TODO: Need to add key for what symbols are for what faction
        // TODO: Need to grab new symbol for each faction id
        StringBuilder fMap = new StringBuilder();
        for (int z = minZ; z < maxZ; z++) {
            for (int x = minX; x < maxX; x++) {
                if (x == chunkX && z == chunkZ) {
                    fMap.append("+");
                } else {
                    String keyLookup = String.format("(%d,%d)", z, x);
                    if (nearbyFactionChunks.containsKey(keyLookup)) {
                        fMap.append("&");
                    } else {
                        fMap.append("-");
                    }
                }
            }
            fMap.append("\n");
        }

        // TODO: format this make it centered if possible, add some nice headers, add compass, make it not gold
        player.sendMessage(Component.text(String.format("==========(%d, %d)==========\n", chunk.getX(), chunk.getZ()), NamedTextColor.GOLD)
            .append(Component.text(String.format("%s\n", fMap), NamedTextColor.GOLD))
        );
    }
}