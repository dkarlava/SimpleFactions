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
import org.bukkit.event.entity.EntityDamageEvent;
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
import java.util.Objects;
import java.util.stream.Collectors;

public final class SimpleFactions extends JavaPlugin implements Listener {

    private DataBaseHelper db;
    private PluginConfig config;

    @Override
    public void onEnable() {
        try {
            db = new DataBaseHelper(getLogger(), getDataFolder() + "/factions.db");
        } catch (Exception e) {
            getLogger().severe(e.toString());
        }
        config = new PluginConfig(getConfig());

        this.saveDefaultConfig();
        getLogger().info("SimpleFactions has been enabled! AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Objects.requireNonNull(this.getCommand("f")).setExecutor(new FactionsCommandManager(config, db, getLogger()));
        Objects.requireNonNull(this.getCommand("f")).setTabCompleter(new FactionCommandTabCompleter());
        getServer().getPluginManager().registerEvents(this, this);
        initializeRepeatingTasks();
    }

    @Override
    public void onDisable() {
        try {
            db.close();
        } catch (Exception e) {
            getLogger().severe(e.toString());
        }
    }

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) {
        try {
            if (db.selectPlayerData(event.getPlayer().getUniqueId()) == null) {
                db.insertPlayerData(event.getPlayer().getUniqueId());
            }
        } catch (SQLException e) {
            getLogger().severe(e.toString());
        }
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) throws SQLException {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        FactionClaimProtect.run(db, block.getChunk(), player, event, "You cannot break blocks in claimed land!");
    }

    @EventHandler
    public void onBlockPlace (BlockPlaceEvent event) throws SQLException {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (event.getItemInHand().getType() == Material.CREEPER_SPAWN_EGG) {
            return;
        }
        FactionClaimProtect.run(db, block.getChunk(), player, event, "You cannot place blocks in claimed land!");
    }

    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event) throws SQLException {
        Block block = event.getClickedBlock();
        // Why would block be null?
        if (block == null) {
            return;
        }
        Player player = event.getPlayer();
        FactionClaimProtect.run(db, block.getChunk(), player, event, "You cannot do that in claimed land!");
    }

    @EventHandler
    public void onPlayerInteractEntityEvent (PlayerInteractEntityEvent event) throws SQLException {
        Player player = event.getPlayer();
        Chunk loc = event.getRightClicked().getChunk();
        FactionClaimProtect.run(db, loc, player, event, "You cannot do that in claimed land!");
    }

    @EventHandler
    public void onEntityDamageEvent (EntityDamageEvent event) throws SQLException {
        if (!(event.getDamageSource() instanceof Player player)) {
            return;
        }
        Chunk loc = event.getEntity().getChunk();
        FactionClaimProtect.run(db, loc, player, event, "You cannot do that in claimed land!");
    }

    @EventHandler
    public void onBlockFromToEvent (BlockFromToEvent event) {
        // I would want to protect claims from other players flowing lava into claims, but there is no player, so I cant check the faction
    }

    @EventHandler
    public void onBlockIgniteEvent (BlockIgniteEvent event) throws SQLException {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        FactionClaimProtect.run(db, block.getChunk(), player, event, "You cannot do that in claimed land!");
    }

    @EventHandler
    public void onBlockBurnEvent (BlockBurnEvent event) {
        // I would want to protect claims from other players burning, but there is no player, so I cant check the faction
    }

    @EventHandler
    public void onBlockPistonExtendEvent (BlockPistonExtendEvent event) {
        // I would want to protect claims from piston machines walking into a claim, but there is no player, so I cant check the faction
    }

    @EventHandler
    public void onBlockPistonRetractEvent (BlockPistonRetractEvent event) {
        // I would want to protect claims from piston machines walking into a claim, but there is no player, so I cant check the faction
    }

    @EventHandler
    public void onPlayerBucketEmptyEvent (PlayerBucketEmptyEvent event) throws SQLException {
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Player player = event.getPlayer();
        FactionClaimProtect.run(db, block.getChunk(), player, event, "You cannot do that in claimed land!");
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
                    getLogger().severe(e.toString());
                }
            }
        }.runTaskTimer(this, config.factionPowerIncreaseInterval * 60 * 20L, config.factionPowerIncreaseInterval * 60 * 20L); // 15 minutes in ticks
    }
}
