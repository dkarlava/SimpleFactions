package daveiiii.simpleFactions.data;
import types.Faction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper {

    private Connection connection;

    public void connect (String filePath) throws SQLException {
        String url = "jdbc:sqlite:" + filePath;
        connection = DriverManager.getConnection(url);
        System.out.println("SQLite connected!");
        createTables();
    }

    private void createTables() throws SQLException {
        String createFactions = """
            CREATE TABLE IF NOT EXISTS factions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE NOT NULL
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createFactions);
        }
    }

    public void createFaction(String name) throws SQLException {
        String sql = "INSERT INTO factions(name) VALUES(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    public List<Faction> getAllFactions() throws SQLException {
        String sql = "SELECT name " +
                "FROM factions ";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            List<Faction> ret = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                ret.add(new Faction(name));
            }
            return ret;
        }
    }

    public void close() throws SQLException {
        if (connection != null) connection.close();
    }
}