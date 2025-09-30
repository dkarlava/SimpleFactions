package daveiiii.simpleFactions.data;
import daveiiii.simpleFactions.data.namedQueries.faction.insert.InsertFaction;
import daveiiii.simpleFactions.data.namedQueries.faction.select.SelectFactionByName;
import daveiiii.simpleFactions.data.namedQueries.factionDisband.delete.DeleteFactionDisband;
import daveiiii.simpleFactions.data.namedQueries.factionDisband.insert.InsertFactionDisband;
import daveiiii.simpleFactions.data.namedQueries.factionDisband.select.SelectFactionDisbandedPrimed;
import daveiiii.simpleFactions.data.namedQueries.factionDisband.update.UpdateFactionDisbandTimestampAndPlayerId;
import daveiiii.simpleFactions.data.namedQueries.factionInvite.delete.DeleteFactionInvite;
import daveiiii.simpleFactions.data.namedQueries.factionInvite.insert.InsertPlayerInvite;
import daveiiii.simpleFactions.data.namedQueries.factionInvite.select.SelectFactionInvitePrimed;
import daveiiii.simpleFactions.data.namedQueries.factionInvite.update.UpdatePlayerInviteTimestamp;
import daveiiii.simpleFactions.data.namedQueries.factionMember.insert.InsertFactionMember;
import daveiiii.simpleFactions.data.namedQueries.factionMember.select.SelectAllFactionMembersUsingFactionId;
import daveiiii.simpleFactions.data.namedQueries.tables.CreateFactionDisbandTable;
import daveiiii.simpleFactions.data.namedQueries.tables.CreateFactionInviteTable;
import daveiiii.simpleFactions.data.namedQueries.tables.CreateFactionTable;
import daveiiii.simpleFactions.data.namedQueries.faction.select.SelectFactionPlayerIsIn;
import daveiiii.simpleFactions.data.namedQueries.faction.select.SelectTotalFactionPageNumber;
import daveiiii.simpleFactions.data.namedQueries.faction.select.SelectFactionPage;
import daveiiii.simpleFactions.data.namedQueries.factionMember.delete.DeleteFactionMember;
import daveiiii.simpleFactions.data.namedQueries.tables.CreateFactionMemberTable;
import daveiiii.simpleFactions.data.namedQueries.factionMember.select.SelectFactionMember;
import types.Faction;
import types.FactionDisband;
import types.FactionInvite;
import types.FactionPlayer;

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
        CreateFactionDisbandTable.run(connection);
        logger.info("CreateFactionDisbandTable created");
        CreateFactionInviteTable.run(connection);
        logger.info("CreateFactionInviteTable created");
        logger.info("========== DATABASE INITIALIZED ==========");
    }

    public void insertFaction(String factionName, UUID playerId) throws SQLException {
        InsertFaction.run(connection, factionName,  playerId);
    }

    public Faction selectFactionPlayerIsIn (UUID playerId) throws SQLException {
        return SelectFactionPlayerIsIn.run(connection, playerId);
    }

    public FactionPlayer selectFactionPlayerMember(UUID playerId) throws SQLException {
        return SelectFactionMember.run(connection, playerId);
    }

    public FactionDisband selectFactionDisbandedPrimed (String factionId) throws SQLException {
        return SelectFactionDisbandedPrimed.run(connection, factionId);
    }

    public FactionInvite selectFactionInvitePrimed (String factionId, UUID playerId) throws SQLException {
        return SelectFactionInvitePrimed.run(connection, factionId, playerId);
    }

    public void updatePlayerInviteTimestamp (String factionId, UUID playerId) throws SQLException {
        UpdatePlayerInviteTimestamp.run(connection, factionId, playerId);
    }

    public void insertFactionMember (String factionId, UUID playerId) throws SQLException {
        InsertFactionMember.run(connection, factionId, playerId);
    }

    public void insertFactionDisbandedPrimed (UUID playerId, String factionId) throws SQLException {
        InsertFactionDisband.run(connection, playerId, factionId);
    }

    public void updateFactionDisbandTimestampAndPlayerId (String factionDisbandedId, UUID playerId) throws SQLException {
        UpdateFactionDisbandTimestampAndPlayerId.run(connection, factionDisbandedId, playerId);
    }

    public void deleteFactionInvite (UUID playerId, String factionId) throws SQLException {
        DeleteFactionInvite.run(connection, playerId, factionId);
    }

    public void deleteFactionPlayerIsIn (UUID playerId) throws SQLException {
        DeleteFactionMember.run(connection, playerId);
    }

    public String deleteFactionDisband (String factionDisbandId) throws SQLException {
        return DeleteFactionDisband.run(connection, factionDisbandId);
    }

    public List<Faction> selectFactionPage (int pageNumber) throws SQLException {
        return SelectFactionPage.run(connection, pageNumber);
    }

    public int selectTotalFactionPageNumber () throws SQLException {
        return SelectTotalFactionPageNumber.run(connection);
    }

    public Faction selectFactionByName (String factionName) throws SQLException {
        return SelectFactionByName.run(connection, factionName);
    }

    public void insertPlayerInvite (String factionId, UUID playerId) throws SQLException {
        InsertPlayerInvite.run(connection, factionId, playerId);
    }

    public List<UUID> selectAllFactionMembersUsingFactionId (String factionId) throws SQLException {
        return SelectAllFactionMembersUsingFactionId.run(connection, factionId);
    }

    public void close () throws SQLException {
        if (connection != null) connection.close();
    }
}