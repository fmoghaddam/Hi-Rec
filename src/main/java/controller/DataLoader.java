package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import model.DataModel;
import model.Globals;
import model.Item;
import model.Rating;
import model.User;

/**
 * This class is responsible for reading files and converting them to
 * {@link DataModel} All the functions in this class ignore the lines which
 * starts with "#"
 * 
 * @author FBM
 */
public final class DataLoader {

    /**
     * Logger for this class
     */
    private Logger LOG = Logger.getLogger(DataLoader.class.getCanonicalName());
    private final DataModel dataModel = new DataModel();
    private final List<Integer> doNotAddList;
    {
        if(Globals.DROP_800_POPULAR_ITEM){
            /**
             * Ids of 800 most popular items in 5% (rating sampled) data.
             */
            doNotAddList= Arrays.asList(318,593,260,589,150,1196,592,780,1198,2858,590,457,1270,2959,858,4993,588,608,2762,2028,344,377,7153,648,165,1721,595,736,1197,1193,1580,316,367,1240,1097,231,153,733,1291,4226,1136,1214,541,2628,3793,2716,586,1089,539,587,329,1617,357,1213,1200,1527,253,58559,2997,912,924,2683,4886,778,111,2329,1961,434,1682,4995,5445,1206,2396,1258,185,454,3996,95,7361,339,4963,1208,1968,161,1393,2706,1387,1222,1517,141,3147,919,1917,223,750,1732,288,1784,1307,2291,7438,2115,1584,1080,2797,2502,2987,2355,2916,551,4022,1246,4878,4027,300,2324,2011,1219,1356,3897,5989,1079,4011,2710,474,1653,2791,4896,2692,104,1485,904,788,508,923,410,442,594,1407,2617,337,1625,3623,786,908,368,1247,3481,48516,3977,60069,44191,1391,235,2054,1374,2700,3751,266,920,1252,509,59315,3408,1234,1304,1676,555,2353,2712,953,353,5618,1090,653,1394,3052,1278,2194,1148,163,420,3527,350,2640,370,1207,784,2081,1370,1500,903,2302,3471,1544,1552,1266,832,485,2001,1608,3994,158,49272,2599,1358,552,497,3175,3753,435,1028,3176,1876,596,1376,466,196,72998,543,2657,56367,2699,1777,1035,3253,913,2890,1909,5995,2763,805,1302,1722,1215,2470,3418,172,4308,4370,1748,5218,1408,2804,3101,1242,1372,4979,342,4246,1094,2424,376,1729,2100,3033,1645,51662,151,1088,910,1396,225,55820,1690,265,2023,315,661,2701,4262,5502,186,1375,1285,2407,2572,50872,1674,1276,2105,3448,1275,2005,2294,3160,282,628,852,1262,246,256,355,1339,6870,529,673,4085,3911,252,914,5299,6378,515,2455,4720,105,1562,2161,261,1059,3000,1371,1172,2273,3255,1287,3717,2080,1405,2248,1129,5377,5679,3702,1921,4014,1060,3752,3785,1918,2694,1994,5481,475,1345,198,4447,724,1022,5902,1029,3256,2021,4848,2761,3039,1271,180,1663,2193,1120,1513,3082,2150,1042,51255,204,2078,3499,2336,2605,2968,2144,2288,2908,2393,4310,455,2058,89745,2915,5991,3535,849,2124,2641,2002,3624,1367,224,1882,3210,1347,1261,2723,1244,1907,616,1223,2490,4776,362,78499,216,2580,2140,1680,2826,44195,4361,1235,3386,81845,1321,4718,1884,4270,3361,968,2420,2581,1805,1831,838,2160,4816,47610,2717,41566,107,3148,719,1953,1962,2881,1644,647,372,4148,6218,3868,348,524,3967,2289,1027,3275,3363,5956,3247,2366,3300,2085,1231,1912,2501,3252,27773,3107,247,2722,4367,2087,56174,1378,34162,2949,4239,4321,1911,2944,81591,8361,1586,3108,4223,327,2872,1717,135,1077,2094,1373,1960,800,743,830,2064,2231,1957,4369,1175,2300,3362,6947,1037,3317,5903,922,1299,3005,4002,898,2686,4638,1104,1019,3354,3396,4446,80463,1952,2145,307,1020,2109,2391,2948,4701,1186,2394,2428,4874,1779,2096,2478,1982,1587,45499,1965,4734,3257,3798,8984,2795,1296,866,1253,1212,2405,3556,6188,237,2431,44665,4641,5971,532,1025,2421,2232,379,1385,1409,4016,2125,2423,1292,41569,88125,413,799,3424,546,1591,53125,2010,81834,1416,52973,232,1245,4344,215,222,3745,5388,1086,116797,748,481,954,1449,5945,3285,3809,4128,1284,3100,1801,2369,3450,5872,1945,1103,1438,2378,3969,2046,2067,2384,637,2371,2739,5630,2642,3504,91500,1185,1350,2335,2688,87232,5673,1254,1476,3697,4235,92259,1100,2137,930,4855,1125,2402,4901,1635,3087,3157,2243,5810,52722,5507,1256,1894,2803,8972,112852,207,2186,2986,3704,3254,3827,994,1959,31696,4890,6157,6281,1619,2788,5219,59615,308,1091,109374,1955,4105,5254,2013,382,2126,2467,765,836,2410,2471,2600,361,381,1041,926,55765,1388,374,45517,96610,2004,3526,419,2805,2991,3168,4069,383,538,3435,37729,519,2133,147,915,1892,3104,3264,41997,928,3246,3681,6541,535,1049,1620,140,1556,2053,3638,38061,3882,428,540,1248,98809,1217,1792,2857,2082,3534,2713,2792,85414,1678,4015,6287,3360,514,2528,2672,82459,3186,3683,737,1614,5444,1224,3825,4499,4865,707,3328,1344,2139,2338,3893,101,4007,86332,1209,1711,1958,1537,5617,3098,3198,3083,933,2993,3037,2664,88129,96079,249,1588,2720,3159,4489,955,1687,2333,3593,48738,905,1227,2413,2841,46723,1033,262,314,3298,4639,47099,371,951,1237,3178,3984,91658,144,164,56757,2009,3744,53000,1956,3173,1013,1464,36517,84152,1348,3271,40629,3735,3452,7325,94,1297);
        }else{
            doNotAddList = new ArrayList<>();
        }
    }
    
    /**
     * Reads rating files.
     */
    private
            void readRatingFile() {
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.RATING_FILE_PATH));)
        {
            String line;
            line = reader.readLine();
            while (line.contains("user")) {
                line = reader.readLine();
            }
            String[] tokens;
            int maxUserId = -1;
            int maxItemId = -1;
            float maxRating = -1;
            float minRating = 10000000;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.RATING_FILE_SEPERATOR);
                final float rating = Float.parseFloat(tokens[2]);
                final int userId = Integer.parseInt(tokens[0]);
                if (userId > maxUserId) {
                    maxUserId = userId;
                }
                final int itemId = Integer.parseInt(tokens[1]);
                if(doNotAddList.contains(itemId)){
                    line = reader.readLine();
                    continue;
                }
                if (itemId > maxItemId) {
                    maxItemId = itemId;
                }

                if (rating > maxRating) {
                    maxRating = rating;
                    Globals.setMaxRating(maxRating);
                }
                if (rating < minRating) {
                    minRating = rating;
                    Globals.setMinRating(minRating);
                }
                if (this.dataModel.getUser(userId) != null) {
                    this.dataModel.getUser(userId).addItemRating(itemId,
                            rating);
                } else {
                    final User user = new User(userId);
                    user.addItemRating(itemId, rating);
                    this.dataModel.addUser(user);
                }
                if (this.dataModel.getItem(itemId) != null) {
                    this.dataModel.getItem(itemId).addUserRating(userId,
                            rating);
                } else {
                    final Item item = new Item(itemId);
                    item.addUserRating(userId, rating);
                    this.dataModel.addItem(item);
                }
                this.dataModel.addRating(new Rating(userId, itemId, rating));
                line = reader.readLine();
            }
            Globals.setMaxNumberOfUsers(maxUserId);
            Globals.setMaxNumberOfItems(maxItemId);
            reader.close();
            LOG.info("Rating Data loaded.");
        } catch (final Exception exception) {
            LOG.error("Can not load rating file: " + Globals.RATING_FILE_PATH);
            LOG.error(exception);
            System.exit(1);
        }
    }

    /**
     * Reads low level file and parse it. The format of low level file should be
     * like this: ItemId,feature1,feature2,.... Features should be float
     */
    private
            void readLowLevelFile() {
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.LOW_LEVEL_FILE_PATH));)
        {
            String line;
            line = reader.readLine();
            while (line.contains("#")) {
                line = reader.readLine();
            }
            String[] tokens;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.LOW_LEVEL_FILE_SEPERATOR);
                final FloatArrayList features = new FloatArrayList();
                final int itemId = Integer.parseInt(tokens[0]);
                if(doNotAddList.contains(itemId)){
                    line = reader.readLine();
                    continue;
                }
                for (int i = 1; i < tokens.length; i++) {
                    try {
                        features.add(Float.parseFloat(tokens[i]));
                    } catch (final NumberFormatException exception) {
                        LOG.error(
                                "Can not convert low level feature to numbers."
                                        + exception);
                        throw exception;
                    }
                }
                final Item item = new Item(itemId);
                item.setLowLevelFeature(features);
                this.dataModel.addItem(item);
                line = reader.readLine();
            }
            reader.close();
            LOG.info("Low Level file loaded.");
        } catch (final Exception exception) {
            LOG.error("Can not load low level feature file: "
                    + Globals.LOW_LEVEL_FILE_PATH);
            LOG.error(exception);
            System.exit(1);
        }
    }

    /**
     * Reads genre file and parse it. Genres are presented as boolean vector The
     * format of genre file should be like this: ItemId,0,1,0,.... which 1 means
     * the movie is in the corresponding genre nad 0 means not
     * 
     */
    private
            void readGenreFile() {
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.GENRE_FILE_PATH));)
        {
            String line;
            line = reader.readLine();
            while (line.contains("#")) {
                line = reader.readLine();
            }
            String[] tokens;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.GENRE_FILE_SEPERATOR);
                final FloatArrayList features = new FloatArrayList();
                final int itemId = Integer.parseInt(tokens[0]);
                if(doNotAddList.contains(itemId)){
                    line = reader.readLine();
                    continue;
                }
                for (int i = 1; i < tokens.length; i++) {
                    try {
                        features.add(Float.parseFloat(tokens[i]));
                    } catch (final NumberFormatException exception) {
                        LOG.error("Can not convert genre to numbers."
                                + exception);
                        throw exception;
                    }
                }

                if (this.dataModel.getItem(itemId) == null) {
                    final Item item = new Item(itemId);
                    item.setGenres(features);
                    this.dataModel.addItem(item);
                } else {
                    this.dataModel.getItem(itemId).setGenres(features);
                }

                line = reader.readLine();
            }
            reader.close();
            LOG.info("Genre file loaded.");
        } catch (final Exception exception) {
            LOG.error("Can not load genre file: " + Globals.GENRE_FILE_PATH);
            LOG.error(exception);
            System.exit(1);
        }
    }

    /**
     * Reads tag file and parse it. The format of tag file should be like this:
     * ItemId,tag1,tag2,tag3,.... Tags are String. This function just read tags
     * and does not handle stemming, stopping word removal,...
     */
    private
            void readTagFile() {
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.TAG_FILE_PATH));)
        {
            String line;
            line = reader.readLine();
            while (line.contains("#")) {
                line = reader.readLine();
            }
            String[] tokens;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.TAG_FILE_SEPERATOR);
                final ObjectSet<String> features = new ObjectOpenHashSet<>();
                final int itemId = Integer.parseInt(tokens[0]);
                if(doNotAddList.contains(itemId)){
                    line = reader.readLine();
                    continue;
                }
                for (int i = 1; i < tokens.length; i++) {
                    features.add(tokens[i]);
                }

                if (this.dataModel.getItem(itemId) == null) {
                    final Item item = new Item(itemId);
                    item.setTags(features);
                    this.dataModel.addItem(item);
                } else {
                    this.dataModel.getItem(itemId).setTags(features);
                }

                line = reader.readLine();
            }
            reader.close();
            LOG.info("Tag file loaded.");
        } catch (final Exception exception) {
            LOG.error("Can not load tag file: " + Globals.TAG_FILE_PATH);
            LOG.error(exception);
            System.exit(1);
        }
    }

    /**
     * Main function which reads the files and returns {@link DataModel}
     * 
     * @return {@link DataModel}
     */
    public
            DataModel readData() {
        boolean hasContent = false;
        if (Globals.LOW_LEVEL_FILE_PATH != null
                && !Globals.LOW_LEVEL_FILE_PATH.isEmpty())
        {
            hasContent = true;
            this.readLowLevelFile();
        }
        if (Globals.GENRE_FILE_PATH != null
                && !Globals.GENRE_FILE_PATH.isEmpty())
        {
            hasContent = true;
            this.readGenreFile();
        }
        if (Globals.TAG_FILE_PATH != null && !Globals.TAG_FILE_PATH.isEmpty()) {
            hasContent = true;
            this.readTagFile();
        }
        if (hasContent) {
            this.readRatingFileBaseOnExisintItems();
        } else {
            this.readRatingFile();
        }
        return dataModel;
    }

    /**
     * Reads rating file and parse it. Rating file format should be like this:
     * userId,ItemId,Rating This function ignore all the ratings which does not
     * have any feature (lowlevel,genre,tag)
     */
    private
            void readRatingFileBaseOnExisintItems() {
        try (final BufferedReader reader = new BufferedReader(
                new FileReader(Globals.RATING_FILE_PATH));)
        {
            String line;
            line = reader.readLine();
            while (line.contains("#")) {
                line = reader.readLine();
            }
            String[] tokens;
            int maxUserId = -1;
            int maxItemId = -1;
            float maxRating = -1;
            float minRating = 10000000;
            while (line != null) {
                if (line.trim().startsWith("//")) {
                    line = reader.readLine();
                    continue;
                }
                tokens = line.split(Globals.RATING_FILE_SEPERATOR);
                final float rating = Float.parseFloat(tokens[2]);
                final Integer userId = Integer.parseInt(tokens[0]);
                final Integer itemId = Integer.parseInt(tokens[1]);
                if(doNotAddList.contains(itemId)){
                    line = reader.readLine();
                    continue;
                }
                if (this.dataModel.getItem(itemId) != null) {

                    if (userId > maxUserId) {
                        maxUserId = userId;
                    }

                    if (itemId > maxItemId) {
                        maxItemId = itemId;
                    }

                    if (maxRating < rating) {
                        maxRating = rating;
                        Globals.setMaxRating(maxRating);
                    }
                    if (minRating > rating) {
                        minRating = rating;
                        Globals.setMinRating(minRating);
                    }

                    if (this.dataModel.getUser(userId) != null) {
                        this.dataModel.getUser(userId).addItemRating(itemId,
                                rating);
                    } else {
                        final User user = new User(userId);
                        user.addItemRating(itemId, rating);
                        this.dataModel.addUser(user);
                    }
                    this.dataModel.getItem(itemId).addUserRating(userId,
                            rating);
                    this.dataModel
                            .addRating(new Rating(userId, itemId, rating));
                }
                line = reader.readLine();
            }
            Globals.setMaxNumberOfItems(maxItemId);
            Globals.setMaxNumberOfUsers(maxUserId);
            reader.close();
            LOG.info("Related Rating Data loaded.");
        } catch (final Exception exception) {
            LOG.error("Can not load rating file : " + Globals.RATING_FILE_PATH);
            LOG.error(exception);
            System.exit(1);
        }
    }
}
