package daveiiii.simpleFactions.data.namedQueries.faction.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.Faction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SelectFactionByPlayerId extends BaseQuery {
    public static Faction run (Connection connection, UUID playerId) throws SQLException {
        String[] queryItems = {
                "SELECT f.*",
                "FROM faction_member fm",
                "JOIN faction f ON fm.faction_id = f.id",
                "WHERE fm.player_id = ?",
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, String.valueOf(playerId));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name  = resultSet.getString("name");
                    String id = resultSet.getString("id");
                    return new Faction(name, id);
                } else {
                    return null;
                }
            }
        }
    }
}
