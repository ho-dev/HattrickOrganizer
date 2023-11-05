package core.file.xml

import java.util.Vector

import core.db.user.UserManager
import core.model.HOVerwaltung
import core.model.player.Player
import core.model.player.PlayerCategory
import core.model.player.TrainerType
import module.youth.YouthPlayer
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import core.db.DBManager
import core.util.HOLogger
import module.training.Skills

object XMLPlayersParser {

    fun parsePlayersFromString(inputStream: String): Vector<SafeInsertMap> {
        val doc:Document? = XMLManager.parseString(inputStream)
        return createListe(doc)
    }

    fun parsePlayerDetails(inputStream: String): SafeInsertMap {
        val doc:Document? = XMLManager.parseString(inputStream)
        val root:Element? = doc?.documentElement
        val ele:Element? = root?.getElementsByTagName("Player")?.item(0) as Element?
        return createPlayerDetails(ele)
    }

    /////////////////////////////////////////////////////////////////////////////////
    //Parser Helper private
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * erzeugt das Team aus dem xml
     */
    fun createListe(doc: Document?): Vector<SafeInsertMap> {
        val liste: Vector<SafeInsertMap>  = Vector()

        try {
            var root:Element? = doc?.documentElement
            root = root?.getElementsByTagName("Team")?.item(0) as Element?
            val ele:Element? = root?.getElementsByTagName("TeamID")?.item(0) as Element?
            val teamID = XMLManager.getFirstChildNodeValue(ele)
            root = root?.getElementsByTagName("PlayerList")?.item(0) as Element?

            val list = root?.getElementsByTagName("Player")

            if (list != null) {
                for (i in 0..<list.length) {
                    root = list.item (i) as Element?
                    val hash = createPlayer(root, teamID)

                    liste.add(hash)
                }
            }
        } catch (ignored: Exception) {
        }

        return liste
    }

    fun createPlayerDetails(root: Element?): SafeInsertMap {

        val owningTeam = root?.getElementsByTagName("OwningTeam")?.item(0) as Element?
        val teamID = XMLManager.xmlValue(owningTeam, "TeamID")
        val teamName = XMLManager.xmlValue(owningTeam, "TeamName")
        val hash = createPlayer(root, teamID)
        hash.insert("TeamName", teamName)

        if (root != null) {
            XMLManager.xmlValue2Hash(hash, root, "OwnerNotes")
            XMLManager.xmlValue2Hash(hash, root, "Statement")
            XMLManager.xmlValue2Hash(hash, root, "NativeCountryID", "CountryID")
            XMLManager.xmlValue2Hash(hash, root, "NativeLeagueID")
            XMLManager.xmlValue2Hash(hash, root, "NativeLeagueName")
            XMLManager.xmlValue2Hash(hash, root, "NationalTeamName")
        }

        val transferDetails = root?.getElementsByTagName("TransferDetails")?.item(0) as Element?
        if (transferDetails != null) {
            XMLManager.xmlValue2Hash(hash, transferDetails, "AskingPrice")
            XMLManager.xmlValue2Hash(hash, transferDetails, "Deadline")
            XMLManager.xmlValue2Hash(hash, transferDetails, "HighestBid")
        }

        val motherclub = root?.getElementsByTagName("MotherClub")?.item(0) as Element?
        if (motherclub != null) {
            XMLManager.xmlValue2Hash(hash, motherclub, "TeamID", "MotherclubTeamID")
            XMLManager.xmlValue2Hash(hash, motherclub, "TeamName", "MotherclubTeam")
        }

        return hash
    }

    private fun createPlayer(root: Element?, teamID: String): SafeInsertMap {
        val hash = SafeInsertMap()

        hash.insert("TeamID", teamID)

        var ele:Element? = root?.getElementsByTagName("PlayerID")?.item(0) as Element?
        hash.insert("PlayerID", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("FirstName")?.item(0) as Element?
        hash.insert("FirstName", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("NickName")?.item(0) as Element?
        hash.insert("NickName", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("LastName")?.item(0) as Element?
        hash.insert("LastName", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("PlayerNumber")?.item(0) as Element?
        hash.insert("PlayerNumber", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Age")?.item(0) as Element?
        hash.insert("Age", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("AgeDays")?.item(0) as Element?
        hash.insert("AgeDays", (XMLManager.getFirstChildNodeValue(ele)))

        XMLManager.xmlValue2Hash(hash, root, "ArrivalDate")

        ele = root?.getElementsByTagName("TSI")?.item(0) as Element?
        hash.insert("MarketValue", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("PlayerForm")?.item(0) as Element?
        hash.insert("PlayerForm", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("PlayerCategoryId")?.item(0) as Element?
        hash.insert("PlayerCategoryId", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Experience")?.item(0) as Element?
        hash.insert("Experience", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Loyalty")?.item(0) as Element?
        hash.insert("Loyalty", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("MotherClubBonus")?.item(0) as Element?
        hash.insert("MotherClubBonus", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Leadership")?.item(0) as Element?
        hash.insert("Leadership", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Salary")?.item(0) as Element?
        hash.insert("Salary", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Agreeability")?.item(0) as Element?
        hash.insert("Agreeability", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Aggressiveness")?.item(0) as Element?
        hash.insert("Aggressiveness", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Honesty")?.item(0) as Element?
        hash.insert("Honesty", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("LeagueGoals")?.item(0) as Element?
        hash.insert("LeagueGoals", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("CupGoals")?.item(0) as Element?
        hash.insert("CupGoals", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("FriendliesGoals")?.item(0) as Element?
        hash.insert("FriendliesGoals", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("CareerGoals")?.item(0) as Element?
        hash.insert("CareerGoals", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("CareerHattricks")?.item(0) as Element?
        hash.insert("CareerHattricks", (XMLManager.getFirstChildNodeValue(ele)))

        XMLManager.xmlValue2Hash(hash, root, "MatchesCurrentTeam")
        XMLManager.xmlValue2Hash(hash, root, "GoalsCurrentTeam")

        ele = root?.getElementsByTagName("Specialty")?.item(0) as Element?
        hash.insert("Specialty", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("TransferListed")?.item(0) as Element?
        hash.insert("TransferListed", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("NationalTeamID")?.item(0) as Element?
        hash.insert("NationalTeamID", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("CountryID")?.item(0) as Element?
        hash.insert("CountryID", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Caps")?.item(0) as Element?
        hash.insert("Caps", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("CapsU20")?.item(0) as Element?
        hash.insert("CapsU20", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("Cards")?.item(0) as Element?
        hash.insert("Cards", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("InjuryLevel")?.item(0) as Element?
        hash.insert("InjuryLevel", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("StaminaSkill")?.item(0) as Element?
        hash.insert("StaminaSkill", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("KeeperSkill")?.item(0) as Element?
        hash.insert("KeeperSkill", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("PlaymakerSkill")?.item(0) as Element?
        hash.insert("PlaymakerSkill", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("ScorerSkill")?.item(0) as Element?
        hash.insert("ScorerSkill", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("PassingSkill")?.item(0) as Element?
        hash.insert("PassingSkill", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("WingerSkill")?.item(0) as Element?
        hash.insert("WingerSkill", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("DefenderSkill")?.item(0) as Element?
        hash.insert("DefenderSkill", (XMLManager.getFirstChildNodeValue(ele)))
        ele = root?.getElementsByTagName("SetPiecesSkill")?.item(0) as Element?
        hash.insert("SetPiecesSkill", (XMLManager.getFirstChildNodeValue(ele)))

        // Coach
        val trainerData:NodeList? = root?.getElementsByTagName("TrainerData")
        if (trainerData != null && trainerData.length > 0) {
            val tmpTrainer:Element? = trainerData.item(0) as Element?
            ele = tmpTrainer?.getElementsByTagName("TrainerType")?.item(0) as Element?
            hash.insert("TrainerType", (XMLManager.getFirstChildNodeValue(ele)))
            ele = tmpTrainer?.getElementsByTagName("TrainerSkill")?.item(0) as Element?
            hash.insert("TrainerSkill", (XMLManager.getFirstChildNodeValue(ele)))
        }

        //LastMatch #461
        try {
            val tmpLastMatch = root?.getElementsByTagName("LastMatch")?.item(0) as Element?
            ele = tmpLastMatch?.getElementsByTagName("Date")?.item(0) as Element?
            hash.insert("LastMatch_Date", (XMLManager.getFirstChildNodeValue(ele)))
            ele = tmpLastMatch?.getElementsByTagName("Rating")?.item(0) as Element?
            hash.insert("LastMatch_Rating", (XMLManager.getFirstChildNodeValue(ele)))

            val lastMatchId = XMLManager.xmlIntValue(tmpLastMatch, "MatchId", -1)
            hash.insert("LastMatch_id", lastMatchId.toString())

            // Retrieve MatchType of last match by its ID.
            val matchInfo = DBManager.getLastMatchWithMatchId(lastMatchId)
            if (matchInfo != null) {
                hash.insert("LastMatch_Type", matchInfo.getMatchType().id.toString())
            }

            ele = tmpLastMatch?.getElementsByTagName("PositionCode")?.item(0) as Element?
            hash.insert("LastMatch_PositionCode", (XMLManager.getFirstChildNodeValue(ele)))
            ele = tmpLastMatch?.getElementsByTagName("PlayedMinutes")?.item(0) as Element?
            hash.insert("LastMatch_PlayedMinutes", (XMLManager.getFirstChildNodeValue(ele)))
            ele = tmpLastMatch?.getElementsByTagName("RatingEndOfGame")?.item(0) as Element?
            hash.insert("LastMatch_RatingEndOfGame", (XMLManager.getFirstChildNodeValue(ele)))

        } catch (e: Exception) {
            HOLogger.instance().error(XMLPlayersParser.javaClass, "Error parsing player entry: ${e.message}")
        }

        return hash
    }

    fun parseYouthPlayersFromString(inputStream: String): List<SafeInsertMap>  {
        val doc:Document? = XMLManager.parseString(inputStream)
        return createYouthPlayerList(doc)
    }

    private fun createYouthPlayerList(doc: Document?): List<SafeInsertMap> {
        val ret = Vector<SafeInsertMap>()

        try {
            var root: Element? = doc?.documentElement
            root = root?.getElementsByTagName("PlayerList")?.item(0) as Element?

            // <YouthPlayer>
            val list: NodeList? = root?.getElementsByTagName("YouthPlayer")

            if (list != null) {
                for (i in 0..<list.length) {
                    val hash = SafeInsertMap()
                    root = list.item(i) as Element?

                    //<YouthPlayerID>264643130</YouthPlayerID>
                    XMLManager.xmlValue2Hash(hash, root, "YouthPlayerID")
                    //<FirstName>Claudio</FirstName>
                    XMLManager.xmlValue2Hash(hash, root, "FirstName")
                    //      <NickName />
                    XMLManager.xmlValue2Hash(hash, root, "NickName")
                    //      <LastName>Dattenfeld</LastName>
                    XMLManager.xmlValue2Hash(hash, root, "LastName")
                    //      <Age>18</Age>
                    XMLManager.xmlValue2Hash(hash, root, "Age")
                    //      <AgeDays>50</AgeDays>
                    XMLManager.xmlValue2Hash(hash, root, "AgeDays")
                    //      <ArrivalDate>2020-04-18 09:11:00</ArrivalDate>
                    XMLManager.xmlValue2Hash(hash, root, "ArrivalDate")
                    //      <CanBePromotedIn>-85</CanBePromotedIn>
                    XMLManager.xmlValue2Hash(hash, root, "CanBePromotedIn")
                    //      <PlayerNumber>100</PlayerNumber>
                    XMLManager.xmlValue2Hash(hash, root, "PlayerNumber")
                    //      <Statement />
                    XMLManager.xmlValue2Hash(hash, root, "Statement")
                    //      <OwnerNotes />
                    XMLManager.xmlValue2Hash(hash, root, "OwnerNotes")
                    //      <PlayerCategoryID>0</PlayerCategoryID>
                    XMLManager.xmlValue2Hash(hash, root, "PlayerCategoryID")
                    //      <Cards>0</Cards>
                    XMLManager.xmlValue2Hash(hash, root, "Cards")
                    //      <InjuryLevel>-1</InjuryLevel>
                    XMLManager.xmlValue2Hash(hash, root, "InjuryLevel")
                    //      <Specialty>3</Specialty>
                    XMLManager.xmlValue2Hash(hash, root, "Specialty")
                    //      <CareerGoals>3</CareerGoals>
                    XMLManager.xmlValue2Hash(hash, root, "CareerGoals")
                    //      <CareerHattricks>0</CareerHattricks>
                    XMLManager.xmlValue2Hash(hash, root, "CareerHattricks")
                    //      <LeagueGoals>0</LeagueGoals>
                    XMLManager.xmlValue2Hash(hash, root, "LeagueGoals")
                    //      <FriendlyGoals>1</FriendlyGoals>
                    XMLManager.xmlValue2Hash(hash, root, "FriendlyGoals")
                    //      <OwningYouthTeam>
                    //        <YouthTeamID>2325763</YouthTeamID>
                    //        <YouthTeamName>Brenk Street Boys 2</YouthTeamName>
                    //        <YouthTeamLeagueID>685791</YouthTeamLeagueID>
                    //        <SeniorTeam>
                    //          <SeniorTeamID>1242154</SeniorTeamID>
                    //          <SeniorTeamName>Juventus Brenk 2</SeniorTeamName>
                    //        </SeniorTeam>
                    //      </OwningYouthTeam>
                    //      <PlayerSkills>
                    val playerSkills: Element? = root?.getElementsByTagName("PlayerSkills")?.item(0) as Element?
                    //        <KeeperSkill IsAvailable="False" IsMaxReached="False" MayUnlock="False" />
                    //        <KeeperSkillMax IsAvailable="True">2</KeeperSkillMax>

                    if (playerSkills != null) {
                        for (skillId in YouthPlayer.skillIds) {
                            youthplayerSkills2Hash(hash, playerSkills, skillId)
                        }
                    }

                    //      </PlayerSkills>
                    //      <ScoutCall>
                    val scoutCall: Element? = root?.getElementsByTagName("ScoutCall")?.item(0) as Element?
                    //        <Scout>
                    val scout: Element? = scoutCall?.getElementsByTagName("Scout")?.item(0) as Element?
                    //          <ScoutId>382876</ScoutId>
                    XMLManager.xmlValue2Hash(hash, scout, "ScoutId")
                        //          <ScoutName>Joerg Hopfen</ScoutName>
                    XMLManager.xmlValue2Hash(hash, scout, "ScoutName")

                    //        </Scout>
                    //        <ScoutingRegionID>229</ScoutingRegionID>
                    XMLManager.xmlValue2Hash(hash, scoutCall, "ScoutingRegionID")

                    //        <ScoutComments>
                    val scoutComments = scoutCall?.getElementsByTagName("ScoutComments")?.item(0) as Element?
                    //          <ScoutComment>
                    val scoutCommentsList = scoutComments?.getElementsByTagName("ScoutComment")
                    if (scoutCommentsList != null) {
                        for (c in 0..<scoutCommentsList.length) {
                            val scoutComment = scoutCommentsList.item(c) as Element?
                            val prefix = "ScoutComment$c"
                            //            <CommentText>Wir haben einen aussichtsreichen Kandidaten zu beurteilen. Er trägt den Namen Claudio Dattenfeld und ist 16 Jahre alt.</CommentText>
                            if (scoutComment != null) {
                                XMLManager.xmlValue2Hash(hash, scoutComment, "CommentText", prefix + "Text")
                                //            <CommentType>1</CommentType>
                                XMLManager.xmlValue2Hash(hash, scoutComment, "CommentType", prefix + "Type")
                                //            <CommentVariation>1</CommentVariation>
                                XMLManager.xmlValue2Hash(
                                    hash,
                                    scoutComment,
                                    "CommentVariation",
                                    prefix + "Variation"
                                )
                                //            <CommentSkillType>264643130</CommentSkillType>
                                XMLManager.xmlValue2Hash(
                                    hash,
                                    scoutComment,
                                    "CommentSkillType",
                                    prefix + "SkillType"
                                )
                                //            <CommentSkillLevel>16</CommentSkillLevel>
                                XMLManager.xmlValue2Hash(
                                    hash,
                                    scoutComment,
                                    "CommentSkillLevel",
                                    prefix + "SkillLevel"
                                )
                            }
                            //          </ScoutComment>
                        }
                    }
                    //        </ScoutComments>
                    //      </ScoutCall>
                    val lastMatch = root?.getElementsByTagName("LastMatch")?.item(0) as Element?
                    //      <LastMatch>
                    //        <YouthMatchID>116841872</YouthMatchID>
                    if (lastMatch != null) {
                        XMLManager.xmlValue2Hash(hash, lastMatch, "YouthMatchID")
                        //        <Date>2020-10-29 04:10:00</Date>
                        XMLManager.xmlValue2Hash(hash, lastMatch, "Date", "YouthMatchDate")
                        //        <PositionCode>105</PositionCode>
                        XMLManager.xmlValue2Hash(hash, lastMatch, "PositionCode")
                        //        <PlayedMinutes>90</PlayedMinutes>
                        XMLManager.xmlValue2Hash(hash, lastMatch, "PlayedMinutes")
                        //        <Rating>5</Rating>
                        XMLManager.xmlValue2Hash(hash, lastMatch, "Rating")
                    }
                    //      </LastMatch>
                    //    </YouthPlayer>
                    ret.add(hash)
                }
            }
        } catch (ignored: Exception) {
        }

        return ret
    }

    fun youthplayerSkills2Hash(hash: SafeInsertMap, playerSkills: Element, skillId: Skills.HTSkillID) {
        //        <KeeperSkill IsAvailable="False" IsMaxReached="False" MayUnlock="False" />
        var attr = skillId.toString() + "Skill"
        XMLManager.xmlValue2Hash(hash, playerSkills, attr)
        XMLManager.xmlAttribute2Hash(hash, playerSkills, attr, "IsAvailable")
        XMLManager.xmlAttribute2Hash(hash, playerSkills, attr, "IsMaxReached")
        XMLManager.xmlAttribute2Hash(hash, playerSkills, attr, "MayUnlock")
        //        <KeeperSkillMax IsAvailable="True">2</KeeperSkillMax>
        attr += "Max"
        XMLManager.xmlValue2Hash(hash, playerSkills, attr)
        XMLManager.xmlAttribute2Hash(hash, playerSkills, attr, "IsAvailable")
        XMLManager.xmlAttribute2Hash(hash, playerSkills, attr, "MayUnlock")
    }

    // TODO refactor, use createPlayerDetails
    fun parsePlayerDetailsFromString(xml: String): Player? {
        val doc: Document? = XMLManager.parseString(xml)

        var root:Element? = doc?.documentElement
        root = root?.getElementsByTagName("Player")?.item(0) as Element?

        val owningTeam = root?.getElementsByTagName("OwningTeam")?.item(0) as Element?
        val teamID = XMLManager.xmlIntValue(owningTeam, "TeamID", -1)
        if (!UserManager.getCurrentUser().isNtTeam && teamID != HOVerwaltung.instance().model.getBasics().teamId
        ) return null // foreign player

        val player = Player()
        player.playerID = XMLManager.xmlIntValue(root, "PlayerID")
        player.firstName = XMLManager.xmlValue(root, "FirstName")
        player.nickName = XMLManager.xmlValue(root, "NickName")
        player.lastName = XMLManager.xmlValue(root, "LastName")
        player.setShirtNumber(XMLManager.xmlIntValue(root, "PlayerNumber"))
        player.playerCategory = PlayerCategory.valueOf(XMLManager.xmlIntValue(root, "PlayerCategoryID", -1))
        player.ownerNotes = XMLManager.xmlValue(root, "OwnerNotes")
        player.setAge(XMLManager.xmlIntValue(root, "Age"))
        player.setAgeDays(XMLManager.xmlIntValue(root, "AgeDays"))

//        NextBirthDay : DateTime
//        The aproximate Date/time of next birthday.

        player.arrivalDate = XMLManager.xmlValue(root, "ArrivalDate")
        player.form = XMLManager.xmlIntValue(root, "PlayerForm")
        player.setGelbeKarten(XMLManager.xmlIntValue(root, "Cards"))
        player.injuryWeeks = XMLManager.xmlIntValue(root, "InjuryLevel")
        player.playerStatement = XMLManager.xmlValue(root, "Statement")

//        PlayerLanguage : String
//        This container and its elements is only shown if the user has supporter
//        The language the player speaks in his Statement, if existing.
//                PlayerLanguageID : unsigned Integer
//        This container and its elements is only shown if the user has supporter
//        The languageID the player speaks in his Statement, if existing.

        player.ansehen = XMLManager.xmlIntValue(root, "Agreeability")
        player.agressivitaet = XMLManager.xmlIntValue(root, "Aggressiveness")
        player.charakter = XMLManager.xmlIntValue(root, "Honesty")
        player.experience = XMLManager.xmlIntValue(root, "Experience")
        player.loyalty = XMLManager.xmlIntValue(root, "Loyalty")
        player.isHomeGrown = XMLManager.xmlBoolValue(root, "MotherClubBonus")
        player.leadership = XMLManager.xmlIntValue(root, "Leadership")
        player.playerSpecialty = XMLManager.xmlIntValue(root, "Specialty")
        player.nationalityAsInt = XMLManager.xmlIntValue(root, "NativeCountryID")

//                NativeLeagueID : unsigned Integer
//        LeagueID of the league where the player was born.
//                NativeLeagueName : String
//        LeagueName of the league where the player was born.

        player.tsi = XMLManager.xmlIntValue(root, "TSI")

//        -OwningTeam
//        Container for the team that owns the player.
//        TeamID : unsigned Integer
//        The globally unique TeamID of the team owning the player.
//        TeamName : String
//        The full team name of the team owning the player.
//        LeagueID : unsigned Integer
//        LeagueID for the league of the team owning the player.

        player.setGehalt(XMLManager.xmlIntValue(root, "Salary"))

//        IsAbroad : Boolean*
//                The abroad status of the player, given as an integer. true for beeing in home country, false for beeing in a team abroad. The container is empty If the player has no owner.

        player.laenderspiele = XMLManager.xmlIntValue(root, "Caps")
        player.u20Laenderspiele = XMLManager.xmlIntValue(root, "CapsU20")
        player.allOfficialGoals = XMLManager.xmlIntValue(root, "CareerGoals")
        player.hattrick = XMLManager.xmlIntValue(root, "CareerHattricks")
        player.matchesCurrentTeam = XMLManager.xmlIntegerValue(root, "MatchesCurrentTeam")
        player.goalsCurrentTeam = XMLManager.xmlIntValue(root, "GoalsCurrentTeam")
        player.setNationalTeamId(XMLManager.xmlIntegerValue(root, "NationalTeamID"))

//        NationalTeamName : String*
//                If the player is enrolled on a national team, this is that national team's name.
        val transferListed:Int = if (XMLManager.xmlBoolValue(root, "TransferListed")) 1 else 0
        player.transferlisted = transferListed

        var ele = root?.getElementsByTagName("TrainerData")?.item(0) as Element?
        if (ele != null) {
            val trainerType: TrainerType = TrainerType.valueOf(XMLManager.xmlValue(ele, "TrainerType"))
            player.trainerTyp = trainerType
            val trainerSkill = XMLManager.xmlIntValue(root, "TrainerSkill", 0)
            player.trainerSkill = trainerSkill
        }

        ele = root?.getElementsByTagName("MotherClub")?.item(0) as Element?
        if (ele != null) {
            player.setMotherClubId(XMLManager.xmlIntegerValue(ele, "TeamID"))
            player.setMotherClubName(XMLManager.xmlValue(ele, "TeamName"))
        }

        ele = root?.getElementsByTagName("PlayerSkills")?.item(0) as Element?
        if (ele != null) {
            player.setStamina(XMLManager.xmlIntValue(ele, "StaminaSkill"))
            player.setTorwart(XMLManager.xmlIntValue(ele, "KeeperSkill"))
            player.setSpielaufbau(XMLManager.xmlIntValue(ele, "PlaymakerSkill"))
            player.setTorschuss(XMLManager.xmlIntValue(ele, "ScorerSkill"))
            player.setPasspiel(XMLManager.xmlIntValue(ele, "PassingSkill"))
            player.setFluegelspiel(XMLManager.xmlIntValue(ele, "WingerSkill"))
            player.setVerteidigung(XMLManager.xmlIntValue(ele, "DefenderSkill"))
            player.setStandards(XMLManager.xmlIntValue(ele, "SetPiecesSkill"))
        }

        return player

    }
}
