package daveiiii.simpleFactions.data.namedQueries.factionMember.delete;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DeleteFactionMember extends BaseQuery {
    public static void run (Connection connection, UUID playerId) throws SQLException {
        String deleteFactionMember = "DELETE FROM faction_member WHERE player_id = ? RETURNING player_id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteFactionMember)) {
            preparedStatement.setObject(1, playerId);
            preparedStatement.executeQuery();
        }
    }
}
