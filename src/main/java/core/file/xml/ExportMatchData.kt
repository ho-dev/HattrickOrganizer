package core.file.xml

import core.model.match.MatchKurzInfo
import core.model.match.Matchdetails
import core.model.player.Player

class ExportMatchData {
	var players: Map<Int, Player>? = null
	var details: Matchdetails? = null
	var info: MatchKurzInfo? = null
}
