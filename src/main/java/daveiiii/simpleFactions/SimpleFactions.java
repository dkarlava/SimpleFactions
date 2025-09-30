package daveiiii.simpleFactions;

import daveiiii.simpleFactions.commands.FactionsCommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import types.PluginConfig;
import util.factionCommands.FactionCommandTabCompleter;

import java.util.Objects;

public final class SimpleFactions extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getLogger().info("SimpleFactions has been enabled! AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Objects.requireNonNull(this.getCommand("f")).setExecutor(new FactionsCommandManager(new PluginConfig(getConfig())));
        Objects.requireNonNull(this.getCommand("f")).setTabCompleter(new FactionCommandTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
