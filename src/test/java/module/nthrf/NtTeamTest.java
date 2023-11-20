package module.nthrf;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class NtTeamTest {


    @Test
    public void testParseXml() {


        var xml = """
                <?xml version="1.0" encoding="utf-8"?>
                <HattrickData>
                  <FileName>nationalTeamDetails.xml</FileName>
                  <Version>1.9</Version>
                  <UserID>1757369</UserID>
                  <FetchedDate>2022-01-08 14:33:58</FetchedDate>
                  <UserSupporterTier>platinum</UserSupporterTier>
                  <IsPlayingMatch>False</IsPlayingMatch>
                  <Team>
                    <TeamID>3002</TeamID>
                    <TeamName>Deutschland</TeamName>
                    <ShortTeamName>Deutschland</ShortTeamName>
                    <NationalCoach>
                      <NationalCoachUserID>213185</NationalCoachUserID>
                      <NationalCoachLoginname>LA-Graf_Ete</NationalCoachLoginname>
                    </NationalCoach>
                    <League>
                      <LeagueID>3</LeagueID>
                      <LeagueName>Deutschland</LeagueName>
                    </League>
                    <HomePage>http://www.hattrickportal.pro/Tracker</HomePage>
                    <Logo>//res.hattrick.org/ntteamlogo/1/1/4/3002/3002.png</Logo>
                    <DressURI>//res.hattrick.org/kits/27/267/2663/2662506/matchKitSmall.png</DressURI>
                    <DressAlternateURI>//res.hattrick.org/kits/27/266/2652/2651379/matchKitSmall.png</DressAlternateURI>
                    <Experience433>3</Experience433>
                    <Experience451>8</Experience451>
                    <Experience352>10</Experience352>
                    <Experience532>8</Experience532>
                    <Experience343>8</Experience343>
                    <Experience541>8</Experience541>
                    <Experience523>3</Experience523>
                    <Experience550>3</Experience550>
                    <Experience253>9</Experience253>
                    <Experience442>8</Experience442>
                    <Morale>9</Morale>
                    <SelfConfidence>6</SelfConfidence>
                    <SupportersPopularity>7</SupportersPopularity>
                    <RatingScore>22761</RatingScore>
                    <FanClubSize>2224</FanClubSize>
                    <Rank>7</Rank>
                  </Team>
                </HattrickData>""";

        var ntteam = new NtTeamDetails();
        ntteam.parseDetails(xml);

        Assertions.assertEquals(9, ntteam.getMorale());
        Assertions.assertEquals(6, ntteam.getSelfConfidence());
    }


}