package daveiiii.simpleFactions.data.namedQueries.faction.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import java.sql.*;

public class SelectFactionName  extends BaseQuery {
    public static String run (Connection connection, String factionId) throws SQLException {
        String[] queryItems = {
                "SELECT name",
                "FROM faction",
                "WHERE id = ?",
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(String.format("No such faction %s", factionId));
                }
                return rs.getString("name");
            }
        }
    }
}
