package daveiiii.simpleFactions.data.namedQueries.playerData.update;

import daveiiii.simpleFactions.data.namedQueries.BaseQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateIncreasePowerForAllOnlinePlayers extends BaseQuery {
    public static void run (Connection connection, String allPlayerUUIDs, int powerIncrease, int maxPower) throws SQLException {
        String query = String.format("UPDATE player_data SET power = power + %d WHERE player_id IN (%s) AND power < %d returning id", powerIncrease, allPlayerUUIDs, maxPower);
        // TODO: Migrate back to the regular syntax for inserting variables to a query, for some reason it wasnt working before
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.executeQuery();
        }
    }
}