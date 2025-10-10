package util.other;

import daveiiii.simpleFactions.data.DataBaseHelper;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import types.FactionChunk;
import java.sql.SQLException;
import java.util.logging.Logger;

public class FactionClaimProtect {

    Logger logger;
    DataBaseHelper connection;

    public FactionClaimProtect(Logger logger, DataBaseHelper connection) {
        this.logger = logger;
        this.connection = connection;
    }

    public void run (Event event, Chunk chunkBeingModified, Player initiator, boolean allowWarzone, boolean allowSafezone, boolean allowFactionClaim, boolean requiresPlayer) throws SQLException {
        if (!(event instanceof Cancellable)) {
            logger.info(String.format("Event %s is not cancellable.", event.getEventName()));
            return;
        }

        if (chunkBeingModified == null) {
            logger.warning(String.format("Chunk being modified is null. Canceling %s event.", event.getEventName()));
            ((Cancellable) event).setCancelled(true);
            return;
        }

        if (requiresPlayer && initiator == null) {
            logger.warning(String.format("Event requires player but player was not found. Canceling %s event.", event.getEventName()));
            ((Cancellable) event).setCancelled(true);
            return;
        }

        FactionChunk fChunk = connection.selectFactionUsingChunk(chunkBeingModified.getX(), chunkBeingModified.getZ());
        if (fChunk == null) {
//            logger.info(String.format("Allowing %s because of no zone claim.", event.getEventName()));
            return;
        }

        if (!allowSafezone && fChunk.factionName.equalsIgnoreCase("safezone")) {
            if (requiresPlayer) {
                if (!initiator.isOp()) {
                    ((Cancellable) event).setCancelled(true);
//                    logger.warning(String.format("Canceling %s because safezone.", event.getEventName()));
                    return;
                }
            } else {
                ((Cancellable) event).setCancelled(true);
//                logger.warning(String.format("Canceling %s because safezone.", event.getEventName()));
                return;
            }
        }

        if (!allowWarzone && fChunk.factionName.equalsIgnoreCase("warzone")) {
            if (requiresPlayer) {
                if (!initiator.isOp()) {
                    ((Cancellable) event).setCancelled(true);
//                    logger.warning(String.format("Canceling %s because warzone.", event.getEventName()));
                    return;
                }
            } else {
                ((Cancellable) event).setCancelled(true);
//                logger.warning(String.format("Canceling %s because warzone.", event.getEventName()));
                return;
            }
        }

        if (!allowFactionClaim && !fChunk.factionName.equalsIgnoreCase("safezone") && !fChunk.factionName.equalsIgnoreCase("warzone")) {
            if (requiresPlayer) {
                handleRequiresPlayerFactionClaim(initiator, event);
            } else {
                ((Cancellable) event).setCancelled(true);
//                logger.warning(String.format("Canceling %s because chunk is claimed.", event.getEventName()));
            }
        }
    }

    private void handleRequiresPlayerFactionClaim (Player initiator, Event event) {
        if (!initiator.isOp()) {
            ((Cancellable) event).setCancelled(true);
//            logger.warning(String.format("Canceling %s because chunk is claimed.", event.getEventName()));
        }
    }
}
