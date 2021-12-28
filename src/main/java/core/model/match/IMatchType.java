package core.model.match;

public interface IMatchType {

    int getMatchTypeId();

    int getIconArrayIndex();
    String getName();
    default boolean isCompetitive() {
        return false;
    }
}
