package controller.similarity;

import java.io.Serializable;

import org.apache.log4j.Logger;

import interfaces.SimilarityInterface;
import model.DataModel;
import run.Configuration;

/**
 * This class is responsible for creating relative {@link SimilairityRepository} based on the given {@link Configuration}
 * 
 * @author FBM
 */
public final class SimilarityRepository implements SimilarityInterface,Serializable {

    /**
	 * Unique id used for serialization
	 */
	private static final long serialVersionUID = -1351308470553215704L;
	/**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(SimilarityRepository.class.getCanonicalName());
    private transient final SimilarityInterface similairtyRepository;
    private final Configuration configuration;

    /**
     * Constructor
     * @param trainData
     * @param configuration
     */
    public SimilarityRepository(final DataModel trainData, final Configuration configuration) {
	if (trainData == null) {
	    throw new IllegalArgumentException("Train data model is null");
	}
	if (configuration == null) {
	    throw new IllegalArgumentException("Configuration is null");
	}
	this.configuration = configuration;

	if (configuration.isUseLowLevel() && configuration.isUseGenre()) {
	    similairtyRepository = new LowLevelGenreSimilarityRepository(trainData);
	} else if (configuration.isUseTag()) {
	    similairtyRepository = new TagSimilarityRepository(trainData);
	} else if (configuration.isUseLowLevel()) {
	    similairtyRepository = new LowLevelSimilarityRepository(trainData);
	} else if (configuration.isUseGenre()) {
	    similairtyRepository = new GenreSimilarityRepository(trainData);
	} else if (configuration.isUseRating()) {
	    similairtyRepository = new RatingSimilarityRepository(trainData);
	} else {
	    LOG.warn("Default similairty repository is selected");
	    similairtyRepository = new RatingSimilarityRepository(trainData);
	}
    }

    /*
     * @see interfaces.SimilarityInterface#getItemSimilairty(int, int)
     */
    @Override
    public Float getItemSimilairty(final int itemId1, final int itemId2) {
	return this.similairtyRepository.getItemSimilairty(itemId1, itemId2);
    }

    /**
     * @return The configuration
     */
    public final Configuration getConfiguration() {
	return configuration;
    }

}
