package daveiiii.simpleFactions.data.namedQueries.playerData.insert;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class InsertPlayerData extends BaseQuery {
    public static void run (Connection connection, UUID playerId) throws SQLException {
        String query = "INSERT INTO player_data (player_id) VALUES (?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(playerId));
            stmt.executeQuery();
        }
    }
}