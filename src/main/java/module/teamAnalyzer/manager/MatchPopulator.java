package module.teamAnalyzer.manager;

import core.db.DBManager;
import core.model.enums.MatchType;
import core.model.match.MatchLineupPosition;
import core.model.match.MatchLineupTeam;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.vo.Match;
import module.teamAnalyzer.vo.MatchDetail;
import module.teamAnalyzer.vo.MatchRating;
import module.teamAnalyzer.vo.PlayerPerformance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MatchPopulator {
    //~ Static fields/initializers -----------------------------------------------------------------
    private static List<MatchDetail> analyzedMatch = new ArrayList<>();

    //~ Methods ------------------------------------------------------------------------------------
    public static List<MatchDetail> getAnalyzedMatch() {
        return analyzedMatch;
    }

    public static void clean() {
        analyzedMatch = new ArrayList<>();
    }

    public List<MatchDetail> populate(List<Match> matches) {
        List<MatchDetail> list = new ArrayList<>();

        analyzedMatch = new ArrayList<>();
        for (Match match : matches) {
            Match element = match;
            try {
                MatchDetail md = populateMatch(element);
                if (md != null) {
                    list.add(md);
                    analyzedMatch.add(md);
                }
            } catch (RuntimeException e) {
                // DO NOTHING
            }
        }
        return list;
    }

    private int getTacticLevel(Matchdetails aMatchDetail) {
        if (isHome(aMatchDetail)) {
            return aMatchDetail.getHomeTacticSkill();
        } else {
            return aMatchDetail.getGuestTacticSkill();
        }
    }

    private int getTacticType(Matchdetails aMatchDetail) {
        if (isHome(aMatchDetail)) {
            return aMatchDetail.getHomeTacticType();
        } else {
            return aMatchDetail.getGuestTacticType();
        }
    }

    /**
     * Gets a calculator for match ratings.
     *
     * @param aMatchDetail Match details
     *
     * @return Match ratings calculator
     */
    private MatchRating buildMatchRating(Matchdetails aMatchDetail) {
        MatchRating mr = new MatchRating();

        if (isHome(aMatchDetail)) {
            mr.setMidfield(aMatchDetail.getHomeMidfield());
            mr.setLeftAttack(aMatchDetail.getHomeLeftAtt());
            mr.setLeftDefense(aMatchDetail.getHomeLeftDef());
            mr.setCentralAttack(aMatchDetail.getHomeMidAtt());
            mr.setCentralDefense(aMatchDetail.getHomeMidDef());
            mr.setRightAttack(aMatchDetail.getHomeRightAtt());
            mr.setRightDefense(aMatchDetail.getHomeRightDef());
        } else {
            mr.setMidfield(aMatchDetail.getGuestMidfield());
            mr.setLeftAttack(aMatchDetail.getGuestLeftAtt());
            mr.setLeftDefense(aMatchDetail.getGuestLeftDef());
            mr.setCentralAttack(aMatchDetail.getGuestMidAtt());
            mr.setCentralDefense(aMatchDetail.getGuestMidDef());
            mr.setRightAttack(aMatchDetail.getGuestRightAtt());
            mr.setRightDefense(aMatchDetail.getGuestRightDef());
        }
        mr.setHatStats(mr.computeHatStats());
        mr.setLoddarStats(mr.computeLoddarStats());
        mr.setIndirectSetPiecesAtt(aMatchDetail.getRatingIndirectSetPiecesAtt());
        mr.setIndirectSetPiecesDef(aMatchDetail.getRatingIndirectSetPiecesDef());
        return mr;
    }

    private MatchDetail populateMatch(Match aMatch) {
        var matchType = MatchType.getById( aMatch.getMatchType().getMatchTypeId());
    	Matchdetails tmpMatch = Matchdetails.getMatchdetails(aMatch.getMatchId(), matchType);
        MatchDetail matchDetail = new MatchDetail(aMatch);
        MatchLineupTeam tmpLineupTeam;

        if (isHome(tmpMatch)) {
            tmpLineupTeam =  DBManager.instance().loadMatchLineup(matchType.getId(), aMatch.getMatchId()).getHomeTeam();
        } else {
            tmpLineupTeam =  DBManager.instance().loadMatchLineup(matchType.getId(), aMatch.getMatchId()).getGuestTeam();
        }

        double totStars = 0;

        for (int spot = IMatchRoleID.startLineup; spot < IMatchRoleID.startReserves; spot++) {
            MatchLineupPosition mlp = tmpLineupTeam.getPlayerByPosition(spot);

            if (mlp != null && mlp.getPlayerId() > 0) {
                totStars += mlp.getRating();

                PlayerPerformance pp = new PlayerPerformance(mlp);

                pp.setStatus(PlayerDataManager.getLatestPlayerInfo(mlp.getPlayerId()).getStatus());

                matchDetail.addMatchLineupPlayer(pp);
            }
        }

        MatchLineupPosition setPieces = tmpLineupTeam.getPlayerByPosition(IMatchRoleID.setPieces);
        if ( setPieces != null){
            matchDetail.setSetPiecesTaker(setPieces.getPlayerId());
        }

        MatchRating rating = buildMatchRating(tmpMatch);

        // Match is a WO skip it
        if (rating.getHatStats() == 9) {
            return null;
        }

        matchDetail.setRating(rating);
        matchDetail.setStars(totStars);
        matchDetail.setTacticCode(getTacticType(tmpMatch));
        matchDetail.setTacticLevel(getTacticLevel(tmpMatch));

        matchDetail.setFormation(tmpMatch.getFormation(isHome(tmpMatch)));
        NameManager.addNames(tmpMatch.getLineup(isHome(tmpMatch)));

        matchDetail.setIsMarking(tmpMatch.isTeamManMarking(SystemManager.getActiveTeamId()));

        return matchDetail;
    }
    
    private boolean isHome(Matchdetails match) {
        return  match.getHomeTeamId() == SystemManager.getActiveTeamId();
    }
}
