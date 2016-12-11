package model;

import controller.similarity.SimilarityFunction;
import util.Config;

/**
 * This class reads all the attributes from config file and makes them
 * accessible everywhere
 * 
 * @author FBM
 *
 */
public final class Globals {
    public static float MAX_RATING;
    public static float MIN_RATING;
    public static long MAX_ID_OF_UESRS;
    public static long MAX_ID_OF_ITEMS;
    public static String RATING_FILE_PATH;
    public static String RATING_FILE_SEPERATOR;

    public static String LOW_LEVEL_FILE_PATH;
    public static String LOW_LEVEL_FILE_SEPERATOR;

    public static String GENRE_FILE_PATH;
    public static String GENRE_FILE_SEPERATOR;

    public static String TAG_FILE_PATH;
    public static String TAG_FILE_SEPERATOR;

    public static SimilarityFunction SIMILAIRTY_FUNCTION;
    public static int NUMBER_OF_FOLDS;
    public static int TOP_N;

    public static boolean USE_ONLY_POSITIVE_RATING_IN_TEST;
    public static double MINIMUM_THRESHOLD_FOR_POSITIVE_RATING;
    public static float AT_N;
    public static boolean DROP_POPULAR_ITEM;
    public static int DROP_POPULAR_ITEM_NUMBER;
    public static boolean CALCULATE_TTEST;

    public static Long RANDOMIZATION_SEED;

    static {
        readData();
    }

    public static
            void readData() {
        RATING_FILE_PATH = Config.getString("RATING_FILE_PATH", "");
        RATING_FILE_SEPERATOR = Config.getString("RATING_FILE_SEPARATOR", " ");

        LOW_LEVEL_FILE_PATH = Config.getString("LOW_LEVEL_FILE_PATH", "");
        LOW_LEVEL_FILE_SEPERATOR = Config.getString("LOW_LEVEL_FILE_SEPARATOR",
                " ");

        GENRE_FILE_PATH = Config.getString("GENRE_FILE_PATH", "");
        GENRE_FILE_SEPERATOR = Config.getString("GENRE_FILE_SEPARATOR", " ");

        TAG_FILE_PATH = Config.getString("TAG_FILE_PATH", "");
        TAG_FILE_SEPERATOR = Config.getString("TAG_FILE_SEPARATOR", " ");

        SIMILAIRTY_FUNCTION = SimilarityFunction
                .reolve(Config.getString("SIMILARITY_FUNCTION"));
        NUMBER_OF_FOLDS = Config.getInt("NUMBER_OF_FOLDS");
        TOP_N = Config.getInt("TOP_N");

        USE_ONLY_POSITIVE_RATING_IN_TEST = Config
                .getBoolean("USE_ONLY_POSITIVE_RATING_IN_TEST");
        MINIMUM_THRESHOLD_FOR_POSITIVE_RATING = Config
                .getDouble("MINIMUM_THRESHOLD_FOR_POSITIVE_RATING");

        AT_N = Config.getInt("AT_N");

        DROP_POPULAR_ITEM = Config.getBoolean("DROP_POPULAR_ITEM");
        if(DROP_POPULAR_ITEM){
        	DROP_POPULAR_ITEM_NUMBER = Config.getInt("DROP_POPULAR_ITEM_NUMBER",0);
        }
        CALCULATE_TTEST = Config.getBoolean("CALCULATE_TTEST");
        RANDOMIZATION_SEED = Config.getLong("RANDOMIZATION_SEED", null);
    }

    public static
            void setMaxRating(
                    float maxRating)
    {
        MAX_RATING = maxRating;
    }

    public static
            void setMinRating(
                    float minRating)
    {
        MIN_RATING = minRating;
    }

    public static
            void setMaxNumberOfUsers(
                    long maxNumberOfUser)
    {
        MAX_ID_OF_UESRS = maxNumberOfUser;
    }

    public static
            void setMaxNumberOfItems(
                    long maxNumberOfItem)
    {
        MAX_ID_OF_ITEMS = maxNumberOfItem;
    }

}
