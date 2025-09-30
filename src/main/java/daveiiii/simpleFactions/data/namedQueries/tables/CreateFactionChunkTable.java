package daveiiii.simpleFactions.data.namedQueries.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateFactionChunkTable {
    public static void run(Connection connection) throws SQLException {
        String createFactionChunkTable = """
            CREATE TABLE IF NOT EXISTS faction_chunk (
                id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
                faction_id TEXT NOT NULL,
                x INTEGER NOT NULL,
                z INTEGER NOT NULL,
                UNIQUE (x, z),
                FOREIGN KEY (faction_id) REFERENCES faction(id)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createFactionChunkTable);
        }
    }
}