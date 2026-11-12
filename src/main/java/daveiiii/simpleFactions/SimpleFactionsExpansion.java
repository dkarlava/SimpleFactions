package daveiiii.simpleFactions;

import daveiiii.simpleFactions.data.DataBaseHelper;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import types.FactionPlayer;

import java.sql.SQLException;
import java.util.logging.Logger;

public class SimpleFactionsExpansion extends PlaceholderExpansion {
    private final SimpleFactions plugin;
    private final DataBaseHelper db;
    private final Logger logger;

    public SimpleFactionsExpansion(SimpleFactions plugin, DataBaseHelper db, Logger logger) {
        this.plugin = plugin;
        this.db = db;
        this.logger = logger;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "simplefactions";
    }

    @Override
    public @NotNull String getAuthor() {
        return "daveiiii";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        // Ensure the player is valid
        if (player == null) {
            return "";
        }

        // Faction name placeholder
        if (params.equalsIgnoreCase("faction")) {
            try {
                FactionPlayer factionPlayer = db.selectFactionPlayerMember(player.getUniqueId());
                if (factionPlayer != null) {
                    String prefix = switch (factionPlayer.rank) {
                        case Owner -> "***";
                        case CoOwner -> "**";
                        case Elder -> "*";
                        default -> "";
                    };
                    String displayName = String.format("%s%s", prefix, factionPlayer.factionName);
                    return displayName;
                }
            } catch (SQLException e) {
                logger.severe(e.toString());
            }
            return "";
        }

        // Unknown placeholder
        return null;
    }
}
