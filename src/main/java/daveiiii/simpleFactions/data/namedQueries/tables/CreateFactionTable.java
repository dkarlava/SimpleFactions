package daveiiii.simpleFactions.data.namedQueries.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateFactionTable {
    public static void run(Connection connection) throws SQLException {
        String createFactionTable = """
            CREATE TABLE IF NOT EXISTS faction (
                id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
                name TEXT NOT NULL,
                name_unique UNIQUE NOT NULL
            )
        """;

        try (
            Statement stmt = connection.createStatement()) {
            stmt.execute(createFactionTable);
        }
    }
}
