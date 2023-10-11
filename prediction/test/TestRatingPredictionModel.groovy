import module.lineup.*
import core.rating.RatingPredictionModel
import core.rating.RatingPredictionModel.*
import core.model.Team

@groovy.transform.InheritConstructors
class TestRatingPredictionModel extends RatingPredictionModel {

    TestRatingPredictionModel(Team team){
        super(team)
    }

    @Override
    double calcSectorRating(Lineup lineup, RatingSector s, int minute) { return 1 }
}