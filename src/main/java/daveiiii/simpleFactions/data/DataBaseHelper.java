package daveiiii.simpleFactions.data;
import daveiiii.simpleFactions.data.namedQueries.faction.insert.InsertFaction;
import daveiiii.simpleFactions.data.namedQueries.faction.insert.CreateFactionTable;
import daveiiii.simpleFactions.data.namedQueries.faction.select.SelectFactionPlayerIsIn;
import daveiiii.simpleFactions.data.namedQueries.faction.select.SelectTotalFactionPageNumber;
import daveiiii.simpleFactions.data.namedQueries.faction.select.SelectFactionPage;
import daveiiii.simpleFactions.data.namedQueries.factionMember.delete.DeleteFactionMember;
import daveiiii.simpleFactions.data.namedQueries.factionMember.insert.CreateFactionMemberTable;
import types.Faction;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class DataBaseHelper {

    private final Logger logger;

    private final Connection connection;

    public DataBaseHelper(Logger logger, String filePath) throws SQLException {
        this.logger = logger;
        File dbFile = new File(filePath);
        File ParentDir = dbFile.getParentFile();
        if (ParentDir != null  && !ParentDir.exists()) {
            if (!ParentDir.mkdirs()) {
                logger.warning("Parent directories already exist or could not be created: " + ParentDir);
            }
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + filePath);
        createTables();
    }

    private void createTables () throws SQLException {
        logger.info("========== START DATABASE INIT ==========");
        CreateFactionTable.run(connection);
        logger.info("CreateFactionTable created");
        CreateFactionMemberTable.run(connection);
        logger.info("CreateFactionMemberTable created");
        logger.info("========== DATABASE INITIALIZED ==========");
    }

    public void insertFaction(String factionName, UUID playerId) throws SQLException {
        InsertFaction.run(connection, factionName,  playerId);
    }

    public Faction selectFactionPlayerIsIn (UUID playerId) throws SQLException {
        return SelectFactionPlayerIsIn.run(connection, playerId);
    }

    public void deleteFactionPlayerIsIn (UUID playerId) throws SQLException {
        DeleteFactionMember.run(connection, playerId);
    }

    public List<Faction> selectFactionPage (int pageNumber) throws SQLException {
        return SelectFactionPage.run(connection, pageNumber);
    }

    public int selectTotalFactionPageNumber () throws SQLException {
        return SelectTotalFactionPageNumber.run(connection);
    }

    public void close () throws SQLException {
        if (connection != null) connection.close();
    }
}