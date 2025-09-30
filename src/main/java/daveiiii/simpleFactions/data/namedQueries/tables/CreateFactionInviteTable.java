package daveiiii.simpleFactions.data.namedQueries.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateFactionInviteTable {
    public static void run(Connection connection) throws SQLException {
        String createFactionInviteTable = """
            CREATE TABLE IF NOT EXISTS faction_invite (
                id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
                player_id TEXT NOT NULL,
                faction_id TEXT NOT NULL,
                timestamp INTEGER DEFAULT NULL,
                UNIQUE (faction_id, player_id),
                FOREIGN KEY (faction_id) REFERENCES faction(id)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE,
                FOREIGN KEY (player_id) REFERENCES faction_member(id)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createFactionInviteTable);
        }
    }
}