package types;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.Objects;
import java.util.regex.Pattern;

public class PluginConfig {

    public final Pattern factionNameRegex;
    public final int maxFactionNameLength;
    public final int minFactionNameLength;
    public final int factionInviteAutoTimeout;
    public final int factionDisbandAutoTimeout;
    public final int factionPowerIncreaseInterval;
    public final int factionMaxPowerPerPlayer;
    public final int factionMinPowerPerPlayer;
    public final int factionLandClaimCost;
    public final int factionDeathPowerLose;
    public final int factionPowerIncreaseAmount;
    public final String discordLink;

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
        if (factionDisbandAutoTimeout <= 0) {
            this.factionDisbandAutoTimeout = 5;
        } else {
            this.factionDisbandAutoTimeout = factionDisbandAutoTimeout;
        }
        int factionInviteAutoTimeout = config.getInt("faction-invite-auto-timeout");
        if (factionInviteAutoTimeout <= 0) {
            this.factionInviteAutoTimeout = 5;
        } else {
            this.factionInviteAutoTimeout = factionInviteAutoTimeout;
        }

        int factionPowerIncreaseInterval = config.getInt("faction-power-increase-interval");
        if (factionInviteAutoTimeout <= 0) {
            this.factionPowerIncreaseInterval = 15;
        } else {
            this.factionPowerIncreaseInterval = factionPowerIncreaseInterval;
        }
        int factionMaxPowerPerPlayer = config.getInt("faction-max-power-per-player");
        if (factionMaxPowerPerPlayer <= 0) {
            this.factionMaxPowerPerPlayer = 10;
        } else {
            this.factionMaxPowerPerPlayer = factionMaxPowerPerPlayer;
        }
        int factionMinPowerPerPlayer = config.getInt("faction-min-power-per-player");
        if (factionMinPowerPerPlayer >= factionMaxPowerPerPlayer) {
            this.factionMinPowerPerPlayer = -10;
        } else {
            this.factionMinPowerPerPlayer = factionMinPowerPerPlayer;
        }
        int factionLandClaimCost = config.getInt("faction-land-claim-cost");
        if (factionLandClaimCost <= 0) {
            this.factionLandClaimCost = 1;
        } else {
            this.factionLandClaimCost = factionLandClaimCost;
        }
        int factionDeathPowerLose = config.getInt("faction-death-power-lose");
        if (factionDeathPowerLose <= 0) {
            this.factionDeathPowerLose = 4;
        } else {
            this.factionDeathPowerLose = factionDeathPowerLose;
        }
        int factionPowerIncreaseAmount = config.getInt("faction-power-increase-amount");
        if (factionPowerIncreaseAmount <= 0) {
            this.factionPowerIncreaseAmount = 1;
        } else {
            this.factionPowerIncreaseAmount = factionPowerIncreaseAmount;
        }
        this.discordLink = config.getString("discord-link");
    }
}
