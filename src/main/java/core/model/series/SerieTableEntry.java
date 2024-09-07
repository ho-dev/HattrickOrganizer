package core.model.series;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SerieTableEntry implements Comparable<SerieTableEntry> {
    //~ Instance fields ----------------------------------------------------------------------------
    public static final byte H_SIEG = 1;
    public static final byte A_SIEG = 2;
    /** HOME DRAW */
    public static final byte H_UN = 3;
    public static final byte A_UN = 4;
    /** Home Loose */
    public static final byte H_NIED = 5;
    public static final byte A_NIED = 6;
    public static final byte UNKOWN = 0;
    protected String m_sTeamName = "";
    protected byte[] m_aSerie = new byte[14];
    protected int m_iA_Nied = -1;
    protected int m_iA_Punkte = -1;
    protected int m_iA_Siege = -1;
    protected int m_iA_ToreFuer = -1;
    protected int m_iA_ToreGegen = -1;
    protected int m_iA_Un = -1;
    protected int m_iAltePosition = 1;
    protected int m_iAnzSpiele = -1;
    protected int m_iG_Nied = -1;
    protected int m_iG_Siege = -1;
    protected int m_iG_Un = -1;
    protected int m_iH_Nied = -1;
    protected int m_iH_Punkte = -1;
    protected int m_iH_Siege = -1;
    protected int m_iH_ToreFuer = -1;
    protected int m_iH_ToreGegen = -1;
    protected int m_iH_Un = -1;

    ///////////MEMBER////////////////////////
    protected int m_iPosition = -1;
    protected int m_iPunkte = -1;
    protected int m_iTeamId = -1;
    protected int m_iToreFuer = -1;
    protected int m_iToreGegen = -1;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new instance of LigaTabellenEintrag
     */
    public SerieTableEntry() {
        Arrays.fill(m_aSerie, UNKOWN);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_iA_Nied.
     *
     * @param m_iA_Nied New value of property m_iA_Nied.
     */
    public final void setA_Nied(int m_iA_Nied) {
        this.m_iA_Nied = m_iA_Nied;
    }

    /**
     * Setter for property m_iA_Punkte.
     *
     * @param m_iA_Punkte New value of property m_iA_Punkte.
     */
    public final void setA_Punkte(int m_iA_Punkte) {
        this.m_iA_Punkte = m_iA_Punkte;
    }

    /**
     * Getter for property m_iA_Punkte.
     *
     * @return Value of property m_iA_Punkte.
     */
    public final int getA_Punkte() {
        return m_iA_Punkte;
    }

    /**
     * Setter for property m_iA_Siege.
     *
     * @param m_iA_Siege New value of property m_iA_Siege.
     */
    public final void setA_Siege(int m_iA_Siege) {
        this.m_iA_Siege = m_iA_Siege;
    }

    /**
     * Setter for property m_iA_ToreFuer.
     *
     * @param m_iA_ToreFuer New value of property m_iA_ToreFuer.
     */
    public final void setA_ToreFuer(int m_iA_ToreFuer) {
        this.m_iA_ToreFuer = m_iA_ToreFuer;
    }

    /**
     * Getter for property m_iA_ToreFuer.
     *
     * @return Value of property m_iA_ToreFuer.
     */
    public final int getA_ToreFuer() {
        return m_iA_ToreFuer;
    }

    /**
     * Setter for property m_iA_ToreGegen.
     *
     * @param m_iA_ToreGegen New value of property m_iA_ToreGegen.
     */
    public final void setA_ToreGegen(int m_iA_ToreGegen) {
        this.m_iA_ToreGegen = m_iA_ToreGegen;
    }

    /**
     * Setter for property m_iA_Un.
     *
     * @param m_iA_Un New value of property m_iA_Un.
     */
    public final void setA_Un(int m_iA_Un) {
        this.m_iA_Un = m_iA_Un;
    }

    /**
     * Setter for property m_iAltePosition.
     *
     * @param m_iAltePosition New value of property m_iAltePosition.
     */
    public final void setAltePosition(int m_iAltePosition) {
        this.m_iAltePosition = m_iAltePosition;
    }

    /**
     * Getter for property m_iAltePosition.
     *
     * @return Value of property m_iAltePosition.
     */
    public final int getPreviousPosition() {
        return m_iAltePosition;
    }

    /**
     * Setter for property m_iAnzSpiele.
     *
     * @param m_iAnzSpiele New value of property m_iAnzSpiele.
     */
    public final void setAnzSpiele(int m_iAnzSpiele) {
        this.m_iAnzSpiele = m_iAnzSpiele;
    }

    /**
     * Getter for property m_iAnzSpiele.
     *
     * @return Value of property m_iAnzSpiele.
     */
    public final int getAnzSpiele() {
        return m_iAnzSpiele;
    }

    /**
     * Setter for property m_iG_Nied.
     *
     * @param m_iG_Nied New value of property m_iG_Nied.
     */
    public final void setG_Nied(int m_iG_Nied) {
        this.m_iG_Nied = m_iG_Nied;
    }

    /**
     * Getter for property m_iG_Nied.
     *
     * @return Value of property m_iG_Nied.
     */
    public final int getG_Nied() {
        return m_iG_Nied;
    }

    /**
     * Setter for property m_iG_Siege.
     *
     * @param m_iG_Siege New value of property m_iG_Siege.
     */
    public final void setG_Siege(int m_iG_Siege) {
        this.m_iG_Siege = m_iG_Siege;
    }

    /**
     * Getter for property m_iG_Siege.
     *
     * @return Value of property m_iG_Siege.
     */
    public final int getG_Siege() {
        return m_iG_Siege;
    }

    /**
     * Setter for property m_iG_Un.
     *
     * @param m_iG_Un New value of property m_iG_Un.
     */
    public final void setG_Un(int m_iG_Un) {
        this.m_iG_Un = m_iG_Un;
    }

    /**
     * Getter for property m_iG_Un.
     *
     * @return Value of property m_iG_Un.
     */
    public final int getG_Un() {
        return m_iG_Un;
    }

    /////////////////////////////////////////////////////////////////////////////////
    // EXTENDED Funcs    
    ////////////////////////////////////////////////////////////////////////////////7
    public final int getGoalsDiff() {
        return (m_iToreFuer - m_iToreGegen);
    }

    /**
     * Setter for property m_iH_Nied.
     *
     * @param m_iH_Nied New value of property m_iH_Nied.
     */
    public final void setH_Nied(int m_iH_Nied) {
        this.m_iH_Nied = m_iH_Nied;
    }

    /**
     * Setter for property m_iH_Punkte.
     *
     * @param m_iH_Punkte New value of property m_iH_Punkte.
     */
    public final void setH_Punkte(int m_iH_Punkte) {
        this.m_iH_Punkte = m_iH_Punkte;
    }

    /**
     * Setter for property m_iH_Siege.
     *
     * @param m_iH_Siege New value of property m_iH_Siege.
     */
    public final void setH_Siege(int m_iH_Siege) {
        this.m_iH_Siege = m_iH_Siege;
    }

    /**
     * Setter for property m_iH_ToreFuer.
     *
     * @param m_iH_ToreFuer New value of property m_iH_ToreFuer.
     */
    public final void setH_ToreFuer(int m_iH_ToreFuer) {
        this.m_iH_ToreFuer = m_iH_ToreFuer;
    }

    /**
     * Setter for property m_iH_ToreGegen.
     *
     * @param m_iH_ToreGegen New value of property m_iH_ToreGegen.
     */
    public final void setH_ToreGegen(int m_iH_ToreGegen) {
        this.m_iH_ToreGegen = m_iH_ToreGegen;
    }

    /**
     * Setter for property m_iH_Un.
     *
     * @param m_iH_Un New value of property m_iH_Un.
     */
    public final void setH_Un(int m_iH_Un) {
        this.m_iH_Un = m_iH_Un;
    }

    /**
     * Setter for property m_bPosition.
     *
     * @param position New value of property m_bPosition.
     */
    public final void setPosition(int position) {
        this.m_iPosition = position;
    }

    /**
     * Getter for property m_bPosition.
     *
     * @return Value of property m_bPosition.
     */
    public final int getPosition() {
        return m_iPosition;
    }

    /**
     * Setter for property m_iPunkte.
     *
     * @param m_iPunkte New value of property m_iPunkte.
     */
    public final void setPunkte(int m_iPunkte) {
        this.m_iPunkte = m_iPunkte;
    }

    /**
     * Getter for property m_iPunkte.
     *
     * @return Value of property m_iPunkte.
     */
    public final int getPoints() {
        return m_iPunkte;
    }

    /**
     * Setter for property m_aSerie.
     *
     * @param m_aSerie New value of property m_aSerie.
     */
    public final void setSerie(byte[] m_aSerie) {
        this.m_aSerie = m_aSerie;
    }

    /**
     * Getter for property m_aSerie.
     *
     * @return Value of property m_aSerie.
     */
    public final byte[] getSerie() {
        return this.m_aSerie;
    }

    /**
     * Setter for property m_iTeamId.
     *
     * @param m_iTeamId New value of property m_iTeamId.
     */
    public final void setTeamId(int m_iTeamId) {
        this.m_iTeamId = m_iTeamId;
    }

    /**
     * Getter for property m_iTeamId.
     *
     * @return Value of property m_iTeamId.
     */
    public final int getTeamId() {
        return m_iTeamId;
    }

    /**
     * Setter for property m_sTeamName.
     *
     * @param m_sTeamName New value of property m_sTeamName.
     */
    public final void setTeamName(String m_sTeamName) {
        this.m_sTeamName = m_sTeamName;
    }

    /**
     * Getter for property m_sTeamName.
     *
     * @return Value of property m_sTeamName.
     */
    public final String getTeamName() {
        return m_sTeamName;
    }

    /**
     * Setter for property m_iToreFuer.
     *
     * @param m_iToreFuer New value of property m_iToreFuer.
     */
    public final void setToreFuer(int m_iToreFuer) {
        this.m_iToreFuer = m_iToreFuer;
    }

    /**
     * Getter for property m_iToreFuer.
     *
     * @return Value of property m_iToreFuer.
     */
    public final int getGoalsFor() {
        return m_iToreFuer;
    }

    /**
     * Setter for property m_iToreGegen.
     *
     * @param m_iToreGegen New value of property m_iToreGegen.
     */
    public final void setToreGegen(int m_iToreGegen) {
        this.m_iToreGegen = m_iToreGegen;
    }

    /**
     * Getter for property m_iToreGegen.
     *
     * @return Value of property m_iToreGegen.
     */
    public final int getGoalsAgainst() {
        return m_iToreGegen;
    }

    /**
     * fügt einen Eintrag hinzu
     *
     * @param index = (Spieltag-1)
     * @param serienInfo serienInfo Info
     */
    public final void addSerienEintrag(int index, byte serienInfo) {
        if ((index >= 0) && (index < m_aSerie.length)) {
            m_aSerie[index] = serienInfo;
        }
    }

    /**
     * Compare series table entries
     */
    public final int compareTo(@NotNull SerieTableEntry obj) {

        if (m_iPunkte > obj.getPoints()) {
            return -1;
        } else if (m_iPunkte < obj.getPoints()) {
            return 1;
        } else {
            if (getGoalsDiff() > obj.getGoalsDiff()) {
                return -1;
            } else if (getGoalsDiff() < obj.getGoalsDiff()) {
                return 1;
            } else if (getGoalsDiff() == obj.getGoalsDiff()) {
                if (m_iToreFuer > obj.getGoalsFor()) {
                    return -1;
                } else if (m_iToreFuer < obj.getGoalsFor()) {
                    return 1;
                }
                //nun gilt der Auswärtsfaktor
                else if (getA_Punkte() > obj.getA_Punkte()) {
                    return -1;
                } else if (getA_Punkte() < obj.getA_Punkte()) {
                    return 1;
                } else {
                    if (m_iA_ToreFuer > obj.getA_ToreFuer()) {
                        return -1;
                    } else if (m_iA_ToreFuer < obj.getA_ToreFuer()) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    /////////////////////////////////////////////////////////////////////////////////
    //Overwrite
    ////////////////////////////////////////////////////////////////////////////////7    
    @Override
	public final boolean equals(Object obj) {
        SerieTableEntry lte;

        if (obj instanceof SerieTableEntry) {
            lte = (SerieTableEntry) obj;

            return (lte.getAnzSpiele() == m_iAnzSpiele)
                    && (lte.getPosition() == m_iPosition)
                    && (lte.getPoints() == m_iPunkte)
                    && (lte.getTeamName().equals(m_sTeamName))
                    && (lte.getGoalsFor() == m_iToreFuer)
                    && (lte.getGoalsAgainst() == m_iToreGegen);
        }

        return false;
    }
}
