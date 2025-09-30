package daveiiii.simpleFactions.data.namedQueries.faction.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectFactionByName extends BaseQuery {
    public static String run (Connection connection, String factionName) throws SQLException {
        String[] queryItems = {
                "SELECT id",
                "FROM faction",
                "WHERE name_unique = ?",
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionName.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getString("id");
            }
        }
    }
}
