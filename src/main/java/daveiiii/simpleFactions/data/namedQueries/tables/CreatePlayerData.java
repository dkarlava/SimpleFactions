package daveiiii.simpleFactions.data.namedQueries.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreatePlayerData {
    public static void run(Connection connection) throws SQLException {
        String createPlayerDataTable = """
            CREATE TABLE IF NOT EXISTS player_data (
                id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
                player_id TEXT NOT NULL,
                power INTEGER NOT NULL DEFAULT 0
            )
        """;

        try (
            Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayerDataTable);
        }
    }
}
