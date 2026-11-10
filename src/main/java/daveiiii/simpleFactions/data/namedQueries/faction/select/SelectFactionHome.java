package daveiiii.simpleFactions.data.namedQueries.faction.select;

import types.FactionHome;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectFactionHome {
    public static FactionHome run (Connection connection, String factionId) throws SQLException {
        String query = """
            SELECT home_x, home_y, home_z, home_yaw, home_pitch
            FROM faction
            WHERE id = ?;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, factionId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {

                    return new FactionHome(
                        resultSet.getDouble("home_x"),
                        resultSet.getDouble("home_y"),
                        resultSet.getDouble("home_z"),
                        resultSet.getFloat("home_yaw"),
                        resultSet.getFloat("home_pitch")
                    );
                } else {
                    return null;
                }
            }
        }
    }
}
