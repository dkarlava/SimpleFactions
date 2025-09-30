package daveiiii.simpleFactions.data.namedQueries.playerData.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.PlayerData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SelectPlayerData extends BaseQuery {
    public static PlayerData run (Connection connection, UUID playerId) throws SQLException {
        String[] queryItems = {
                "SELECT power",
                "FROM player_data",
                "WHERE player_id = ?",
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(playerId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new PlayerData(playerId, rs.getInt("power"));
            }
        }
    }
}