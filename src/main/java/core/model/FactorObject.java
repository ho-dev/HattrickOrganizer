package core.model;

import core.model.player.IMatchRoleID;
import core.util.HOLogger;

public final class FactorObject {
    //~ Instance fields ----------------------------------------------------------------------------

    /** The position that is described by this FactorObject */
    private byte m_bPosition = IMatchRoleID.UNKNOWN;

    /** Influence of Winger for this position */
    private float fWing;

    /** Influence of Passing for this position */
    private float fPassing;

    /** Influence of Playmaking for this position */
    private float fPlaymaking;

    /** Influence of Set Pieces for this position */
    private float fSetPieces;

    /** Influence of Scoring for this position */
    private float fScoring;

    /** Influence of Goalkeeping for this position */
    private float fGoalkeeping;

    /** Influence of Defending for this position */
    private float fDefending;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of FactorObject
     */
    public FactorObject(byte position, float gk, float pm, float ps,
                        float wi, float de, float sc, float sp) {
        fScoring = sc;
        fGoalkeeping = gk;
        fSetPieces = sp;
        fPlaymaking = pm;
        fPassing = ps;
        fDefending = de;
        fWing = wi;
        m_bPosition = position;
    }

    /**
     * Creates a new FactorObject object.
     */
    public FactorObject(java.sql.ResultSet rs) {
        try {
            if (rs != null) {
                fScoring = rs.getFloat("Torschuss");
                fGoalkeeping = rs.getFloat("Torwart");
                fSetPieces = rs.getFloat("Standards");
                fPlaymaking = rs.getFloat("Spielaufbau");
                fPassing = rs.getFloat("Passpiel");
                fDefending = rs.getFloat("Verteidigung");
                fWing = rs.getFloat("Fluegel");
                m_bPosition = rs.getByte("HOPosition");
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"Konstruktor Faktor Obj: " + e.toString());
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property fWing.
     *
     * @param m_fFluegelspiel New value of property fWing.
     */
    public final void setFluegelspiel(float m_fFluegelspiel) {
        this.fWing = m_fFluegelspiel;
    }

    /**
     * Getter for property fWing.
     *
     * @return Value of property fWing.
     */
    public final float getFluegelspielScaled(boolean normalized) {
    	if (normalized) {
    		return fWing /getSum();
    	}
        return fWing /10.0f;
    }

    /**
     * Setter for property fPassing.
     *
     * @param m_fPasspiel New value of property fPassing.
     */
    public final void setPasspiel(float m_fPasspiel) {
        this.fPassing = m_fPasspiel;
    }

    /**
     * Getter for property fPassing.
     *
     * @return Value of property fPassing.
     */
    public final float getPasspielScaled(boolean normalized) {
		if (normalized) {
			return fPassing /getSum();
		}
        return fPassing /10.0f;
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
     * Setter for property fPlaymaking.
     *
     * @param m_fSpielaufbau New value of property fPlaymaking.
     */
    public final void setSpielaufbau(float m_fSpielaufbau) {
        this.fPlaymaking = m_fSpielaufbau;
    }

    /**
     * Getter for property fPlaymaking.
     *
     * @return Value of property fPlaymaking.
     */
    public final float getSpielaufbauScaled(boolean normalized) {
		if (normalized) {
			return fPlaymaking /getSum();
		}
        return fPlaymaking /10.0f;
    }


    /**
     * Setter for property fSetPieces.
     *
     * @param m_fStandards New value of property fSetPieces.
     */
    public final void setStandards(float m_fStandards) {
        this.fSetPieces = m_fStandards;
    }

    /**
     * Getter for property fSetPieces.
     *
     * @return Value of property fSetPieces.
     */
    public final float getStandardsScaled(boolean normalized) {
		if (normalized) {
			return fSetPieces /getSum();
		}    	
        return fSetPieces /10.0f;
    }

    //HelperFuncs//////
    public final float getSum() {
        return (fGoalkeeping + fSetPieces + fScoring + fDefending + fWing
               + fPassing + fPlaymaking);
    }

    /**
     * Setter for property fScoring.
     *
     * @param m_fTorschuss New value of property fScoring.
     */
    public final void setTorschuss(float m_fTorschuss) {
        this.fScoring = m_fTorschuss;
    }

    /**
     * Getter for property fScoring.
     *
     * @return Value of property fScoring.
     */
    public final float getTorschussScaled(boolean normalized) {
		if (normalized) {
			return fScoring /getSum();
		}    	
        return fScoring /10.0f;
    }

    /**
     * Setter for property m_iTorwart.
     *
     * @param m_iTorwart New value of property m_iTorwart.
     */
    public final void setTorwart(float m_iTorwart) {
        this.fGoalkeeping = m_iTorwart;
    }

    ///////////////Accessor//////////////////////

    /**
     * Getter for property m_iTorwart.
     *
     * @return Value of property m_iTorwart.
     */
    public final float getTorwartScaled(boolean normalized) {
		if (normalized) {
			return fGoalkeeping /getSum();
		}    	    	
        return fGoalkeeping /10.0f;
    }

    /**
     * Setter for property fDefending.
     *
     * @param m_fVerteidigung New value of property fDefending.
     */
    public final void setVerteidigung(float m_fVerteidigung) {
        this.fDefending = m_fVerteidigung;
    }


    /**
     * Getter for property fDefending.
     *
     * @return Value of property fDefending.
     */
    public final float getVerteidigungScaled(boolean normalized) {
		if (normalized) {
			return fDefending /getSum();
		}    	      	
        return fDefending /10.0f;
    }

	public float getWIfactor() {
		return fWing;
	}

	public float getPSfactor() {
		return fPassing;
	}

	public float getPMfactor() {
		return fPlaymaking;
	}

	public float getSPfactor() {
		return fSetPieces;
	}

	public float getSCfactor() {
		return fScoring;
	}

	public float getGKfactor() {
		return fGoalkeeping;
	}

	public float getDEfactor() {
		return fDefending;
	}

}
