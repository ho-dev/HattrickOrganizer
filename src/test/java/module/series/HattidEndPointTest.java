package module.series;


import module.series.statistics.DataDownloader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HattidEndPointTest {

    @Test
    public void testEndPointPowerRatings() {

        var res = DataDownloader.instance().fetchLeagueTeamPowerRatings(21641, 14, 76);

        Assertions.assertEquals(res.get(294211), 1040); // Châteauneuvais d'Alleray
        Assertions.assertEquals(res.get(1166365), 968); // RC Boulogne Sur Mer
        Assertions.assertEquals(res.get(294232), 832); // grostaz team

    }


}
