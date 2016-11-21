package model;

import controller.similarity.SimilarityFunction;
import util.Config;

/**
 * this class reads all the attributes from config file and makes them accessible everywhere
 * @author FBM
 *
 */
public final class Globals {
    public static float MAX_RATING;
    public static float MIN_RATING;
    public static long MAX_ID_OF_UESRS;
    public static long MAX_ID_OF_ITEMS;
    public static final String RATING_FILE_PATH;
    public static final String RATING_FILE_SEPERATOR;

    public static final String LOW_LEVEL_FILE_PATH;
    public static final String LOW_LEVEL_FILE_SEPERATOR;

    public static final String GENRE_FILE_PATH;
    public static final String GENRE_FILE_SEPERATOR;

    public static final String TAG_FILE_PATH;
    public static final String TAG_FILE_SEPERATOR;

    public static final SimilarityFunction SIMILAIRTY_FUNCTION;
    public static final double NUMBER_OF_FOLDS;
    public static final int TOP_N;

    public static final int NUMBER_OF_FEATURES_FOR_FM;
    public static final int NUMBER_OF_FEATURES_FOR_FUNKSVD;
    public static final int NUMBER_OF_ITERATION_FOR_FUNKSVD;
    public static final float LEARNING_RATE_FOR_FM;
    public static final int NUMBER_OF_ITERATION_FOR_FM;
    public static final int NUMBER_OF_NEAREST_NEIGHBOUR;
    public static final double LEARNING_RATE_FOR_FUNKSVD;
    public static final boolean USE_ONLY_POSITIVE_RATING_IN_TEST;
    public static final double MINIMUM_THRESHOLD_FOR_POSITIVE_RATING;
    public static final float AT_N;
    public static final boolean DROP_POPULAR_ITEM;
    public static final int DROP_POPULAR_ITEM_NUMBER;
    public static final boolean CALCULATE_TTEST;
    
    public static final Long RANDOMIZATION_SEED; 

    static {
	RATING_FILE_PATH = Config.getString("RATING_FILE_PATH", "");
	RATING_FILE_SEPERATOR = Config.getString("RATING_FILE_SEPARATOR", " ");

	LOW_LEVEL_FILE_PATH = Config.getString("LOW_LEVEL_FILE_PATH", "");
	LOW_LEVEL_FILE_SEPERATOR = Config.getString("LOW_LEVEL_FILE_SEPARATOR", " ");

	GENRE_FILE_PATH = Config.getString("GENRE_FILE_PATH", "");
	GENRE_FILE_SEPERATOR = Config.getString("GENRE_FILE_SEPARATOR", " ");

	TAG_FILE_PATH = Config.getString("TAG_FILE_PATH", "");
	TAG_FILE_SEPERATOR = Config.getString("TAG_FILE_SEPARATOR", " ");

	SIMILAIRTY_FUNCTION = SimilarityFunction.reolve(Config.getString("SIMILARITY_FUNCTION", "cosine"));
	NUMBER_OF_FOLDS = Config.getDouble("NUMBER_OF_FOLDS", 0.0);
	TOP_N = Config.getInt("TOP_N", 1);
	NUMBER_OF_FEATURES_FOR_FM = Config.getInt("NUMBER_OF_FEATURES_FOR_FM", 1);
	NUMBER_OF_FEATURES_FOR_FUNKSVD = Config.getInt("NUMBER_OF_FEATURES_FOR_FUNKSVD", 1);
	LEARNING_RATE_FOR_FM = (float) Config.getDouble("LEARNING_RATE_FOR_FM", 0.01);
	LEARNING_RATE_FOR_FUNKSVD = (float) Config.getDouble("LEARNING_RATE_FOR_FUNKSVD", 0.005);
	NUMBER_OF_ITERATION_FOR_FM = Config.getInt("NUMBER_OF_ITERATION_FOR_FM", 1);
	NUMBER_OF_ITERATION_FOR_FUNKSVD = Config.getInt("NUMBER_OF_ITERATION_FOR_FUNKSVD", 1);
	NUMBER_OF_NEAREST_NEIGHBOUR = Config.getInt("NUMBER_OF_NEAREST_NEIGHBOUR", 1);
	
	USE_ONLY_POSITIVE_RATING_IN_TEST = Config.getBoolean("USE_ONLY_POSITIVE_RATING_IN_TEST", true);
	MINIMUM_THRESHOLD_FOR_POSITIVE_RATING = Config.getDouble("MINIMUM_THRESHOLD_FOR_POSITIVE_RATING", 3.5);
	
	AT_N=Config.getInt("AT_N", 4);
	
	DROP_POPULAR_ITEM=Config.getBoolean("DROP_POPULAR_ITEM", false);
	DROP_POPULAR_ITEM_NUMBER = Config.getInt("NUMBER_OF_NEAREST_NEIGHBOUR", 1);
	CALCULATE_TTEST=Config.getBoolean("CALCULATE_TTEST", false);
	RANDOMIZATION_SEED=Config.getLong("RANDOMIZATION_SEED", null);
    }

    public static void setMaxRating(float maxRating) {
	MAX_RATING = maxRating;
    }

    public static void setMinRating(float minRating) {
	MIN_RATING = minRating;
    }

    public static void setMaxNumberOfUsers(long mAX_NUMBER_OF_UESRS) {
	MAX_ID_OF_UESRS = mAX_NUMBER_OF_UESRS;
    }

    public static void setMaxNumberOfItems(long mAX_NUMBER_OF_ITEMS) {
	MAX_ID_OF_ITEMS = mAX_NUMBER_OF_ITEMS;
    }

}
