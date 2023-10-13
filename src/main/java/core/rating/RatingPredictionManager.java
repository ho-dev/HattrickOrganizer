package core.rating;

import core.model.HOVerwaltung;
import core.model.Team;
import core.util.HOLogger;
import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Manage the rating prediction models
 * Default model is implemented by the class RatingPredictionModel
 * Groovy classes overriding this default can be loaded from the prediction directory
 */
public class RatingPredictionManager {

    private GroovyClassLoader loader = null;
    private RatingPredictionModel ratingPredictionModel;
    private static final String predictionDirectory = "prediction";
    private static String ratingPredictionModelName = null;

    /**
     * Get a list of all available prediction models
     * The default model is named "Schum (default)".
     * Further models are names of subdirectories of the directory "prediction".
     * @return List of strings
     */
    public List<String> getAllPredictionModelNames(){
        var ret = new ArrayList<String>();
        ret.add("Schum (default)");
        var dirs = new File(predictionDirectory).listFiles();
        if ( dirs != null) {
            for (var f : dirs) {
                if ( f.isDirectory()){
                    ret.add(f.getName());
                }
            }
        }
        return ret;
    }

    /**
     * Get the loaded rating prediction model.
     * @return RatingPredictionModel
     */
    public  RatingPredictionModel getRatingPredictionModel(){
        return this.ratingPredictionModel;
    }

    /**
     * Load a rating prediction model, if not loaded already.
     * @param modelName Name of the model (subdirectory name of prediction directory)
     * @param t Team
     * @return RatingPredictionModel
     */
    public RatingPredictionModel getRatingPredictionModel(String modelName, Team t){
        if (modelName.equals(ratingPredictionModelName)){
            return this.ratingPredictionModel;
        }
        return createRatingPredictionModel(modelName, t);
    }

    /**
     * Create a rating prediction model.
     * If the model name exists in the prediction directory the file *RatingPredictionModel.groovy is loaded.
     * If the model cannot be loaded from prediction directory the default model is created.
     * @param modelName Name of the model
     * @param team Team
     * @return RatingPredictionModel
     */
    private RatingPredictionModel createRatingPredictionModel(String modelName, Team team) {
        var groovyModelDir = new File(predictionDirectory + File.separator + modelName);
        if (groovyModelDir.isDirectory()) {
            var groovyModelFile = Arrays.stream(Objects.requireNonNull(groovyModelDir.listFiles((dir, name) -> name.toLowerCase().endsWith("ratingpredictionmodel.groovy")))).findFirst();
            if (groovyModelFile.isPresent()){
                if ( loader == null) loader = new GroovyClassLoader();
                try {
                    var groovyClass = loader.parseClass(groovyModelFile.get());
                    var constr =  groovyClass.getConstructor(Team.class);
                    var model = constr.newInstance(HOVerwaltung.instance().getModel().getTeam());
                    ratingPredictionModelName = groovyModelDir.getName();
                    ratingPredictionModel = (RatingPredictionModel) model;
                    return ratingPredictionModel;

                } catch (Exception e) {
                    HOLogger.instance().error(getClass(), "Can not load groovy rating model class " + groovyModelFile.get().getName() + ". Default model is used instead. " + e);
                }
            }
        }
        ratingPredictionModelName = "Schum (default)";
        ratingPredictionModel = new RatingPredictionModel(team);
        return ratingPredictionModel;
    }
}