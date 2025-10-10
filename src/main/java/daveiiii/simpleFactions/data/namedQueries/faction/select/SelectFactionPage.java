package daveiiii.simpleFactions.data.namedQueries.faction.select;

import types.Faction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectFactionPage {
    public static List<Faction> run (Connection connection, int pageNumber) throws SQLException {
        // todo: order by created date or value
        String query = """
            SELECT name, id
            FROM faction
            WHERE name_unique != 'warzone' AND name_unique != 'safezone'
            ORDER BY name_unique ASC
            LIMIT 10
            OFFSET ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, (pageNumber - 1) * 10);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Faction> ret = new ArrayList<>();
                while (rs.next()) {
                    String name = rs.getString("name");
                    String id = rs.getString("id");
                    ret.add(new Faction(name, id));
                }
                return ret;
            }
        }
    }
}
