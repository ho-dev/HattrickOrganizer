// %2684565945:hoplugins.toTW%
package module.teamOfTheWeek.gui;

import core.model.series.Paarung;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

class MatchLineupPlayer {
    //~ Instance fields ----------------------------------------------------------------------------

    private String nname;
    private float Rating;
    private int PositionCode;
    private int SpielerID;
    private int TeamID;
    private String teamName;

    //~ Constructors -------------------------------------------------------------------------------

    String getTeamName() {
		return teamName;
	}

    MatchLineupPlayer(ResultSet rs, List<Paarung> matches) {
        try {
            rs.next();
            TeamID = rs.getInt("TEAMID");
            SpielerID = rs.getInt("SPIELERID");
            PositionCode = rs.getInt("HOPOSCODE");
            Rating = rs.getFloat("RATING");
            teamName = getTeamName(matches,TeamID, rs.getInt("MATCHID"));
            
            nname =rs.getString("NAME");
        } catch (SQLException e) {
            TeamID = -1;
            PositionCode = 0;
            Rating = -1F;
        }
    }

    
    private String getTeamName(List<Paarung> matches,int teamId, int matchId){
        for (Paarung iPaarung : matches) {
            if (iPaarung.getMatchId() != matchId)
                continue;
            if (teamId == iPaarung.getHeimId())
                return iPaarung.getHeimName();
            else if (teamId == iPaarung.getGastId())
                return iPaarung.getGastName();
        }
    	return "";
    }
    
    //~ Methods ------------------------------------------------------------------------------------

    final String getNname() {
        return nname;
    }

    int getPositionCode() {
        return PositionCode;
    }

    float getRating() {
        return Rating;
    }

    @Override
	public String toString() {
        String buffer = "MatchLineupPosition[" +
                "TeamID = " + TeamID +
                ", PositionCode = " + PositionCode +
                ", SpielerID = " + SpielerID +
                ", Rating = " + Rating +
                ", nname = " + nname +
                "]";
        return buffer;
    }
}
