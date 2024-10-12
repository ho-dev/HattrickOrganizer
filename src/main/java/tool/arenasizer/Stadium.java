package tool.arenasizer;

import core.db.AbstractTable;
import core.util.HODateTime;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Optional;
import java.util.Properties;

@Setter
@Getter
public class Stadium extends AbstractTable.Storable {

    /**
     * HRF-ID
     */
    private int hrfId;

    /**
     * Arena-ID
     */
    private int arenaId;

    /**
     * Arena-Name
     */
    private String stadienname = "";

    /**
     * Terraces
     */
    private int stehplaetze;

    /**
     * Basic Seating
     */
    private int sitzplaetze;

    /**
     * Seats under Roof
     */
    private int ueberdachteSitzplaetze;

    /**
     * VIP Boxes
     */
    private int logen;

    /**
     * Expansion?
     */
    private boolean ausbau;

    /**
     * Expansion of Terraces
     */
    private int ausbauStehplaetze;

    /**
     * Expansion of Basic Seating
     */
    private int ausbauSitzplaetze;

    /**
     * Expansion of Seats under Roof
     */
    private int ausbauUeberdachteSitzplaetze;

    /**
     * Expansion of VIP Boxes
     */
    private int ausbauLogen;

    /**
     * Cost of Expansion
     */
    private int ausbauKosten;

    /**
     * Rebuilt date
     */
    @Setter
    @Getter
    private HODateTime rebuiltDate;

    /**
     * Expansion date
     */
    @Setter
    @Getter
    private HODateTime expansionDate;

    public Stadium() {
    }

    public Stadium(Properties properties) {
        // 'seattotal' and 'expandingSseatTotal' are currently not read
        arenaId = NumberUtils.toInt(properties.getProperty("arenaid"), 0);
        stadienname = properties.getProperty("arenaname", "");
        stehplaetze = NumberUtils.toInt(properties.getProperty("antalstaplats"), 0);
        sitzplaetze = NumberUtils.toInt(properties.getProperty("antalsitt"), 0);
        ueberdachteSitzplaetze = NumberUtils.toInt(properties.getProperty("antaltak"), 0);
        logen = NumberUtils.toInt(properties.getProperty("antalvip"), 0);
        ausbauStehplaetze = NumberUtils.toInt(properties.getProperty("expandingstaplats"), 0);
        ausbauSitzplaetze = NumberUtils.toInt(properties.getProperty("expandingsitt"), 0);
        ausbauUeberdachteSitzplaetze = NumberUtils.toInt(properties.getProperty("expandingtak"), 0);
        ausbauLogen = NumberUtils.toInt(properties.getProperty("expandingvip"), 0);
        ausbau = NumberUtils.toInt(properties.getProperty("isexpanding"), 0) > 0;
        if (ausbau) {
            ausbauKosten = NumberUtils.toInt(properties.getProperty("expandcost"), 0);
        }
        rebuiltDate = HODateTime.fromHT(properties.getProperty("rebuiltdate"));
        expansionDate = HODateTime.fromHT(properties.getProperty("expansiondate"));
    }

    public int getGesamtgroesse() {
        return getStehplaetze() + getSitzplaetze() + getUeberdachteSitzplaetze() + getLogen();
    }

    public Optional<Integer> getAusbauGesamtgroesse() {
        return isAusbau() ?
                Optional.of(getAusbauStehplaetze() + getAusbauSitzplaetze() + getAusbauUeberdachteSitzplaetze() + getAusbauLogen()) :
                Optional.empty();
    }

    public Optional<Integer> getZukunftGesamtgroesse() {
        return getAusbauGesamtgroesse().map(ausbauGesamtgroesse -> ausbauGesamtgroesse + getGesamtgroesse());
    }

    public Optional<Integer> getZukunftStehplaetze() {
        return isAusbau() ? Optional.of(getStehplaetze() + getAusbauStehplaetze()) : Optional.empty();
    }

    public Optional<Integer> getZukunftSitzplaetze() {
        return isAusbau() ? Optional.of(getSitzplaetze() + getAusbauSitzplaetze()) : Optional.empty();
    }

    public Optional<Integer> getZukunftUeberdachteSitzplaetze() {
        return isAusbau() ? Optional.of(getUeberdachteSitzplaetze() + getAusbauUeberdachteSitzplaetze()) : Optional.empty();
    }

    public Optional<Integer> getZukunftLogen() {
        return isAusbau() ? Optional.of(getLogen() + getAusbauLogen()) : Optional.empty();
    }
}
