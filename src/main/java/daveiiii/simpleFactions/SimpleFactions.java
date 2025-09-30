package daveiiii.simpleFactions;

import daveiiii.simpleFactions.commands.FactionsCommandManager;
import daveiiii.simpleFactions.data.DataBaseHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import types.PluginConfig;
import util.factionCommands.FactionCommandTabCompleter;

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
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            if (db.selectPlayerData(event.getPlayer().getUniqueId()) == null) {
                db.insertPlayerData(event.getPlayer().getUniqueId());
            }
        } catch (SQLException e) {
            getLogger().severe(e.toString());
        }
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
