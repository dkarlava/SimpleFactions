package daveiiii.simpleFactions.data.namedQueries.faction.select;

import types.Faction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectFaction {
    public static Faction run (Connection connection, String factionId) throws SQLException {
        String query = """
            SELECT id, name
            FROM faction
            WHERE id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Faction(rs.getString("name"), rs.getString("id"));
            }
        }
    }
}
