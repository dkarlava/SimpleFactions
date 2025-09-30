package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.PlayerData;
import types.PluginConfig;
import util.BaseFactionCommand;

import java.sql.SQLException;

public record Power(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            if (args.length != 1) {
                sender.sendMessage(Component.text("Non-players need to specify a player name to use this command."));
            } else {
                String otherPlayerName = args[0];
                Power.getOtherPlayersPower(sender, connection, otherPlayerName);
            }
            return;
        }

        if (args.length != 0) {
            String otherPlayerName = args[0];
            Power.getOtherPlayersPower(sender, connection, otherPlayerName);
        } else {
            PlayerData playerData = connection.selectPlayerData(player.getUniqueId());
            // TODO: allow max power to be a config option
            sender.sendMessage(Component.text(String.format("Your power is %d/%d", playerData.power, 10)));
        }
    }

    private static void getOtherPlayersPower (CommandSender sender, DataBaseHelper connection, String otherPlayerName) throws SQLException {
        Player otherPlayer = Bukkit.getPlayer(otherPlayerName);
        if (otherPlayer == null) {
            // TODO: Throw Error
            return;
        }
        PlayerData otherPlayerData = connection.selectPlayerData(otherPlayer.getUniqueId());
        // TODO: allow max power to be a config option
        sender.sendMessage(Component.text(String.format("%s's power is %d/%d", otherPlayerName, otherPlayerData.power, 10)));
    }
}