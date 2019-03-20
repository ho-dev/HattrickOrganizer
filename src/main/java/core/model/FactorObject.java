package core.model;

import core.model.player.IMatchRoleID;
import core.util.HOLogger;

public final class FactorObject {
    //~ Instance fields ----------------------------------------------------------------------------

    /** The position that is described by this FactorObject */
    private byte m_bPosition = IMatchRoleID.UNKNOWN;

    /** Influence of Winger for this position */
    private float m_fFluegelspiel;

    /** Influence of Passing for this position */
    private float m_fPasspiel;

    /** Influence of Playmaking for this position */
    private float m_fSpielaufbau;

    /** Influence of Set Pieces for this position */
    private float m_fStandards;

    /** Influence of Scoring for this position */
    private float m_fTorschuss;

    /** Influence of Goalkeeping for this position */
    private float m_fTorwart;

    /** Influence of Defending for this position */
    private float m_fVerteidigung;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of FactorObject
     */
    public FactorObject(byte position, float torwart, float spielaufbau, float passpiel,
                        float fluegel, float abwehr, float torschuss, float standards) {
        m_fTorschuss = torschuss;
        m_fTorwart = torwart;
        m_fStandards = standards;
        m_fSpielaufbau = spielaufbau;
        m_fPasspiel = passpiel;
        m_fVerteidigung = abwehr;
        m_fFluegelspiel = fluegel;
        m_bPosition = position;
    }

    /**
     * Creates a new FactorObject object.
     */
    public FactorObject(java.sql.ResultSet rs) {
        try {
            if (rs != null) {
                m_fTorschuss = rs.getFloat("Torschuss");
                m_fTorwart = rs.getFloat("Torwart");
                m_fStandards = rs.getFloat("Standards");
                m_fSpielaufbau = rs.getFloat("Spielaufbau");
                m_fPasspiel = rs.getFloat("Passpiel");
                m_fVerteidigung = rs.getFloat("Verteidigung");
                m_fFluegelspiel = rs.getFloat("Fluegel");
                m_bPosition = rs.getByte("HOPosition");
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"Konstruktor Faktor Obj: " + e.toString());
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_fFluegelspiel.
     *
     * @param m_fFluegelspiel New value of property m_fFluegelspiel.
     */
    public final void setFluegelspiel(float m_fFluegelspiel) {
        this.m_fFluegelspiel = m_fFluegelspiel;
    }

    /**
     * Getter for property m_fFluegelspiel.
     *
     * @return Value of property m_fFluegelspiel.
     */
    public final float getFluegelspielScaled(boolean normalized) {
    	if (normalized) {
    		return m_fFluegelspiel/getSum();
    	}
        return m_fFluegelspiel/10.0f;
    }

    /**
     * Setter for property m_fPasspiel.
     *
     * @param m_fPasspiel New value of property m_fPasspiel.
     */
    public final void setPasspiel(float m_fPasspiel) {
        this.m_fPasspiel = m_fPasspiel;
    }

    /**
     * Getter for property m_fPasspiel.
     *
     * @return Value of property m_fPasspiel.
     */
    public final float getPasspielScaled(boolean normalized) {
		if (normalized) {
			return m_fPasspiel/getSum();
		}
        return m_fPasspiel/10.0f;
    }

    /**
     * Setter for property m_bPosition.
     *
     * @param m_bPosition New value of property m_bPosition.
     */
    public final void setPosition(byte m_bPosition) {
        this.m_bPosition = m_bPosition;
    }

    /**
     * Getter for property m_bPosition.
     *
     * @return Value of property m_bPosition.
     */
    public final byte getPosition() {
        return m_bPosition;
    }

    /**
     * Setter for property m_fSpielaufbau.
     *
     * @param m_fSpielaufbau New value of property m_fSpielaufbau.
     */
    public final void setSpielaufbau(float m_fSpielaufbau) {
        this.m_fSpielaufbau = m_fSpielaufbau;
    }

    /**
     * Getter for property m_fSpielaufbau.
     *
     * @return Value of property m_fSpielaufbau.
     */
    public final float getSpielaufbauScaled(boolean normalized) {
		if (normalized) {
			return m_fSpielaufbau/getSum();
		}
        return m_fSpielaufbau/10.0f;
    }


    /**
     * Setter for property m_fStandards.
     *
     * @param m_fStandards New value of property m_fStandards.
     */
    public final void setStandards(float m_fStandards) {
        this.m_fStandards = m_fStandards;
    }

    /**
     * Getter for property m_fStandards.
     *
     * @return Value of property m_fStandards.
     */
    public final float getStandardsScaled(boolean normalized) {
		if (normalized) {
			return m_fStandards/getSum();
		}    	
        return m_fStandards/10.0f;
    }

    //HelperFuncs//////
    public final float getSum() {
        return (m_fTorwart + m_fStandards + m_fTorschuss + m_fVerteidigung + m_fFluegelspiel
               + m_fPasspiel + m_fSpielaufbau);
    }

    /**
     * Setter for property m_fTorschuss.
     *
     * @param m_fTorschuss New value of property m_fTorschuss.
     */
    public final void setTorschuss(float m_fTorschuss) {
        this.m_fTorschuss = m_fTorschuss;
    }

    /**
     * Getter for property m_fTorschuss.
     *
     * @return Value of property m_fTorschuss.
     */
    public final float getTorschussScaled(boolean normalized) {
		if (normalized) {
			return m_fTorschuss/getSum();
		}    	
        return m_fTorschuss/10.0f;
    }

    /**
     * Setter for property m_iTorwart.
     *
     * @param m_iTorwart New value of property m_iTorwart.
     */
    public final void setTorwart(float m_iTorwart) {
        this.m_fTorwart = m_iTorwart;
    }

    ///////////////Accessor//////////////////////

    /**
     * Getter for property m_iTorwart.
     *
     * @return Value of property m_iTorwart.
     */
    public final float getTorwartScaled(boolean normalized) {
		if (normalized) {
			return m_fTorwart/getSum();
		}    	    	
        return m_fTorwart/10.0f;
    }

    /**
     * Setter for property m_fVerteidigung.
     *
     * @param m_fVerteidigung New value of property m_fVerteidigung.
     */
    public final void setVerteidigung(float m_fVerteidigung) {
        this.m_fVerteidigung = m_fVerteidigung;
    }


    /**
     * Getter for property m_fVerteidigung.
     *
     * @return Value of property m_fVerteidigung.
     */
    public final float getVerteidigungScaled(boolean normalized) {
		if (normalized) {
			return m_fVerteidigung/getSum();
		}    	      	
        return m_fVerteidigung/10.0f;
    }

	public float getFluegelspiel() {
		return m_fFluegelspiel;
	}

	public float getPasspiel() {
		return m_fPasspiel;
	}

	public float getSpielaufbau() {
		return m_fSpielaufbau;
	}

	public float getStandards() {
		return m_fStandards;
	}

	public float getTorschuss() {
		return m_fTorschuss;
	}

	public float getTorwart() {
		return m_fTorwart;
	}

	public float getVerteidigung() {
		return m_fVerteidigung;
	}

}
