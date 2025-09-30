package daveiiii.simpleFactions.data.namedQueries.factionChunk.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.FactionChunk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SelectNearbyChunks extends BaseQuery {
    public static Map<String, FactionChunk> run (Connection connection, int minX, int maxX, int minZ, int maxZ) throws SQLException {
        // TODO: codebase wide refactor to use the triple quotes syntax
        String[] queryItems = {
                "SELECT faction_id, x, z",
                "FROM faction_chunk",
                "WHERE x >= ? AND x <= ? AND z >= ? AND z <= ?",
        };
        String query = BaseQuery.createQuery(queryItems);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, minX);
            stmt.setInt(2, maxX);
            stmt.setInt(3, minZ);
            stmt.setInt(4, maxZ);
            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, FactionChunk> ret = new HashMap<>();
                while (rs.next()) {
                    int x = rs.getInt("x");
                    int z = rs.getInt("z");
                    ret.put(String.format("(%d,%d)", z, x), new FactionChunk(rs.getString("faction_id"), x, z));
                }
                return ret;
            }
        }
    }
}