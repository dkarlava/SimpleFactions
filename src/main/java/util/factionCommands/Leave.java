package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;
import java.sql.SQLException;
import java.util.UUID;

public record Leave (PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length != 0) {
            sender.sendMessage("Usage: /f leave");
            return;
        }

        UUID playerId = player.getUniqueId();
        FactionPlayer factionPlayer = connection.selectFactionPlayerMember(playerId);
        if (factionPlayer == null) {
            sender.sendMessage(Component.text("You must be in a faction to leave a faction.").color(NamedTextColor.RED));
            return;
        }

        if (factionPlayer.rank == PlayerRank.Owner) {
            int factionMembers = connection.selectAllFactionMembersUsingFactionId(factionPlayer.factionId).size();
            if (factionMembers > 1) {
                sender.sendMessage(Component.text("You must transfer ownership to someone else or disband the faction before you can leave").color(NamedTextColor.RED));
            } else {
                sender.sendMessage(Component.text("You are the only member of the faction. Use /f disband instead").color(NamedTextColor.RED));
            }
            return;
        }

        connection.deleteFactionMember(playerId);
        sender.sendMessage(Component.text("You have left the faction.").color(NamedTextColor.GRAY));
    }
}
