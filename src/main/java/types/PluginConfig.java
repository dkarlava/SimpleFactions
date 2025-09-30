package types;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.Objects;
import java.util.regex.Pattern;

public class PluginConfig {

    public final Pattern factionNameRegex;
    public final Integer maxFactionNameLength;
    public final Integer minFactionNameLength;
    public final Integer factionDisbandAutoTimeout;

    public PluginConfig(FileConfiguration config) {
        this.factionNameRegex = Pattern.compile(Objects.requireNonNullElse(config.getString("faction-name-regex"), "^[a-zA-Z0-9]+$"));
        int minFactionNameLength = config.getInt("faction-name-length-min");
        if (minFactionNameLength <= 0) {
            this.minFactionNameLength = 3;
        } else {
            this.minFactionNameLength = minFactionNameLength;
        }
        int maxFactionNameLength = config.getInt("faction-name-length-max");
        if (maxFactionNameLength < minFactionNameLength || maxFactionNameLength >= 100) {
            this.maxFactionNameLength = 15;
        } else {
            this.maxFactionNameLength = maxFactionNameLength;
        }
        int factionDisbandAutoTimeout = config.getInt("faction-disband-auto-timeout");
        if (minFactionNameLength <= 0) {
            this.factionDisbandAutoTimeout = 5;
        } else {
            this.factionDisbandAutoTimeout = factionDisbandAutoTimeout;
        }
    }
}
