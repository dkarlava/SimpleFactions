package daveiiii.simpleFactions.data.namedQueries.factionMember.insert;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateFactionMemberTable {
    public static void run(Connection connection) throws SQLException {
        String createFactionTable = """
            CREATE TABLE IF NOT EXISTS faction_member (
                id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
                player_id TEXT NOT NULL,
                faction_id TEXT NOT NULL,
                rank TEXT NOT NULL DEFAULT 'member' CHECK (rank IN ('owner', 'co_owner', 'elder', 'member')),
                UNIQUE (player_id),
                FOREIGN KEY (faction_id)  REFERENCES faction(id)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE
            )
        """;

        try (
                Statement stmt = connection.createStatement()) {
            stmt.execute(createFactionTable);
        }
    }
}

