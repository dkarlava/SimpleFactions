package daveiiii.simpleFactions.data.namedQueries.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AlterFaction {
    public static void run(Connection connection) throws SQLException {
        try {
            connection.setAutoCommit(false);
            String createNewFactionTable = """
                CREATE TABLE IF NOT EXISTS faction_new (
                    id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
                    name TEXT NOT NULL,
                    name_unique UNIQUE NOT NULL
                )
            """;
            Statement createNewFactionTableStmt = connection.createStatement();
            createNewFactionTableStmt.execute(createNewFactionTable);
//
            String copyNewFactionTable = """
                INSERT INTO faction_new (id, name, name_unique)
                SELECT id, name, name_unique
                FROM faction;
            """;
            Statement copyNewFactionTableStmt = connection.createStatement();
            copyNewFactionTableStmt.execute(copyNewFactionTable);
//
            String dropOldFactionTable = """
                DROP TABLE faction;
            """;
            Statement dropOldFactionTableStmt = connection.createStatement();
            dropOldFactionTableStmt.execute(dropOldFactionTable);
//
            String renameNewTable = """    
                ALTER TABLE faction_new RENAME TO faction;
            """;
            Statement renameNewTableStmt = connection.createStatement();
            renameNewTableStmt.execute(renameNewTable);
//
            String alterFactionAddHomeX = """
                ALTER TABLE faction ADD COLUMN home_x REAL DEFAULT NULL
            """;
            Statement alterFactionAddHomeXStmt = connection.createStatement();
            alterFactionAddHomeXStmt.execute(alterFactionAddHomeX);
//
            String alterFactionAddHomeY = """
                ALTER TABLE faction ADD COLUMN home_y REAL DEFAULT NULL
            """;
            Statement alterFactionAddHomeYStmt = connection.createStatement();
                alterFactionAddHomeYStmt.execute(alterFactionAddHomeY);
//
            String alterFactionAddHomeZ = """
                ALTER TABLE faction ADD COLUMN home_z REAL DEFAULT NULL
            """;
            Statement alterFactionAddHomeZStmt = connection.createStatement();
            alterFactionAddHomeZStmt.execute(alterFactionAddHomeZ);
//
            String alterFactionAddHomeYaw = """
                ALTER TABLE faction ADD COLUMN home_yaw REAL DEFAULT NULL
            """;
            Statement alterFactionAddHomeYawStmt = connection.createStatement();
            alterFactionAddHomeYawStmt.execute(alterFactionAddHomeYaw);
//
            String alterFactionAddHomePitch = """
                ALTER TABLE faction ADD COLUMN home_pitch REAL DEFAULT NULL
            """;
            Statement alterFactionAddHomePitchStmt = connection.createStatement();
            alterFactionAddHomePitchStmt.execute(alterFactionAddHomePitch);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }  finally {
            connection.setAutoCommit(true);
        }
    }
}
