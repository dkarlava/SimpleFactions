package util.other;

import daveiiii.simpleFactions.data.DataBaseHelper;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import types.Faction;
import types.FactionChunk;
import types.PluginConfig;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class FactionClaimProtect {

    Logger logger;
    PluginConfig pluginConfig;
    DataBaseHelper connection;

    public FactionClaimProtect(Logger logger, DataBaseHelper connection, PluginConfig config) {
        this.logger = logger;
        this.pluginConfig = config;
        this.connection = connection;
    }

    public void run (Event event, Chunk chunkBeingModified, Player initiator, boolean allowWarzone, boolean allowSafezone, boolean allowFactionClaim, boolean requiresPlayer, UUID worldId) throws SQLException {
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

        FactionChunk fChunk = connection.selectFactionUsingChunk(chunkBeingModified.getX(), chunkBeingModified.getZ(), worldId);
        if (fChunk == null) {
            if (pluginConfig.debug) {
                logger.info(String.format("Allowing %s because of no zone claim.", event.getEventName()));
            }
            return;
        }

        if (!allowSafezone && fChunk.factionName.equalsIgnoreCase("safezone")) {
            // dont allow action if allowSafezone is false
            if (requiresPlayer) {
                if (!initiator.isOp()) {
                    ((Cancellable) event).setCancelled(true);
                    if (pluginConfig.debug) {
                        logger.warning(String.format("Canceling %s because safezone.", event.getEventName()));
                    }
                }
            } else {
                ((Cancellable) event).setCancelled(true);
                if (pluginConfig.debug) {
                    logger.warning(String.format("Canceling %s because safezone.", event.getEventName()));
                }
            }
        } else if (!allowWarzone && fChunk.factionName.equalsIgnoreCase("warzone")) {
            // dont allow action if allowWarzone is false
            if (requiresPlayer) {
                if (!initiator.isOp()) {
                    ((Cancellable) event).setCancelled(true);
                    if (pluginConfig.debug) {
                        logger.warning(String.format("Canceling %s because warzone.", event.getEventName()));
                    }
                }
            } else {
                ((Cancellable) event).setCancelled(true);
                if (pluginConfig.debug) {
                    logger.warning(String.format("Canceling %s because warzone.", event.getEventName()));
                }
            }
        } else if (requiresPlayer) {
            // only block player actions in land claims, all other events are allowed, block spread, creepers, mob spawners


            Faction playerFaction = connection.selectFactionByPlayerId(initiator.getUniqueId());
            // player is attempting to preform an action that is not part of their claim. Prevent it if allowFactionClaim is false
            // otherwise, the player is part of the faction so allow the action
            if  (playerFaction == null || !Objects.equals(playerFaction.id, fChunk.factionId)) {
                if (!allowFactionClaim && !initiator.isOp()) {
                    ((Cancellable) event).setCancelled(true);
                    if (pluginConfig.debug) {
                        logger.warning(String.format("Canceling %s because chunk is claimed.", event.getEventName()));
                    }
                }
            }
        }
    }
}
