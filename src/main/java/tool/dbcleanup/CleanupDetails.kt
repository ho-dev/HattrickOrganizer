package tool.dbcleanup

import core.model.enums.MatchType

class CleanupDetails(
    var ownTeamMatchTypes : List<MatchType>,
    var otherTeamMatchTypes : List<MatchType>,
    var ownTeamWeeks : Int,
    var otherTeamWeeks : Int
)