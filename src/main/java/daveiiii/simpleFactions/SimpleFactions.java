package daveiiii.simpleFactions;

import daveiiii.simpleFactions.commands.FactionsCommandManager;
import daveiiii.simpleFactions.data.DataBaseHelper;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import types.PluginConfig;
import util.factionCommands.FactionCommandTabCompleter;
import util.other.FactionClaimProtect;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        config = new PluginConfig(getConfig());

        this.saveDefaultConfig();
        logger.info("SimpleFactions has been enabled! AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        factionClaimProtect = new FactionClaimProtect (logger);
        Objects.requireNonNull(this.getCommand("f")).setExecutor(new FactionsCommandManager(config, db, logger));
        Objects.requireNonNull(this.getCommand("f")).setTabCompleter(new FactionCommandTabCompleter());
        getServer().getPluginManager().registerEvents(this, this);
        initializeRepeatingTasks();

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
    public void onPlayerJoin (PlayerJoinEvent event) {
        try {
            if (db.selectPlayerData(event.getPlayer().getUniqueId()) == null) {
                db.insertPlayerData(event.getPlayer().getUniqueId());
            }
        } catch (SQLException e) {
            logger.severe(e.toString());
        }
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) throws SQLException {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        factionClaimProtect.run(db, new FactionClaimProtect.FactionClaimProtectArgs(block.getChunk(), player, event, "You cannot break blocks in claimed land!", true, true));
    }

    @EventHandler
    public void onBlockPlace (BlockPlaceEvent event) throws SQLException {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (event.getItemInHand().getType() == Material.CREEPER_SPAWN_EGG) {
            return;
        }
        factionClaimProtect.run(db, new FactionClaimProtect.FactionClaimProtectArgs(block.getChunk(), player, event, "You cannot place blocks in claimed land!", true, true));
    }

    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) throws SQLException {
        Block block = event.getClickedBlock();
        // Why would block be null?
        if (block == null) {
            return;
        }
        Player player = event.getPlayer();
        factionClaimProtect.run(db, new FactionClaimProtect.FactionClaimProtectArgs(block.getChunk(), player, event, "You cannot do that in claimed land!", true, true));
    }

    @EventHandler
    public void onPlayerInteractEntityEvent (PlayerInteractEntityEvent event) throws SQLException {
        Player player = event.getPlayer();
        Chunk loc = event.getRightClicked().getChunk();
        factionClaimProtect.run(db, new FactionClaimProtect.FactionClaimProtectArgs(loc, player, event, "You cannot do that in claimed land!", true, true));
    }

    @EventHandler
    public void onEntityDamageEvent (EntityDamageEvent event) throws SQLException {
        if (!(event.getDamageSource() instanceof Player player)) {
            return;
        }
        Chunk loc = event.getEntity().getChunk();
        factionClaimProtect.run(db, new FactionClaimProtect.FactionClaimProtectArgs(loc, player, event, "You cannot do that in claimed land!", true, true));
    }

    @EventHandler
    public void onBlockDamageEvent (BlockDamageEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getBlock().getChunk(), event.getPlayer(), event, null, true, true));
    }

    @EventHandler
    public void onBlockFromToEvent (BlockFromToEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getBlock().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onBlockIgniteEvent (BlockIgniteEvent event) throws SQLException {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        factionClaimProtect.run(db, new FactionClaimProtect.FactionClaimProtectArgs(block.getChunk(), player, event, "You cannot do that in claimed land!", true, true));
    }

    @EventHandler
    public void onBlockBurnEvent (BlockBurnEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getBlock().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onBlockPistonExtendEvent (BlockPistonExtendEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getBlock().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onBlockPistonRetractEvent (BlockPistonRetractEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getBlock().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onPlayerBucketEmptyEvent (PlayerBucketEmptyEvent event) throws SQLException {
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Player player = event.getPlayer();
        factionClaimProtect.run(db, new FactionClaimProtect.FactionClaimProtectArgs(block.getChunk(), player, event, "You cannot do that in claimed land!", true, true));
    }

    @EventHandler
    public void onBlockFadeEvent (BlockFadeEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getBlock().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onEntityChangeBlockEvent (EntityChangeBlockEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getBlock().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onEntityDamageByEntityEvent (EntityDamageByEntityEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getEntity().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onEntityExplodeEvent (EntityExplodeEvent event) throws SQLException {
        List<Block> blocks = event.blockList();
        for (Block block : blocks) {
            factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(block.getChunk(), null, event, null, true, true));
        }
    }

    @EventHandler
    public void onEntityInteractEvent (EntityInteractEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getBlock().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onCreatureSpawnEvent (CreatureSpawnEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getEntity().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onHangingBreakEvent (HangingBreakEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getEntity().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onFoodLevelChangeEvent (FoodLevelChangeEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getEntity().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onPotionSplashEvent (PotionSplashEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getEntity().getChunk(), null, event, null, true, true));
    }

    @EventHandler
    public void onBlockGrowEvent (BlockGrowEvent event) throws SQLException {
        factionClaimProtect.zoneRun(db, new FactionClaimProtect.FactionClaimProtectArgs(event.getBlock().getChunk(), null, event, null, true, true));
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
