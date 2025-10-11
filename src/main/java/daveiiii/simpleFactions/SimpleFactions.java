package daveiiii.simpleFactions;

import daveiiii.simpleFactions.commands.DiscordCommandManager;
import daveiiii.simpleFactions.commands.EnchantCommandManager;
import daveiiii.simpleFactions.commands.FactionsCommandManager;
import daveiiii.simpleFactions.data.DataBaseHelper;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import types.*;
import util.factionCommands.FactionCommandTabCompleter;
import util.factionCommands.MapCommand;
import util.other.FactionClaimProtect;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO: Owner can leave faction with /f leave. needs to either disband or give owner to someone else
// TODO: need to be able to unset f home

public final class SimpleFactions extends JavaPlugin implements Listener {

    private Logger logger;
    private DataBaseHelper db;
    private PluginConfig config;
    private FactionClaimProtect factionClaimProtect;

    @Override
    public void onEnable() {
        logger = getLogger();
        try {
            db = new DataBaseHelper(logger, getDataFolder() + "/factions.db");
        } catch (Exception e) {
            logger.severe(e.toString());
        }

        this.saveDefaultConfig();
        config = new PluginConfig(getConfig());

        factionClaimProtect = new FactionClaimProtect (logger, db);
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new DiscordCommandManager(config));
        Objects.requireNonNull(this.getCommand("setenchant")).setExecutor(new EnchantCommandManager(config));
        Objects.requireNonNull(this.getCommand("f")).setExecutor(new FactionsCommandManager(config, db, logger));
        Objects.requireNonNull(this.getCommand("f")).setTabCompleter(new FactionCommandTabCompleter());
        getServer().getPluginManager().registerEvents(this, this);
        initializeRepeatingTasks();
        logger.info("SimpleFactions has been enabled!");
    }

    @Override
    public void onDisable() {
        try {
            db.close();
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        db.updatePlayerDataAutoMap(uuid, false);
        if (db.selectPlayerData(uuid) == null) {
            db.insertPlayerData(uuid);
        }
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) throws SQLException {
        factionClaimProtect.run(event, event.getBlock().getChunk(), event.getPlayer(), false, false, false, true);
    }

    @EventHandler
    public void onBlockPlace (BlockPlaceEvent event) throws SQLException {
        factionClaimProtect.run(event, event.getBlock().getChunk(), event.getPlayer(), false, false, false, true);
    }

    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) throws SQLException {
        boolean allowWarzone = false;
        boolean allowSafezone = false;
        boolean allowFactionClaim = false;
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Material type = block.getType();
        ItemStack item = event.getItem();
        Action action = event.getAction();
        Chunk chunkBeingModified = block.getChunk();
        Block targetBlock = block.getRelative(event.getBlockFace());
        Chunk targetChunk = targetBlock.getChunk();

        if (item != null && item.getType() == Material.CREEPER_SPAWN_EGG) {
            // Allow a creeper to be placed in faction claim
            allowFactionClaim = true;
            factionClaimProtect.run(event, chunkBeingModified, event.getPlayer(), allowWarzone, allowSafezone, allowFactionClaim, true);
        } else if (block.getState() instanceof InventoryHolder) {
            // Always allow chests and similar items to be opened
            // NOTE: InventoryHolder does not include minecart chests/ hoppers and not boats either
            allowWarzone = true;
            allowSafezone = true;
            allowFactionClaim = true;
            factionClaimProtect.run(event, chunkBeingModified, event.getPlayer(), allowWarzone, allowSafezone, allowFactionClaim, true);
        } else if (Tag.BUTTONS.isTagged(type) || Tag.DOORS.isTagged(type) || Tag.TRAPDOORS.isTagged(type) || Tag.FENCE_GATES.isTagged(type) || Tag.PRESSURE_PLATES.isTagged(type)) {
            // TODO: Add lever to this check
            allowWarzone = true;
            allowSafezone = true;
            factionClaimProtect.run(event, chunkBeingModified, event.getPlayer(), allowWarzone, allowSafezone, allowFactionClaim, true);
        } else if (action == Action.PHYSICAL) {
            if (type == Material.FARMLAND) {
                // allowing trample in claims, but not in safezone
                allowFactionClaim = true;
                factionClaimProtect.run(event, chunkBeingModified, event.getPlayer(), allowWarzone, allowSafezone, allowFactionClaim, true);
            }
        } else {
            factionClaimProtect.run(event, targetChunk, event.getPlayer(), allowWarzone, allowSafezone, allowFactionClaim, true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent (PlayerInteractEntityEvent event) throws SQLException {
        Entity target = event.getRightClicked();
        Chunk chunkBeingModified = target.getChunk();
        Player player = event.getPlayer();
        boolean allowFactionClaim = false;
        if (target instanceof Creeper && player.getInventory().getItemInMainHand().getType() == Material.FLINT_AND_STEEL) {
            allowFactionClaim = true;
        }

        factionClaimProtect.run(event, chunkBeingModified, player, false, false, allowFactionClaim, true);
    }

    @EventHandler
    public void onEntityDamageEvent (EntityDamageEvent event) throws SQLException {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        boolean allowWarzone = true;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            allowWarzone = false;
        }

        Chunk chunkBeingModified = player.getChunk();

        factionClaimProtect.run(event, chunkBeingModified, player, allowWarzone, false, true, true);
    }

    @EventHandler
    public void onBlockDamageEvent (BlockDamageEvent event) throws SQLException {
        factionClaimProtect.run(event, event.getBlock().getChunk(), event.getPlayer(), false, false, true, true);
    }

    @EventHandler
    public void onBlockFromToEvent (BlockFromToEvent event) throws SQLException {
        Player player = null;
        factionClaimProtect.run(event, event.getBlock().getChunk(), player, false, false, true, false);
    }

    @EventHandler
    public void onBlockIgniteEvent (BlockIgniteEvent event) throws SQLException {
        // This event has a .getPlayer(), but I am choosing to ignore since it is sometimes null
        Player player = null;
        factionClaimProtect.run(event, event.getBlock().getChunk(), player, false, false, true, false);
    }

    @EventHandler
    public void onBlockBurnEvent (BlockBurnEvent event) throws SQLException {
        Player player = null;
        factionClaimProtect.run(event, event.getBlock().getChunk(), player, false, false, true, false);
    }

    @EventHandler
    public void onBlockPistonExtendEvent (BlockPistonExtendEvent event) throws SQLException {
        Player player = null;
        List<Block> blocks = event.getBlocks();
        for (Block block : blocks) {
            Block targetBlock = block.getRelative(event.getDirection());
            Chunk chunkBeingModified = targetBlock.getChunk();
            factionClaimProtect.run(event, chunkBeingModified, player, false, false, false, false);
        }
    }

    @EventHandler
    public void onBlockPistonRetractEvent (BlockPistonRetractEvent event) throws SQLException {
        Player player = null;
        List<Block> blocks = event.getBlocks();
        for (Block block : blocks) {
            Chunk chunkBeingModified = block.getChunk();
            factionClaimProtect.run(event, chunkBeingModified, player, false, false, false, false);
        }
    }

    @EventHandler
    public void onPlayerBucketEmptyEvent (PlayerBucketEmptyEvent event) throws SQLException {
        Chunk chunkBeingModified = event.getBlockClicked().getRelative(event.getBlockFace()).getChunk();
        factionClaimProtect.run(event, chunkBeingModified, event.getPlayer(), false, false, false, true);
    }

    @EventHandler
    public void onBlockFadeEvent (BlockFadeEvent event) throws SQLException {
        Player player = null;
        factionClaimProtect.run(event, event.getBlock().getChunk(), player, false, false, true, false);
    }

    @EventHandler
    public void onEntityChangeBlockEvent (EntityChangeBlockEvent event) throws SQLException {
        Player player = null;
        factionClaimProtect.run(event, event.getBlock().getChunk(), player, false, false, true, false);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent (EntityDamageByEntityEvent event) throws SQLException {
        Chunk chunkBeingModified = event.getEntity().getChunk();
        Player player = null;
        boolean allowWarzone = true;
        boolean allowSafezone = false;
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getDamager() instanceof Player maybePlayer) {
            if (maybePlayer.isOp()) {
                allowSafezone = true;
            }
        }

        factionClaimProtect.run(event, chunkBeingModified, player, allowWarzone, allowSafezone, true, false);
    }

    @EventHandler
    public void onEntityExplodeEvent (EntityExplodeEvent event) throws SQLException {
        // First we check if the origin is inside a zone, if so we cancel, otherwise filter out zone blocks
        Chunk chunkBeingModifiedOrigin = event.getLocation().getChunk();
        factionClaimProtect.run(event, chunkBeingModifiedOrigin, null, false, false, true, false);


        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            Chunk chunkBeingModified = block.getChunk();
            FactionChunk fChunk = db.selectFactionUsingChunk(chunkBeingModified.getX(), chunkBeingModified.getZ());
            if (fChunk != null) {
                if (fChunk.factionName.equalsIgnoreCase("safezone") || fChunk.factionName.equalsIgnoreCase("warzone")) {
                    it.remove();
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteractEvent (EntityInteractEvent event) throws SQLException {
        Player player = null;
        factionClaimProtect.run(event, event.getBlock().getChunk(), player, false, false, false, false);
    }

    @EventHandler
    public void onCreatureSpawnEvent (CreatureSpawnEvent event) throws SQLException {
        Chunk chunkBeingModified = event.getEntity().getChunk();
        Player player = null;
        factionClaimProtect.run(event, chunkBeingModified, player, false, false, true, false);
    }

    @EventHandler
    public void onHangingBreakEvent (HangingBreakEvent event) throws SQLException {
        // handled elsewhere
        if (event instanceof HangingBreakByEntityEvent) {
            return;
        }
        Chunk chunkBeingModified = event.getEntity().getChunk();
        Player player = null;
        factionClaimProtect.run(event, chunkBeingModified, player, false, false, true, false);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) throws SQLException {
        Entity remover = event.getRemover();
        Chunk chunkBeingModified = event.getEntity().getChunk();
        boolean allowWarzone = false;
        boolean allowSafezone = false;
        Player player = null;
        if (remover instanceof Player) {
            player = (Player) remover;
            if (player.isOp()) {
                allowWarzone = true;
                allowSafezone = true;
            }
        }
        factionClaimProtect.run(event, chunkBeingModified, player, allowWarzone, allowSafezone, true, false);
    }

    @EventHandler
    public void onFoodLevelChangeEvent (FoodLevelChangeEvent event) throws SQLException {
        Chunk chunkBeingModified = event.getEntity().getChunk();
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        int oldFood = player.getFoodLevel();
        int newFood = event.getFoodLevel();

        boolean allowSafezone = false;
        if (newFood >= oldFood) {
            allowSafezone = true;
        }
        factionClaimProtect.run(event, chunkBeingModified, player, true, allowSafezone, true, true);
    }

    @EventHandler
    public void onPotionSplashEvent (PotionSplashEvent event) throws SQLException {
        for (LivingEntity target : event.getAffectedEntities()) {
            if (target instanceof Player) {
                Chunk chunk = target.getLocation().getChunk();
                FactionChunk fChunk = db.selectFactionUsingChunk(chunk.getX(), chunk.getZ());
                if (fChunk != null && fChunk.factionName.equalsIgnoreCase("safezone")) {
                    event.setIntensity(target, 0.0);
                }
            }
        }
    }

    @EventHandler
    public void onBlockGrowEvent (BlockGrowEvent event) throws SQLException {
        Player player = null;
        factionClaimProtect.run(event, event.getBlock().getChunk(), player, false, false, true, false);
    }

    @EventHandler
    public void onBlockSpreadEvent  (BlockSpreadEvent  event) throws SQLException {
        Player player = null;
        factionClaimProtect.run(event, event.getBlock().getChunk(), player, false, false, true, false);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        int currentPower = db.selectPlayerData(playerId).power;
        currentPower = currentPower - config.factionDeathPowerLose;
        if (currentPower <= config.factionMinPowerPerPlayer) {
            currentPower = config.factionMinPowerPerPlayer;
        }
        db.updatePlayerPower(playerId, currentPower);
        player.sendMessage(Component.text(String.format("Your new power is %d/%d", currentPower, config.factionMaxPowerPerPlayer), NamedTextColor.RED));
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) throws SQLException {
        Player player = event.getPlayer();

        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();

        if (!fromChunk.equals(toChunk) && db.selectPlayerDataAutoMap(player.getUniqueId())) {
            MapCommand.displayMap(db, player, toChunk);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) throws SQLException {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        FactionPlayer factionPlayer = db.selectFactionPlayerMember(playerId);
        if (factionPlayer == null || factionPlayer.rank == PlayerRank.Member) {
            return;
        }
        FactionHome factionHome = db.selectFactionHome(factionPlayer.factionId);
        if (factionHome == null || (factionHome.x == 0 && factionHome.z == 0)) {
            return;
        }
        Location loc = new Location(
            Bukkit.getWorld("world"),
            factionHome.x,
            factionHome.y,
            factionHome.z,
            factionHome.yaw,
            factionHome.pitch
        );
        event.setRespawnLocation(loc);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) throws SQLException {
        Player player = event.getPlayer();
        FactionPlayer factionPlayer = db.selectFactionPlayerMember(player.getUniqueId());
        String displayName;

        if (factionPlayer != null) {
            String prefix = switch (factionPlayer.rank) {
                case Owner -> "***";
                case CoOwner -> "**";
                case Elder -> "*";
                default -> "";
            };

            displayName = String.format("[%s%s] %s", prefix, factionPlayer.factionName, player.getName());
        } else {
            displayName = player.getName();
        }

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            if (viewer instanceof Player playerViewer) {
                try {
                    FactionPlayer viewerFaction = db.selectFactionPlayerMember(playerViewer.getUniqueId());
                    if (viewerFaction != null && factionPlayer != null) {
                        if (viewerFaction.factionId.equals(factionPlayer.factionId)) {
                            return Component.text(String.format("%s: ", displayName)).append(message).color(NamedTextColor.GREEN);
                        }
                    }
                } catch (SQLException e) {
                    // no-op
                }
            }
            return Component.text(String.format("%s: ", displayName)).append(message);
        });
    }

    private void initializeRepeatingTasks () {
        // Interval to increase the power of online players, delays 1 minute to give the DB time to come online
        new BukkitRunnable() {
            @Override
            public void run() {
                String uuids = Bukkit.getOnlinePlayers().stream()
                    .map(p -> String.format("'%s'", p.getUniqueId()))
                    .collect(Collectors.joining(", "));
                try {
                    db.updateIncreasePowerForAllOnlinePlayers(uuids, config.factionPowerIncreaseAmount, config.factionMaxPowerPerPlayer);
                } catch (SQLException e) {
                    logger.severe(e.toString());
                }
            }
        }.runTaskTimer(this, config.factionPowerIncreaseInterval * 60 * 20L, config.factionPowerIncreaseInterval * 60 * 20L); // 15 minutes in ticks
    }
}
