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

/**
 * TODO: These items still need to be protected against
 *   - Fire needs to spread in non-safezone
 *   - Add Check for players hitting mobs
 *     - Can hit a mob if the mob is outside of the safezone but cannot if the mob is inside
 *   - Piston itself can extend into safezone
 *     - Blocks pushed by piston can as well
 *   - Can consume food, but food does not go up in safezone
 *   - Cannot regen in safezone
 *   - Player cannot take block damage in faction claim
 *   - Player cannot hit zombie in safezone but yes in non-safezone
 *     - EntityDamageByEntityEvent because safezone
 */

/**
 * TODO:
 *   - Need to allow block place if clicking the face of a claim or zone
 *   - Pistons either taking blocks from spawn or claimed land as well as being pushed in seem to all be allowed
 *   - Canceling PlayerInteractEvent because chunk is claimed for trample land
 *   - EntityChangeBlockEvent needs to allow grass to spread and sand and gravel to fall in claimed land, sheep eating grass needs to be allowed for faction land as well
 *     - Sand needs to fall for safe zone too
 *     - faction land needs to allow trampling of crops
 *     - allow armor stands for all zones
 *   - onEntityDamageByEntityEvent Controls the following
 *     - player vs Player (PvP)
 *     - When one player hits another with their hand, weapon, etc.
 *     - Player vs Mob
 *     - A player hitting a zombie, skeleton, creeper, etc.
 *     - Mob vs Player
 *     - Zombie punching a player, spider biting, skeleton shooting, etc.
 *     - Mob vs Mob
 *     - Wolves attacking skeletons, iron golems smashing zombies, etc.
 *     - Projectile damage
 *     - Arrows (from skeletons or players), tridents, snowballs, eggs, etc.
 *     - Thrown potions / lingering potions
 *     - Damage caused by harming potions.
 *     - TNT / Creeper explosions
 *     - If the damage source can be attributed to an entity (e.g., a creeper or primed TNT).
 *     - Lightning from tridents
 *     - If summoned via a channeling trident.
 *     - Thorns enchantment
 *     - When a mob/player takes reflected damage from attacking something wearing Thorns.
 *     - Pets
 *     - Wolves or cats dealing damage on behalf of their owner.
 */

// This function returns cancels the incoming event if the player faction does not equal the faction in the event chunk
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
            logger.info(String.format("Allowing %s because of no zone claim.", event.getEventName()));
            return;
        }

        if (!allowSafezone && fChunk.factionName.equalsIgnoreCase("safezone")) {
            if (requiresPlayer) {
                if (!initiator.isOp()) {
                    ((Cancellable) event).setCancelled(true);
                    logger.warning(String.format("Canceling %s because safezone.", event.getEventName()));
                    return;
                }
            } else {
                ((Cancellable) event).setCancelled(true);
                logger.warning(String.format("Canceling %s because safezone.", event.getEventName()));
                return;
            }
        }

        if (!allowWarzone && fChunk.factionName.equalsIgnoreCase("warzone")) {
            if (requiresPlayer) {
                if (!initiator.isOp()) {
                    ((Cancellable) event).setCancelled(true);
                    logger.warning(String.format("Canceling %s because warzone.", event.getEventName()));
                    return;
                }
            } else {
                ((Cancellable) event).setCancelled(true);
                logger.warning(String.format("Canceling %s because warzone.", event.getEventName()));
                return;
            }
        }

        if (!allowFactionClaim) {
            if (requiresPlayer) {
                handleRequiresPlayerFactionClaim(initiator, event);
            } else {
                ((Cancellable) event).setCancelled(true);
                logger.warning(String.format("Canceling %s because chunk is claimed.", event.getEventName()));
            }
        }
    }

    private void handleRequiresPlayerFactionClaim (Player initiator, Event event) {
        if (!initiator.isOp()) {
            ((Cancellable) event).setCancelled(true);
            logger.warning(String.format("Canceling %s because chunk is claimed.", event.getEventName()));
        }
    }
}
