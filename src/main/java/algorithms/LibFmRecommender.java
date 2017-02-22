package algorithms;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import algorithms.fm.NewFactorizationMachine;
import controller.similarity.SimilarityRepository;
import gui.model.LearningMethod;
import interfaces.AbstractRecommender;
import interfaces.SimilarityInterface;
import model.DataModel;
import model.DataType;
import model.Item;
import model.User;
import run.Configuration;
import util.MapUtil;

/**
 * This class is used to create a recommender instance, that uses the
 * factorization machines approach by Rendle et. al. to predict ratings.
 * 
 */
public class LibFmRecommender extends AbstractRecommender {

	private static final long serialVersionUID = -6385294423366379817L;
	private NewFactorizationMachine _fm;
	private LearningMethod _method = LearningMethod.ALS;
	private double _initStdev = 0.1;
	private int numberOfFeatures;
	private int[] _dim = new int[] { 1, 1, 8};
	private boolean _doSampling = true;
	private boolean _doMultilevel = true;
	private int numberOfIteration = 100;
	private int _numEvalCases = 100;
	private TaskType _taskType = TaskType.Regression;
	private double[] _regular = new double[] {0, 0, 0.0025};
	private double learningRate;
	private boolean _verbose = false;
	private boolean _contextEnabled = true;
	private HashMap<Integer, HashMap<Integer, int[]>> _contextSourceForTestData;
	private Configuration configuration;
	
	public LibFmRecommender() {
		final HashMap<String, String> h1 = new HashMap<String, String>();
		h1.put("NUMBER_OF_FEATURES", "Numbre of latent factor");
		this.configurableParametersMap.put("numberOfFeatures", h1);

		final HashMap<String, String> h2 = new HashMap<String, String>();
		h2.put("NUMBER_OF_ITERATION", "Number of iteration");
		this.configurableParametersMap.put("numberOfIteration", h2);

		final HashMap<String, String> h3 = new HashMap<String, String>();
		h3.put("LEARNING_RATE", "Learning rate");
		this.configurableParametersMap.put("learningRate", h3);
	}

	@Override
	public Float predictRating(User user, Item item) {
		return _fm.PredictRating(user.getId(), item.getId());
	}
	
	@Override
	public void setSimilarityRepository(SimilarityInterface similarityRepository) {
		if (similarityRepository == null) {
			throw new IllegalArgumentException("SimilarityRepository is null");
		}
		if (similarityRepository instanceof SimilarityRepository) {
			this.configuration = ((SimilarityRepository) similarityRepository).getConfiguration();
		}
	}

	@Override
	public Map<Integer, Float> recommendItems(User user) {
		final Map<Integer, Float> predictions = new LinkedHashMap<Integer, Float>();

		for (final Item item : trainDataModel.getItems().values()) {
			final int itemId = item.getId();
			final float predictRating = predictRating(user, item);
			if (!Float.isNaN(predictRating)) {
				predictions.put(itemId, predictRating);
			}
		}
		final Map<Integer, Float> sortByComparator = MapUtil.sortByValueDescending(predictions);
		return sortByComparator;
	}

	public enum TaskType {
		Regression, Classification
	}

	@Override
	public void train(DataModel trainData) {
		if(this.configuration.getDataType()==DataType.Personality){
			_contextEnabled = true;
		}
		else{
			_contextEnabled = false;
		}
		try {
			_dim[2] = numberOfFeatures;
			trainDataModel = trainData.getCopy();
			_fm = new algorithms.fm.NewFactorizationMachine();
			_fm.Initialize(_method, _initStdev, _dim, _doSampling, _doMultilevel, numberOfIteration, _numEvalCases,
					_taskType, _regular, learningRate, _verbose, trainData, _contextEnabled, _contextSourceForTestData,this.configuration.getDataType());
			_fm.Learn();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "LibFmRecommender";
	}

	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}

	public void setNumberOfFeatures(int numberOfFeatures) {
		this.numberOfFeatures = numberOfFeatures;
	}

	public int getNumberOfIteration() {
		return numberOfIteration;
	}

	public void setNumberOfIteration(int numberOfIteration) {
		this.numberOfIteration = numberOfIteration;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
}
