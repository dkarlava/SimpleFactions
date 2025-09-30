package util.other;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import types.FactionPlayer;

import java.sql.SQLException;
import java.util.List;

public class BroadcastMessageToFactionMembers {
    // TODO: I think player joining and being kicked need to be refactored to use this
    public static void run(DataBaseHelper connection, String factionId, Component message) throws SQLException {
        List<FactionPlayer> otherMembers = connection.selectAllFactionMembersUsingFactionId(factionId);
        for (FactionPlayer otherPlayers : otherMembers) {
            Player otherPlayer = Bukkit.getPlayer(otherPlayers.playerId);
            if (otherPlayer != null) {
                otherPlayer.sendMessage(message);
            }
        }
    }
}
