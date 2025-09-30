package daveiiii.simpleFactions.data.namedQueries.faction.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.Faction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectFactionByName extends BaseQuery {
    public static Faction run (Connection connection, String factionName) throws SQLException {
        String[] queryItems = {
                "SELECT id, name",
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
                return new Faction(rs.getString("name"), rs.getString("id"));
            }
        }
    }
}
