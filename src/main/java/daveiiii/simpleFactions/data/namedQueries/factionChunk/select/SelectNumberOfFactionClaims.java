package daveiiii.simpleFactions.data.namedQueries.factionChunk.select;

import types.FactionChunk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// this one is weird
public class SelectNumberOfFactionClaims {
    public static int run (Connection connection, String factionId) throws SQLException {
        String query = """
            SELECT COUNT(*)
            FROM faction_chunk fc
            JOIN faction f ON fc.faction_id = f.id
            WHERE faction_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return -1;
            }
        }
    }
}
