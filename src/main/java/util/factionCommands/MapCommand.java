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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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

        // This is slightly complex logic to generate random symbols for claimed lands on f map
        // If this functionality requires too much compute we can just statically reference the symbols, but that seemed like less fun
        Random random = new Random();
        String symbols = "!@#$%^&*()_+=[]{}|;:'\",.<>/?`~";
        StringBuilder pool = new StringBuilder(symbols);
        Map<String, FactionChunk> nearbyFactionChunks = connection.selectNearbyChunks(minX, maxX, minZ, maxZ);
        Map<String, Character> factionSymbols = new HashMap<>();

        // TODO: allow for the max rows and columns to be configurable?
        // TODO: based on the symbol the row width is difference sizes, anyway to fix?
        StringBuilder fMap = new StringBuilder();
        for (int z = minZ; z < maxZ; z++) {
            for (int x = minX; x < maxX; x++) {
                String keyLookup = String.format("(%d,%d)", z, x);
                FactionChunk factionChunk = nearbyFactionChunks.get(keyLookup);
                if (factionChunk == null) {
                    if (x == chunkX && z == chunkZ) {
                        fMap.append("+");
                    } else {
                        fMap.append("-");
                    }
                } else {
                    Character factionSymbol = factionSymbols.get(factionChunk.factionName);
                    if (factionSymbol == null) {
                        if (pool.isEmpty()) {
                            pool = new StringBuilder(symbols);
                        }
                        int index = random.nextInt(pool.length());
                        Character symbol = pool.charAt(index);
                        if (x == chunkX && z == chunkZ) {
                            fMap.append("+");
                        } else {
                            fMap.append(symbol);
                        }
                        factionSymbols.put(factionChunk.factionName, symbol);
                        pool.deleteCharAt(index);
                    } else {
                        if (x == chunkX && z == chunkZ) {
                            fMap.append("+");
                        } else {
                            fMap.append(factionSymbol);
                        }
                    }
                }
            }
            fMap.append("\n");
        }

        String factionSymbolKey = factionSymbols.entrySet().stream()
            .map(entry -> String.format("%c: %s", entry.getValue(), entry.getKey()))
            .collect(Collectors.joining(", "));

        // TODO: format this make it centered if possible, add some nice headers, add compass, make it not gold, make your own claims green
        player.sendMessage(Component.text(String.format("==========(%d, %d)==========\n", chunk.getX(), chunk.getZ()), NamedTextColor.GOLD)
            .append(Component.text(String.format("%s\n", fMap), NamedTextColor.GOLD))
            .append(Component.text(String.format("%s\n", factionSymbolKey), NamedTextColor.GOLD))
        );
    }
}