package algorithms;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import algorithms.fatorizationmachine.FactorizationMachineModel;
import algorithms.fatorizationmachine.SGDLearner;
import controller.similarity.SimilarityRepository;
import interfaces.Recommender;
import interfaces.SimilarityInterface;
import model.DataModel;
import model.Item;
import model.Rating;
import model.User;
import run.Configuration;
import util.MapUtil;

/**
 * This is Factorization Machine algorithm based on:
 * "Factorization Machines", Steffan Rendle 
 * @author FBM
 *
 */
public final class FactorizationMachine implements Recommender {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(FactorizationMachine.class.getCanonicalName());
    private FactorizationMachineModel model;
    private Configuration configuration;

    public FactorizationMachine() {
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#predictRating(model.User, model.Item)
     */
    @Override
    public Float predictRating(final User user,final Item item) {
	if (item == null) {
	    throw new IllegalArgumentException("Item is null");
	}
	if (user == null) {
	    throw new IllegalArgumentException("User is null");
	}
	return this.model.calculate(new Rating(user.getId(), item.getId(), (float) 0));
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#recommendItems(model.User)
     */
    @Override
    public Map<Integer, Float> recommendItems(final User user) {
	if(user == null){
	    throw new IllegalArgumentException("User is null");
	}
	final Map<Integer, Float> predictions = new LinkedHashMap<Integer, Float>();

	for (final Item item : model.getDataModel().getItems().values()) {
	    final int itemId = item.getId();
	    final float predictRating = predictRating(user, item);
	    if (!Float.isNaN(predictRating)) {
		predictions.put(itemId, predictRating);
	    }
	}
	final Map<Integer, Float> sortByComparator = MapUtil.sortByValueDescending(predictions);
	return sortByComparator;
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#train(model.DataModel)
     */
    @Override
    public void train(final DataModel trainData) {
	if(trainData == null){
	    throw new IllegalArgumentException("Train data is null");
	}
	final long now = new Date().getTime();
	this.model = new FactorizationMachineModel(trainData, configuration);
	new SGDLearner().learn(this.model);
	LOG.debug("Train time: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - now));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "FactorizationMachine";
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.Recommender#setSimilarityRepository(controller.
     * SimilarityRepository)
     */
    @Override
    public void setSimilarityRepository(SimilarityInterface similarityRepository) {
	if(similarityRepository == null){
	    throw new IllegalArgumentException("SimilarityRepository is null");
	}
	if (similarityRepository instanceof SimilarityRepository) {
	    this.configuration = ((SimilarityRepository) similarityRepository).getConfiguration();
	}
    }

    /*
     * (non-Javadoc)
     * @see interfaces.Recommender#isSimilairtyNeeded()
     */
    @Override
    public boolean isSimilairtyNeeded() {
	return false;
    }

}
