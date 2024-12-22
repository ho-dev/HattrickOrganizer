package core.util;

import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.XtraData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HOCurrencyTests {

    @Test
    void test() {
        // Prepare model
        var hov = HOVerwaltung.instance();
        hov.setModel(new HOModel(1));
        hov.getModel().setXtraDaten(new XtraData());
        hov.getModel().getXtraDaten().setCountryId(3);
        UserParameter.instance().FXrate = 10;

        var c = new HOCurrency(10);
        Assertions.assertEquals(0, c.toLocale());

        var e = new HOCurrency(50);
        Assertions.assertEquals(10, e.toLocale());

        var d = new HOCurrency(c.getSwedishKrona()+90);
        var nbsp = "\u00A0";
        Assertions.assertEquals("10" + nbsp + "€", d.toLocaleString());

    }

}
