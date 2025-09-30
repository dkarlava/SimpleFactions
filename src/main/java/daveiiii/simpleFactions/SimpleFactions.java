package daveiiii.simpleFactions;

import daveiiii.simpleFactions.commands.FactionsCommandManager;
import daveiiii.simpleFactions.data.DataBaseHelper;
import org.bukkit.plugin.java.JavaPlugin;
import types.PluginConfig;
import util.factionCommands.FactionCommandTabCompleter;
import java.util.Objects;

public final class SimpleFactions extends JavaPlugin {

    private DataBaseHelper db;

    @Override
    public void onEnable() {
        try {
            db = new DataBaseHelper(getLogger(), getDataFolder() + "/factions.db");
        } catch (Exception e) {
            getLogger().severe(e.toString());
        }

        this.saveDefaultConfig();
        getLogger().info("SimpleFactions has been enabled! AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Objects.requireNonNull(this.getCommand("f")).setExecutor(new FactionsCommandManager(new PluginConfig(getConfig()), db, getLogger()));
        Objects.requireNonNull(this.getCommand("f")).setTabCompleter(new FactionCommandTabCompleter());
    }

    @Override
    public void onDisable() {
        try {
            db.close();
        } catch (Exception e) {
            getLogger().severe(e.toString());
        }
    }
}
