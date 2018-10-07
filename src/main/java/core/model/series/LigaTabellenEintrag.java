package core.model.series;

public class LigaTabellenEintrag  implements Comparable<LigaTabellenEintrag>{
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
    public LigaTabellenEintrag() {
        for (int i = 0; i < m_aSerie.length; i++) {
            m_aSerie[i] = UNKOWN;
        }
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
     * Getter for property m_iA_Nied.
     *
     * @return Value of property m_iA_Nied.
     */
    public final int getA_Nied() {
        return m_iA_Nied;
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

    /////////////////////////////////////////////////////////////////////////////////
    // EXTENDED MEMBER Accessor    
    ////////////////////////////////////////////////////////////////////////////////7

    /**
     * Getter for property m_iA_Siege.
     *
     * @return Value of property m_iA_Siege.
     */
    public final int getA_Siege() {
        return m_iA_Siege;
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
     * Getter for property m_iA_ToreGegen.
     *
     * @return Value of property m_iA_ToreGegen.
     */
    public final int getA_ToreGegen() {
        return m_iA_ToreGegen;
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
     * Getter for property m_iA_Un.
     *
     * @return Value of property m_iA_Un.
     */
    public final int getA_Un() {
        return m_iA_Un;
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
    public final int getAltePosition() {
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

    public final int getAwayTorDiff() {
        return (m_iA_ToreFuer - m_iA_ToreGegen);
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
    public final int getGesamtTorDiff() {
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
     * Getter for property m_iH_Nied.
     *
     * @return Value of property m_iH_Nied.
     */
    public final int getH_Nied() {
        return m_iH_Nied;
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
     * Getter for property m_iH_Punkte.
     *
     * @return Value of property m_iH_Punkte.
     */
    public final int getH_Punkte() {
        return m_iH_Punkte;
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
     * Getter for property m_iH_Siege.
     *
     * @return Value of property m_iH_Siege.
     */
    public final int getH_Siege() {
        return m_iH_Siege;
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
     * Getter for property m_iH_ToreFuer.
     *
     * @return Value of property m_iH_ToreFuer.
     */
    public final int getH_ToreFuer() {
        return m_iH_ToreFuer;
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
     * Getter for property m_iH_ToreGegen.
     *
     * @return Value of property m_iH_ToreGegen.
     */
    public final int getH_ToreGegen() {
        return m_iH_ToreGegen;
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
     * Getter for property m_iH_Un.
     *
     * @return Value of property m_iH_Un.
     */
    public final int getH_Un() {
        return m_iH_Un;
    }

    public final int getHeimTorDiff() {
        return (m_iH_ToreFuer - m_iH_ToreGegen);
    }

    /**
     * liefert die letzen XXX Spiele als Serie
     *
     * @param anzahl wie viele der letzen Spiele sollen angezeigt werden,  -1 = alle, 0 = nur das
     *        akuelle, 1-x = anzahl Spiele vor dem aktuellen
     */
    public final byte[] getLastSerie(int anzahl) {
        byte[] miniSerie = new byte[0];
        int start = -1;
        final int ende = m_iAnzSpiele;

        // Bereich vorbereiten
        if (anzahl >= 0) {
            if (m_iAnzSpiele > anzahl) {
                //-1 weil index eines Spieltages = ( Spieltag - 1 ) ist
                start = (m_iAnzSpiele - 1) - anzahl;
            } else {
                start = 0;
            }

            //miniSerie vorbereiten
            miniSerie = new byte[ende - start];
        }

        for (int i = start; i < ende; i++) {
            //- start damit bei 0 angefangen wird, hier nur i da bereits auf array abgestimmt ist!
            miniSerie[i - start] = m_aSerie[i];
        }

        //nix zurückgeben
        return miniSerie;
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
    public final int getPunkte() {
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

    public final String getSerieAsString() {
        final StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < m_aSerie.length; i++) {
            switch (m_aSerie[i]) {
                case H_SIEG:
                    buffer.append(core.model.HOVerwaltung.instance().getLanguageString("SerieHeimSieg"));
                    break;

                case H_UN:
                    buffer.append(core.model.HOVerwaltung.instance().getLanguageString("SerieHeimUnendschieden"));
                    break;

                case H_NIED:
                    buffer.append(core.model.HOVerwaltung.instance().getLanguageString("SerieHeimNiederlage"));
                    break;

                case A_SIEG:
                    buffer.append(core.model.HOVerwaltung.instance().getLanguageString("SerieAuswaertsSieg"));
                    break;

                case A_UN:
                    buffer.append(core.model.HOVerwaltung.instance().getLanguageString("SerieAuswaertsUnendschieden"));
                    break;

                case A_NIED:
                    buffer.append(core.model.HOVerwaltung.instance().getLanguageString("SerieAuswaertsNiederlage"));
                    break;

                default:

                    //nix
                    break;
            }
        }

        return buffer.toString();
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
    public final void setTeamName(java.lang.String m_sTeamName) {
        this.m_sTeamName = m_sTeamName;
    }

    /**
     * Getter for property m_sTeamName.
     *
     * @return Value of property m_sTeamName.
     */
    public final java.lang.String getTeamName() {
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
    public final int getToreFuer() {
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
    public final int getToreGegen() {
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
     * vergleicht die Einträge
     */
    public final int compareTo(LigaTabellenEintrag obj) {
        if (obj instanceof LigaTabellenEintrag) {
            final LigaTabellenEintrag lte = (LigaTabellenEintrag) obj;

            if (m_iPunkte > lte.getPunkte()) {
                return -1;
            } else if (m_iPunkte < lte.getPunkte()) {
                return 1;
            } else if (m_iPunkte == lte.getPunkte()) {
                if (getGesamtTorDiff() > lte.getGesamtTorDiff()) {
                    return -1;
                } else if (getGesamtTorDiff() < lte.getGesamtTorDiff()) {
                    return 1;
                } else if (getGesamtTorDiff() == lte.getGesamtTorDiff()) {
                    if (m_iToreFuer > lte.getToreFuer()) {
                        return -1;
                    } else if (m_iToreFuer < lte.getToreFuer()) {
                        return 1;
                    }
                    /*else if ( m_iToreFuer == lte.getToreFuer () )
                       {
                           return 0;
                       }*/

                    //nun gilt der Auswärtsfaktor
                    else if (getA_Punkte() > lte.getA_Punkte()) {
                        return -1;
                    } else if (getA_Punkte() < lte.getA_Punkte()) {
                        return 1;
                    } else if (getA_Punkte() == lte.getA_Punkte()) {
                        if (m_iA_ToreFuer > lte.getA_ToreFuer()) {
                            return -1;
                        } else if (m_iA_ToreFuer < lte.getA_ToreFuer()) {
                            return 1;
                        }

                        /* else if ( m_iA_ToreFuer  == lte.getA_ToreFuer () )
                           {
                               return 0;
                           }*/
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
        LigaTabellenEintrag lte = null;

        if (obj instanceof LigaTabellenEintrag) {
            lte = (LigaTabellenEintrag) obj;

            if ((lte.getAnzSpiele() == m_iAnzSpiele)
                && (lte.getPosition() == m_iPosition)
                && (lte.getPunkte() == m_iPunkte)
                && (lte.getTeamName().equals(m_sTeamName))
                && (lte.getToreFuer() == m_iToreFuer)
                && (lte.getToreGegen() == m_iToreGegen)) {
                return true;
            }
        }

        return false;
    }
}
