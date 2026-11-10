package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.*;
import util.BaseFactionCommand;
import util.other.BroadcastMessageToFactionMembers;

import java.sql.SQLException;

public record DelHome(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            return;
        }

        if (args.length != 0) {
            sender.sendMessage(Component.text("Usage: /f delhome", NamedTextColor.RED));
            return;
        }

        FactionPlayer factionPlayer =  connection.selectFactionPlayerMember(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.factionId == null) {
            player.sendMessage(Component.text("You must be in a faction to run this command.", NamedTextColor.RED));
            return;
        }

        if (factionPlayer.rank == PlayerRank.Owner || factionPlayer.rank == PlayerRank.CoOwner) {
            connection.updateFactionHomeUnset(factionPlayer.factionId);
            BroadcastMessageToFactionMembers.run(connection, factionPlayer.factionId, Component.text(String.format("%s just unset the faction home!", player.getName()), NamedTextColor.RED));
        } else {
            player.sendMessage(Component.text("You must be faction owner or co-owner to run this command.", NamedTextColor.RED));
        }
    }
}