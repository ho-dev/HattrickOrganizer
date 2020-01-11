package core.specialevents;

import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class SpecialEventsPrediction {

    private double chanceCreationProbability = 0;
    private double goalProbability = 0;
    private ISpecialEventPredictionAnalyzer.SpecialEventType eventType;
    private IMatchRoleID responsiblePosition;
    private HashSet<IMatchRoleID> involvedPositions;
    private HashSet<IMatchRoleID> involvedOpponentPositions;

    public SpecialEventsPrediction(IMatchRoleID position, ISpecialEventPredictionAnalyzer.SpecialEventType type, double p) {
        responsiblePosition = position;
        eventType = type;
        chanceCreationProbability = p;
    }

    public SpecialEventsPrediction(SpecialEventsPrediction se) {
        this.chanceCreationProbability = se.chanceCreationProbability;
        this.eventType = se.eventType;
        this.goalProbability = se.goalProbability;
        if (se.involvedOpponentPositions != null) {
            this.involvedOpponentPositions = new HashSet<>(se.involvedOpponentPositions);
        }
        if (se.involvedPositions != null) {
            this.involvedPositions = new HashSet<>(se.involvedPositions);
        }
        this.responsiblePosition = se.responsiblePosition;
    }

    public IMatchRoleID getResponsiblePosition() {
        return responsiblePosition;
    }

    public void setResponsiblePosition(IMatchRoleID responsiblePosition) {
        this.responsiblePosition = responsiblePosition;
    }

    public HashSet<IMatchRoleID> getInvolvedPositions() {
        return involvedPositions;
    }

    public void setInvolvedPositions(HashSet<IMatchRoleID> m_cInvolvedPositions) {
        this.involvedPositions = m_cInvolvedPositions;
    }

    public void setInvolvedPosition(MatchRoleID mid) {
        if (this.involvedPositions == null) {
            this.involvedPositions = new HashSet<IMatchRoleID>();
        } else {
            this.involvedPositions.clear();
        }
        this.involvedPositions.add(mid);
    }

    public void addInvolvedPosition(MatchRoleID mid) {
        if (this.involvedPositions == null) {
            this.involvedPositions = new HashSet<IMatchRoleID>();
        }
        this.involvedPositions.add(mid);
    }

    public HashSet<IMatchRoleID> getInvolvedOpponentPositions() {
        return involvedOpponentPositions;
    }

    public void setInvolvedOpponentPosition(MatchRoleID mid) {
        if (this.involvedOpponentPositions == null) {
            this.involvedOpponentPositions = new HashSet<IMatchRoleID>();
        } else {
            this.involvedOpponentPositions.clear();
        }
        this.involvedOpponentPositions.add(mid);
    }

    public void addInvolvedOpponentPosition(MatchRoleID mid) {
        if (this.involvedOpponentPositions == null) {
            this.involvedOpponentPositions = new HashSet<IMatchRoleID>();
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
            if (value <= valueAtNullProbability) return null;
            if (value > valueAtMaxProbability)
                return new SpecialEventsPrediction(position, eventName, maxProbability);
        } else {
            if (value >= valueAtNullProbability) return null;
            if (value < valueAtMaxProbability)
                return new SpecialEventsPrediction(position, eventName, maxProbability);
        }
        double f = maxProbability - maxProbability / (valueAtNullProbability - valueAtMaxProbability) * (value - valueAtMaxProbability);  // linear fit
        return new SpecialEventsPrediction(position, eventName, f);
    }

    public void setChanceCreationProbability(double p) {
        this.chanceCreationProbability = p;
    }

    public double getChanceCreationProbability() {
        return chanceCreationProbability;
    }

    public double getGoalProbability() {
        return goalProbability;
    }

    public void setGoalProbability(double goalProbability) {
        this.goalProbability = goalProbability;
    }

    public String getEventTypeAsString() {
        return this.eventType.toString();
    }

    public ISpecialEventPredictionAnalyzer.SpecialEventType getEventType() {
        return this.eventType;
    }
}