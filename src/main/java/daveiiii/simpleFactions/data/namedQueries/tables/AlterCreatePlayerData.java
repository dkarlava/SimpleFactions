package daveiiii.simpleFactions.data.namedQueries.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AlterCreatePlayerData {
    public static void run(Connection connection) throws SQLException {
        String alterCreatePlayerData = """
            ALTER TABLE player_data
            ADD COLUMN auto_map BOOLEAN NOT NULL DEFAULT FALSE
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(alterCreatePlayerData);
        } catch (SQLException e) {
            // no-op, just means the column is already created
        }
    }
}
