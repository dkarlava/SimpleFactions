package util.other;

import types.PlayerRank;

public class CompareRanks {
    public static boolean compareRanks(PlayerRank playerOne, PlayerRank playerTwo){
        if (playerOne.equals(PlayerRank.Owner)) {
            return !playerTwo.equals(PlayerRank.Owner);
        }
        if (playerOne.equals(PlayerRank.CoOwner)) {
            return !playerTwo.equals(PlayerRank.Owner) && !playerTwo.equals(PlayerRank.CoOwner);
        }
        if (playerOne.equals(PlayerRank.Elder)) {
            return !playerTwo.equals(PlayerRank.Owner) && !playerTwo.equals(PlayerRank.CoOwner) && !playerTwo.equals(PlayerRank.Elder);
        }
        return false;
    }
}
