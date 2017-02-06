package interfaces;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import model.DataModel;
import model.DataType;
import model.Item;
import model.User;

/**
 * @author FBM
 *
 */
public class AbstractRecommender implements Recommender,Serializable {

	private static final long serialVersionUID = -7339073808310101731L;
	protected transient DataModel trainDataModel;
	private DataType dataType;
	/**
	 * A map which contains information about the fields name in the class
	 * and the related key in config file.
	 * Key = filed name
	 * Value = Map<config file key,pretty name>
	 * pretty name used in GUI
	 */
	protected transient final Map<String,Map<String,String>> configurableParametersMap = new HashMap<>();
	/**
	 * Repository used for calculating similarities
	 */
	protected transient SimilarityInterface similarityRepository;
	
	/* (non-Javadoc)
	 * @see interfaces.Recommender#predictRating(model.User, model.Item)
	 */
	@Override
	public Float predictRating(User user, Item item) {
		return null;
	}

	/* (non-Javadoc)
	 * @see interfaces.Recommender#recommendItems(model.User)
	 */
	@Override
	public Map<Integer, Float> recommendItems(User user) {
		return null;
	}

	/* (non-Javadoc)
	 * @see interfaces.Recommender#train(model.DataModel)
	 */
	@Override
	public void train(DataModel trainData) {
		this.trainDataModel = trainData;
	}

	/* (non-Javadoc)
	 * @see interfaces.Recommender#setSimilarityRepository(interfaces.SimilarityInterface)
	 */
	@Override
	public void setSimilarityRepository(SimilarityInterface similarityRepository) {
		if (similarityRepository == null) {
			throw new IllegalArgumentException("SimilarityRepository is null");
		}
		this.similarityRepository = similarityRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see interfaces.Recommender#getSimilarityRepository()
	 */
	@Override
	public SimilarityInterface getSimilarityRepository() {
		return similarityRepository;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.Recommender#isSimilairtyNeeded()
	 */
	@Override
	public boolean isSimilairtyNeeded() {
		return true;
	}

	/* (non-Javadoc)
	 * @see interfaces.Recommender#getConfigurabaleParameters()
	 */
	@Override
	public Map<String,Map<String,String>> getConfigurabaleParameters() {
		return this.configurableParametersMap;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

}
