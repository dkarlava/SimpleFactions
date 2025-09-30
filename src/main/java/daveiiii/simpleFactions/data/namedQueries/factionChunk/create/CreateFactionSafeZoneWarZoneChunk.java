package daveiiii.simpleFactions.data.namedQueries.factionChunk.create;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateFactionSafeZoneWarZoneChunk extends BaseQuery {
    public static void run (Connection connection, String factionName, int x, int z) throws SQLException {
        try {
            connection.setAutoCommit(false);
            String getSafeZoneFactionIdQuery = """
                    SELECT id FROM faction WHERE name_unique = ?
            """;
            String safezoneFactionId;
            try (PreparedStatement stmt = connection.prepareStatement(getSafeZoneFactionIdQuery)) {
                stmt.setString(1, factionName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("getSafeZoneFactionIdQuery query expected a result");
                    }
                    safezoneFactionId = rs.getString("id");
                }
            }

            String query = """
                INSERT INTO faction_chunk (faction_id, x, z) VALUES (?, ?, ?) RETURNING id;
            """;
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, String.valueOf(safezoneFactionId));
                stmt.setInt(2, x);
                stmt.setInt(3, z);
                stmt.executeQuery();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }  finally {
            connection.setAutoCommit(true);
        }
    }
}
