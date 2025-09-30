package daveiiii.simpleFactions.data.namedQueries.faction.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;
import types.Faction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SelectFactionPage extends BaseQuery {
    public static List<Faction> run (Connection connection, int pageNumber) throws SQLException {
        String[] queryItems = {
            "SELECT name",
            "FROM faction",
            "LIMIT 10",
            String.format("OFFSET %d", (pageNumber - 1) * 10),
        };
        String query = BaseQuery.createQuery(queryItems);
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            List<Faction> ret = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                ret.add(new Faction(name));
            }
            return ret;
        }
    }
}
