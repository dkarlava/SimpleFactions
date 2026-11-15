package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.Faction;
import types.FactionChunk;
import types.PluginConfig;
import util.BaseFactionCommand;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public record MapCommand(PluginConfig config, DataBaseHelper connection, Logger logger) implements BaseFactionCommand {
    private static final String symbols = "=#$%&/\\^*~?<>@";

    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length > 1) {
            sender.sendMessage(Component.text("Usage: /f map", NamedTextColor.RED));
            return;
        }

        if (args.length == 1) {
            String onOff = args[0];
            if (onOff.equalsIgnoreCase("on")) {
                connection.updatePlayerDataAutoMap(player.getUniqueId(), true);
            } else if (onOff.equalsIgnoreCase("off")) {
                connection.updatePlayerDataAutoMap(player.getUniqueId(), false);
            } else {
                player.sendMessage(Component.text("Usage: /f map", NamedTextColor.RED));
            }
        }
        MapCommand.displayMap(connection, player, null, logger);
    }

    // Passing in chunk instead of just getting it from the player to help with the auto map
    public static void displayMap (DataBaseHelper connection, Player player, Chunk overrideChunk, Logger logger) throws SQLException {
        Chunk chunk = player.getChunk();
        if (overrideChunk != null) {
            chunk = overrideChunk;
        }

        Faction playerFaction = connection.selectFactionByPlayerId(player.getUniqueId());
        String playerFactionName = null;
        if (playerFaction != null) {
            playerFactionName = playerFaction.name;
        }

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int minX = chunkX - 23;
        int maxX = chunkX + 23;
        int minZ = chunkZ - 8;
        int maxZ = chunkZ + 7;
        UUID worldId = player.getWorld().getUID();

        FactionChunk currentChunk = connection.selectFactionUsingChunk(chunkX, chunkZ, worldId);
        Map<String, FactionChunk> nearbyFactionChunks = connection.selectNearbyChunks(minX, maxX, minZ, maxZ, worldId);
        Map<String, Character> factionSymbols = new HashMap<>();

        Component fMap = Component.text("[___________] ", NamedTextColor.GOLD)
            .append(Component.text(String.format("(%d, %d) ", chunk.getX(), chunk.getZ()), NamedTextColor.GREEN))
            .append(Component.text(currentChunk == null ? "Unclaimed" : currentChunk.factionName, NamedTextColor.GOLD))
            .append(Component.text(" [___________]", NamedTextColor.GOLD))
            .append(Component.text('\n'));
        int claimSymbolIndex = 0;
        for (int z = minZ; z < maxZ; z++) {
            for (int x = minX; x < maxX; x++) {
                String keyLookup = String.format("(%d,%d)", z, x);
                FactionChunk factionChunk = nearbyFactionChunks.get(keyLookup);
                if (x == chunkX && z == chunkZ) {
                    fMap = fMap.append(Component.text('+', NamedTextColor.AQUA));
                } else if (factionChunk == null) {
                    fMap = fMap.append(Component.text('-', NamedTextColor.GRAY));
                } else {
                    Character factionSymbol = factionSymbols.get(factionChunk.factionName);
                    if (factionSymbol == null) {
                        if (Objects.equals(factionChunk.factionName, "SafeZone")) {
                            fMap = fMap.append(Component.text('+', NamedTextColor.GOLD));
                        } else if (Objects.equals(factionChunk.factionName, "WarZone")) {
                            fMap = fMap.append(Component.text('+', NamedTextColor.DARK_RED));
                        } else {
                            // if there are more different faction claims than we have characters for we restart the symbol iteration
                            // I am not really sure how to accommodate this
                            if (claimSymbolIndex == symbols.length()) {
                                claimSymbolIndex = 0;
                            }
                            char symbol = symbols.charAt(claimSymbolIndex);
                            if (playerFactionName != null && playerFactionName.equals(factionChunk.factionName)) {
                                fMap = fMap.append(Component.text(symbol, NamedTextColor.GREEN));
                            } else {
                                fMap = fMap.append(Component.text(symbol, NamedTextColor.WHITE));
                            }
                            factionSymbols.put(factionChunk.factionName, symbol);
                            claimSymbolIndex += 1;
                        }
                    } else {
                        if (playerFactionName != null && playerFactionName.equals(factionChunk.factionName)) {
                            fMap = fMap.append(Component.text(factionSymbol, NamedTextColor.GREEN));
                        } else {
                            fMap = fMap.append(Component.text(factionSymbol, NamedTextColor.WHITE));
                        }
                    }
                }
            }
            fMap = fMap.append(Component.text('\n'));
        }

        String factionSymbolKey = factionSymbols.entrySet().stream()
                .map(entry -> String.format("%c: %s", entry.getValue(), entry.getKey()))
                .collect(Collectors.joining(", "));

        fMap = fMap.append(Component.text(factionSymbolKey, NamedTextColor.WHITE));
        player.sendMessage(fMap);
    }
}