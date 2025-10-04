package daveiiii.simpleFactions.data.namedQueries.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createFactionTable);
            String insertSafeZoneFaction = """
                INSERT OR IGNORE INTO faction (name, name_unique) VALUES (?, ?)
            """;
            try (PreparedStatement safezoneStmt = connection.prepareStatement(insertSafeZoneFaction)) {
                safezoneStmt.setString(1, "SafeZone");
                safezoneStmt.setString(2, "safezone");
                safezoneStmt.execute();
            }
            String insertWarZoneFaction = """
                INSERT OR IGNORE INTO faction (name, name_unique) VALUES (?, ?)
            """;
            try (PreparedStatement warzoneStmt = connection.prepareStatement(insertWarZoneFaction)) {
                warzoneStmt.setString(1, "WarZone");
                warzoneStmt.setString(2, "warzone");
                warzoneStmt.execute();
            }
        }
    }
}
