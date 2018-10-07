// %2684565945:hoplugins.toTW%
package module.teamOfTheWeek.gui;

import core.db.DBManager;
import core.model.series.Paarung;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;


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

	void setTeamName(String teamName) {
		this.teamName = teamName;
	}

    MatchLineupPlayer() {
        TeamID = -1;
        PositionCode = 0;
        Rating = -1F;

        //        nname = "";
    }

    MatchLineupPlayer(ResultSet rs,Vector<Paarung> matches) {
        try {
            rs.next();
            TeamID = rs.getInt("TEAMID");
            SpielerID = rs.getInt("SPIELERID");
            PositionCode = rs.getInt("HOPOSCODE");
            Rating = rs.getFloat("RATING");
            teamName = getTeamName(matches,TeamID, rs.getInt("MATCHID"));
            
            nname =DBManager.deleteEscapeSequences(rs.getString("NAME"));
        } catch (SQLException e) {
            TeamID = -1;
            PositionCode = 0;
            Rating = -1F;
        }
    }

    
    private String getTeamName(Vector<Paarung> matches,int teamId, int matchId){
    	for (Iterator<Paarung> iterator = matches.iterator(); iterator.hasNext();) {
			Paarung iPaarung = iterator.next();
			if (iPaarung.getMatchId() != matchId)
				continue;
			if(teamId == iPaarung.getHeimId())
            	return iPaarung.getHeimName();
            else if(teamId == iPaarung.getGastId())
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

    int getSpielerID() {
        return SpielerID;
    }

    int getTeamID() {
        return TeamID;
    }

    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("MatchLineupPlayer[");
        buffer.append("TeamID = ").append(TeamID);
        buffer.append(", PositionCode = ").append(PositionCode);
        buffer.append(", SpielerID = ").append(SpielerID);
        buffer.append(", Rating = ").append(Rating);
        buffer.append(", nname = ").append(nname);
        buffer.append("]");
        return buffer.toString();
    }
}
