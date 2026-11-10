package util.other;

import daveiiii.simpleFactions.data.DataBaseHelper;
import types.FactionPlayer;
import java.sql.SQLException;
import java.util.List;

public class GetTotalLandData {
    public static class GetTotalLandDataReturn {
        public int totalPower;
        public int totalLand;
        GetTotalLandDataReturn (int totalPower, int totalLand) {
            this.totalLand = totalLand;
            this.totalPower = totalPower;
        }
    }

    public static GetTotalLandDataReturn run (DataBaseHelper connection, String factionId) throws SQLException {
        int totalPower = 0;
        List<FactionPlayer> factionPlayers = connection.selectAllFactionMembersUsingFactionId(factionId);
        if (factionPlayers == null) {
            return new GetTotalLandDataReturn(-1, -1);
        }

        for (FactionPlayer overclaimfactionPlayer : factionPlayers) {
            totalPower = totalPower + overclaimfactionPlayer.power;
        }
        int numberOfAllClaims = connection.selectAllFactionClaims(factionId).size();
        return new GetTotalLandDataReturn(totalPower, numberOfAllClaims);
    }
}
