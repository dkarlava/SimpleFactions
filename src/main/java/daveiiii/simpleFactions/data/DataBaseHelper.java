package daveiiii.simpleFactions.data;
import daveiiii.simpleFactions.data.namedQueries.faction.insert.InsertFaction;
import daveiiii.simpleFactions.data.namedQueries.faction.select.*;
import daveiiii.simpleFactions.data.namedQueries.faction.update.UpdateFactionHome;
import daveiiii.simpleFactions.data.namedQueries.factionChunk.create.CreateFactionChunk;
import daveiiii.simpleFactions.data.namedQueries.factionChunk.create.CreateFactionSafeZoneWarZoneChunk;
import daveiiii.simpleFactions.data.namedQueries.factionChunk.delete.DeleteAllFactionLand;
import daveiiii.simpleFactions.data.namedQueries.factionChunk.delete.DeleteFactionChunk;
import daveiiii.simpleFactions.data.namedQueries.factionChunk.select.SelectAllFactionClaims;
import daveiiii.simpleFactions.data.namedQueries.factionChunk.select.SelectFactionUsingChunk;
import daveiiii.simpleFactions.data.namedQueries.factionChunk.select.SelectNearbyChunks;
import daveiiii.simpleFactions.data.namedQueries.factionChunk.update.UpdateFactionChunk;
import daveiiii.simpleFactions.data.namedQueries.factionChunk.update.UpdateFactionSafeZoneWarZoneChunk;
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
import daveiiii.simpleFactions.data.namedQueries.factionMember.update.UpdateFactionMemberRank;
import daveiiii.simpleFactions.data.namedQueries.playerData.insert.InsertPlayerData;
import daveiiii.simpleFactions.data.namedQueries.playerData.select.SelectPlayerData;
import daveiiii.simpleFactions.data.namedQueries.playerData.select.SelectPlayerDataAutoMap;
import daveiiii.simpleFactions.data.namedQueries.playerData.update.UpdateIncreasePowerForAllOnlinePlayers;
import daveiiii.simpleFactions.data.namedQueries.playerData.update.UpdatePlayerDataAutoMap;
import daveiiii.simpleFactions.data.namedQueries.playerData.update.UpdatePlayerPower;
import daveiiii.simpleFactions.data.namedQueries.tables.*;
import daveiiii.simpleFactions.data.namedQueries.factionMember.delete.DeleteFactionMember;
import daveiiii.simpleFactions.data.namedQueries.factionMember.select.SelectFactionMember;
import types.*;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Map;
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
        CreatePlayerData.run(connection);
        logger.info("CreatePlayerData created");
        CreateFactionChunkTable.run(connection);
        logger.info("CreateFactionChunkTable created");
        AlterCreatePlayerData.run(connection);
        logger.info("AlterCreatePlayerData altered");
        logger.info("========== DATABASE INITIALIZED ==========");
    }

    public void insertFaction(String factionName, UUID playerId) throws SQLException {
        InsertFaction.run(connection, factionName,  playerId);
    }

    public Faction selectFactionByPlayerId (UUID playerId) throws SQLException {
        return SelectFactionByPlayerId.run(connection, playerId);
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

    public void deleteFactionMember (UUID playerId) throws SQLException {
        DeleteFactionMember.run(connection, playerId);
    }

    public PlayerData selectPlayerData (UUID playerId) throws SQLException {
        return SelectPlayerData.run(connection, playerId);
    }

    public void insertPlayerData (UUID playerId) throws SQLException {
        InsertPlayerData.run(connection, playerId);
    }

    public void updateIncreasePowerForAllOnlinePlayers (String allPlayerUUIDs, int powerIncrease, int maxPower) throws SQLException {
        UpdateIncreasePowerForAllOnlinePlayers.run(connection, allPlayerUUIDs, powerIncrease, maxPower);
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

    public List<FactionPlayer> selectAllFactionMembersUsingFactionId (String factionId) throws SQLException {
        return SelectAllFactionMembersUsingFactionId.run(connection, factionId);
    }

    public void createFactionChunk (String factionId, int x, int z) throws SQLException {
        CreateFactionChunk.run(connection, factionId, x, z);
    }

    public void createFactionSafezoneChunk (int x, int z) throws SQLException {
        CreateFactionSafeZoneWarZoneChunk.run(connection, "safezone", x, z);
    }

    public void createFactionWarzoneChunk (int x, int z) throws SQLException {
        CreateFactionSafeZoneWarZoneChunk.run(connection, "warzone", x, z);
    }

    public Map<String, FactionChunk> selectNearbyChunks(int minX, int maxX, int minZ, int maxZ) throws SQLException {
        return SelectNearbyChunks.run(connection, minX, maxX, minZ, maxZ);
    }

    public FactionChunk selectFactionUsingChunk(int x, int z) throws SQLException {
        return SelectFactionUsingChunk.run(connection, x, z);
    }

    public void deleteFactionChunk (int x, int z) throws SQLException {
        DeleteFactionChunk.run(connection, x, z);
    }

    public void updatePlayerPower (UUID playerId, int newPower) throws SQLException {
        UpdatePlayerPower.run(connection, playerId, newPower);
    }

    public List<FactionChunk> selectAllFactionClaims (String factionId) throws SQLException {
        return SelectAllFactionClaims.run(connection, factionId);
    }

    public void updatePlayerDataAutoMap (UUID playerId, boolean autoMap) throws SQLException {
        UpdatePlayerDataAutoMap.run(connection, playerId, autoMap);
    }

    public boolean selectPlayerDataAutoMap (UUID playerId) throws SQLException {
        return SelectPlayerDataAutoMap.run(connection, playerId);
    }

    public void updateFactionChunk (String factionId, int x, int z) throws SQLException {
        UpdateFactionChunk.run(connection, factionId, x, z);
    }

    public Faction selectFaction (String factionId) throws SQLException {
        return SelectFaction.run(connection, factionId);
    }

    public void updateFactionSafezoneChunk (int x, int z) throws SQLException {
        UpdateFactionSafeZoneWarZoneChunk.run(connection, "safezone", x, z);
    }

    public void updateFactionWarzoneChunk (int x, int z) throws SQLException {
        UpdateFactionSafeZoneWarZoneChunk.run(connection, "warzone", x, z);
    }

    public void deleteAllFactionLand (String factionId) throws SQLException {
        DeleteAllFactionLand.run(connection, factionId);
    }

    public void updateFactionHome (String factionId, double x, double y, double z, float yaw, float pitch) throws SQLException {
        UpdateFactionHome.run(connection, factionId, x, y, z, yaw, pitch);
    }

    public FactionHome selectFactionHome (String factionId) throws SQLException {
        return SelectFactionHome.run(connection, factionId);
    }

    public void updateFactionMemberRank (UUID playerId, PlayerRank rank) throws SQLException {
        UpdateFactionMemberRank.run(connection, playerId, rank);
    }

    public void close () throws SQLException {
        if (connection != null) connection.close();
    }
}