package daveiiii.simpleFactions.data.namedQueries.faction.select;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SelectTotalFactionPageNumber extends BaseQuery {
    public static int run (Connection connection) throws SQLException {
        String[] queryItems = {
            "SELECT COUNT(*) as total",
            "FROM faction",
        };
        String query = BaseQuery.createQuery(queryItems);
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
            throw new SQLException("SelectTotalFactionPageNumber query expected a result");
        }
    }
}
