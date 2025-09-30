package util.other;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import types.Faction;
import types.FactionChunk;
import java.sql.SQLException;
import java.util.Objects;

// This function returns cancels the incoming event if the player faction does not equal the faction in the event chunk
public class FactionClaimProtect {
    public static void run (DataBaseHelper connection, Chunk chunk, Player player, Event event, String cancelMessage) throws SQLException {
        FactionChunk fChunk = connection.selectFactionUsingChunk(chunk.getX(), chunk.getZ());
        Faction playerFaction = connection.selectFactionByPlayerId(player.getUniqueId());
        if (fChunk == null) {
            return;
        }
        if (playerFaction == null) {
            cancelEvent(event, player, cancelMessage);
            return;
        }
        if (!Objects.equals(fChunk.factionId, playerFaction.id)) {
            cancelEvent(event, player, cancelMessage);
        }
    }

    private static void cancelEvent (Event event, Player player, String cancelMessage) {
        if (event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(true);
        }
        player.sendMessage(Component.text(cancelMessage, NamedTextColor.RED));
    }
}
