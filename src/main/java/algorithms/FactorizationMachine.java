package algorithms;

import algorithms.fatorizationmachine.FactorizationMachineModel;
import algorithms.fatorizationmachine.SGDLearner;
import controller.similarity.SimilarityRepository;
import interfaces.AbstractRecommender;
import interfaces.SimilarityInterface;
import model.DataModel;
import model.Item;
import model.Rating;
import model.User;
import org.apache.log4j.Logger;
import run.Configuration;
import util.MapUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is Factorization Machine algorithm based on: "Factorization Machines",
 * Steffan Rendle
 * 
 * @author FBM
 *
 */
public final class FactorizationMachine extends AbstractRecommender {

	/**
	 * Unique id used for serialization
	 */
	private static final long serialVersionUID = -1309491493850748844L;
	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(FactorizationMachine.class.getCanonicalName());
	private FactorizationMachineModel model;
	private Configuration configuration;
	/**
	 * Number of features
	 */
	public int numberOfLatentFactors;
	/**
     * Number of iteration for stopping learning
     */
	private int numberOfIteration;
	/**
	 * Learning rate
	 */
	private float learnRates;

	public FactorizationMachine() {
		final HashMap<String, String> h1 = new HashMap<String, String>();
        h1.put("NUMBER_OF_FEATURES_FOR_FM", "Number of latent factor");
		this.configurableParametersMap.put("numberOfLatentFactors", h1);
		
		final HashMap<String, String> h2 = new HashMap<String, String>();
		h2.put("NUMBER_OF_ITERATION_FOR_FM","Number of iteration");
		this.configurableParametersMap.put("numberOfIteration", h2);
		
		final HashMap<String, String> h3 = new HashMap<String, String>();
		h3.put("LEARNING_RATE_FOR_FM","Learning rate");
		this.configurableParametersMap.put("learnRates", h3);
	}

	/**
	 * @return the numberOfLatentFactors
	 */
	public final int getNumberOfLatentFactors() {
		return numberOfLatentFactors;
	}


	/**
	 * @param numberOfLatentFactors the numberOfLatentFactors to set
	 */
	public final void setNumberOfLatentFactors(int numberOfLatentFactors) {
		this.numberOfLatentFactors = numberOfLatentFactors;
	}


	/**
	 * @return the numberOfIteration
	 */
	public final int getNumberOfIteration() {
		return numberOfIteration;
	}


	/**
	 * @param numberOfIteration the numberOfIteration to set
	 */
	public final void setNumberOfIteration(int numberOfIteration) {
		this.numberOfIteration = numberOfIteration;
	}


	/**
	 * @return the learnRates
	 */
	public final float getLearnRates() {
		return learnRates;
	}


	/**
	 * @param learnRates the learnRates to set
	 */
	public final void setLearnRates(float learnRates) {
		this.learnRates = learnRates;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see interfaces.Recommender#predictRating(model.User, model.Item)
	 */
	@Override
	public Float predictRating(final User user, final Item item) {
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
	 * 
	 * @see interfaces.Recommender#recommendItems(model.User)
	 */
	@Override
	public Map<Integer, Float> recommendItems(final User user) {
		if (user == null) {
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
	 * 
	 * @see interfaces.Recommender#train(model.DataModel)
	 */
	@Override
	public void train(final DataModel trainData) {
		if (trainData == null) {
			throw new IllegalArgumentException("Train data is null");
		}
		this.model = new FactorizationMachineModel(trainData, this.configuration,this.numberOfLatentFactors);
		new SGDLearner(this.numberOfIteration,this.learnRates).learn(this.model);
	}

	/*
	 * (non-Javadoc)
	 * 
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
		if (similarityRepository == null) {
			throw new IllegalArgumentException("SimilarityRepository is null");
		}
		if (similarityRepository instanceof SimilarityRepository) {
			this.configuration = ((SimilarityRepository) similarityRepository).getConfiguration();
		}
	}

}
