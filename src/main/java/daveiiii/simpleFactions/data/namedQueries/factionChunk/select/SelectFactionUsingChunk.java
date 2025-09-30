package daveiiii.simpleFactions.data.namedQueries.factionChunk.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.FactionChunk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectFactionUsingChunk extends BaseQuery {
    public static FactionChunk run (Connection connection, int x, int z) throws SQLException {
        // TODO: codebase wide refactor to use the triple quotes syntax
        String query = """
            SELECT fc.faction_id, fc.x, fc.z, f.name
            FROM faction_chunk fc
            JOIN faction f ON fc.faction_id = f.id
            WHERE x = ? AND z = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, x);
            stmt.setInt(2, z);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new FactionChunk(rs.getString("faction_id"), rs.getInt("x"), rs.getInt("z"), rs.getString("name"));
            }
        }
    }
}