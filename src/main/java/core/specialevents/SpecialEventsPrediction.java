package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;

import java.util.List;
import java.util.Vector;

public class SpecialEventsPrediction {

    private double chanceCreationProbability = 0;
    private double goalProbability = 0;
    private ISpecialEventPredictionAnalyzer.SpecialEventType eventType;
    private IMatchRoleID responsiblePosition;
    private List<IMatchRoleID> involvedPositions;
    private List<IMatchRoleID> involvedOpponentPositions;

    public SpecialEventsPrediction(IMatchRoleID position, ISpecialEventPredictionAnalyzer.SpecialEventType type, double p) {
        responsiblePosition = position;
        eventType = type;
        chanceCreationProbability = p;
    }

    public IMatchRoleID getResponsiblePosition() {
        return responsiblePosition;
    }

    public void setResponsiblePosition(IMatchRoleID responsiblePosition) {
        this.responsiblePosition = responsiblePosition;
    }

    public List<IMatchRoleID> getInvolvedPositions() {
        return involvedPositions;
    }

    public void setInvolvedPositions(List<IMatchRoleID> m_cInvolvedPositions) {
        this.involvedPositions = m_cInvolvedPositions;
    }

    public void setInvolvedPosition(MatchRoleID mid) {
        if (this.involvedPositions == null) {
            this.involvedPositions = new Vector<IMatchRoleID>();
        } else {
            this.involvedPositions.clear();
        }
        this.involvedPositions.add(mid);
    }

    public void addInvolvedPosition(MatchRoleID mid){
        if (this.involvedPositions == null) {
            this.involvedPositions = new Vector<IMatchRoleID>();
        }
        this.involvedPositions.add(mid);
    }

    public List<IMatchRoleID> getInvolvedOpponentPositions() {
        return involvedOpponentPositions;
    }

    public void setInvolvedOpponentPosition(MatchRoleID mid) {
        if (this.involvedOpponentPositions == null) {
            this.involvedOpponentPositions = new Vector<IMatchRoleID>();
        } else {
            this.involvedOpponentPositions.clear();
        }
        this.involvedOpponentPositions.add(mid);
    }

    public void addInvolvedOpponentPosition(MatchRoleID mid){
        if (this.involvedOpponentPositions == null) {
            this.involvedOpponentPositions = new Vector<IMatchRoleID>();
        }
        this.involvedOpponentPositions.add(mid);
    }

    static public SpecialEventsPrediction createIfInRange(IMatchRoleID position,
                                                          ISpecialEventPredictionAnalyzer.SpecialEventType eventName,
                                                          double maxProbability,
                                                          double valueAtMaxProbability,
                                                          double valueAtNullProbability,
                                                          double value) {
        if (valueAtMaxProbability > valueAtNullProbability) {
            if (value <= valueAtNullProbability ) return null;
            if (value > valueAtMaxProbability) return new SpecialEventsPrediction(position, eventName, valueAtMaxProbability);
        } else {
            if (value >= valueAtNullProbability ) return null;
            if (value < valueAtMaxProbability) return new SpecialEventsPrediction(position, eventName, valueAtMaxProbability);
        }
        double f = maxProbability - maxProbability / (valueAtNullProbability - valueAtMaxProbability) * (value - valueAtMaxProbability);  // linear fit
        return new SpecialEventsPrediction(position, eventName, f);
    }

    public double getGoalProbability() {
        return goalProbability;
    }

    public void setGoalProbability(double goalProbability) {
        this.goalProbability = goalProbability;
    }
}
