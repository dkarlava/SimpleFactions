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
import java.util.logging.Logger;

/**
 * TODO: These items still need to be protected against
 */

// This function returns cancels the incoming event if the player faction does not equal the faction in the event chunk
public class FactionClaimProtect {

    Logger logger;
    public FactionClaimProtect(Logger logger) {
        this.logger = logger;
    }

    public static class FactionClaimProtectArgs {
        Chunk chunk;
        Player player;
        Event event;
        String cancelMessage;
        boolean safezoneProtect;
        boolean warzoneProtect;
        public FactionClaimProtectArgs(Chunk chunk, Player player, Event event, String cancelMessage, boolean safezoneProtect, boolean warzoneProtect) {
            this.chunk = chunk;
            this.player = player;
            this.event = event;
            this.cancelMessage = cancelMessage;
            this.safezoneProtect = safezoneProtect;
            this.warzoneProtect = warzoneProtect;
        }
    }

    public void run (DataBaseHelper connection, FactionClaimProtectArgs args) throws SQLException {
        FactionChunk fChunk = connection.selectFactionUsingChunk(args.chunk.getX(), args.chunk.getZ());
        Faction playerFaction = connection.selectFactionByPlayerId(args.player.getUniqueId());
        if (fChunk == null) {
            logger.info(String.format("Allowing %s because of no claim.", args.event.getEventName()));
            return;
        }
        if (args.player != null && args.player.isOp()) {
            logger.info(String.format("Allowing %s because player is op.", args.event.getEventName()));
            return;
        }
        if (fChunk.factionName.equalsIgnoreCase("safezone") && args.safezoneProtect) {
            boolean result = cancelEvent(args.event, args.player, args.cancelMessage);
            if (result) {
                logger.warning(String.format("Canceling %s because safezone.", args.event.getEventName()));
                return;
            }
        }
        if (fChunk.factionName.equalsIgnoreCase("warzone") && args.warzoneProtect) {
            boolean result = cancelEvent(args.event, args.player, args.cancelMessage);
            if (result) {
                logger.warning(String.format("Canceling %s because warzone.", args.event.getEventName()));
                return;
            }
        }

        if (playerFaction == null) {
            boolean result = cancelEvent(args.event, args.player, args.cancelMessage);
            if (result) {
                logger.warning(String.format("Canceling %s because no player faction modifies player faction.", args.event.getEventName()));
                return;
            }
        }
        if (playerFaction != null && !Objects.equals(fChunk.factionId, playerFaction.id)) {
            boolean result = cancelEvent(args.event, args.player, args.cancelMessage);
            if (result) {
                logger.warning(String.format("Canceling %s because player faction modifies another player faction.", args.event.getEventName()));
            }
        }
    }

    public void zoneRun (DataBaseHelper connection, FactionClaimProtectArgs args) throws SQLException {
        FactionChunk fChunk = connection.selectFactionUsingChunk(args.chunk.getX(), args.chunk.getZ());
        if (fChunk == null) {
            logger.info(String.format("Allowing %s because of no zone claim.", args.event.getEventName()));
            return;
        }
        if (args.player != null && args.player.isOp()) {
            logger.info(String.format("Allowing %s because player is op in zone.", args.event.getEventName()));
            return;
        }

        if (fChunk.factionName.equalsIgnoreCase("safezone") && args.safezoneProtect) {
            if (args.event instanceof Cancellable) {
                ((Cancellable) args.event).setCancelled(true);
                logger.warning(String.format("Canceling %s because safezone.", args.event.getEventName()));
                return;
            }
        }
        if (fChunk.factionName.equalsIgnoreCase("warzone") && args.warzoneProtect) {
            if (args.event instanceof Cancellable) {
                ((Cancellable) args.event).setCancelled(true);
                logger.warning(String.format("Canceling %s because warzone.", args.event.getEventName()));
            }
        }
    }

    // returns true if the event was canceled false otherwise
    // NOTE: If the event is not cancelable is there anything we can do?
    private static boolean cancelEvent (Event event, Player player, String cancelMessage) {
        if (event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(true);
            player.sendMessage(Component.text(cancelMessage, NamedTextColor.RED));
            return true;
        }
        return false;
    }
}
