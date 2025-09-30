package daveiiii.simpleFactions.data.namedQueries.factionChunk.select;

import types.FactionChunk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectAllFactionClaims {
    public static List<FactionChunk> run (Connection connection, String factionId) throws SQLException {
        String query = """
            SELECT fc.faction_id, fc.x, fc.z, f.name
            FROM faction_chunk fc
            JOIN faction f ON fc.faction_id = f.id
            WHERE faction_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<FactionChunk> ret = new ArrayList<>();
                while (rs.next()) {
                    ret.add(new FactionChunk(rs.getString("faction_id"), rs.getInt("x"), rs.getInt("z"), rs.getString("name")));
                }
                return ret;
            }
        }
    }
}
