/**
 * 
 */
package util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import controller.DataLoader;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import model.DataModel;
import model.Item;

/**
 * This class generates Id of top popular items This ids could be used somewhere
 * else (e.g. DataLoader to remove popular items)
 * 
 * @author FBM
 */
public class TopPopularItemsUtil {

    /**
     * How many items should be reported?
     */
    private static final int SIZE = 800;

    /**
     * @param args
     */
    public static
    void main(
            String[] args)
    {
        final DataLoader loader = new DataLoader();
        final DataModel dataModel = loader.readData();
        dataModel.printStatistic();
        final Map<Integer, Integer> itemPopularityMap = new LinkedHashMap<>();
        for (Entry<Integer, Item> entry: dataModel.getItems().entrySet()) {
            itemPopularityMap.put(entry.getKey(),
                    entry.getValue().getUserRated().size());
        }
        final Map<Integer, Integer> sortByValueDescending = MapUtil
                .sortByValueDescending(itemPopularityMap);

        int counter = 0;
        for (Entry<Integer, Integer> entry: sortByValueDescending.entrySet()) {
            if (counter < SIZE) {
                System.err.print(entry.getKey() + ",");
                counter++;
            } else {
                break;
            }
        }
    }

    public static IntArrayList getTopPopularItems(final int numberOfItems,final DataModel dataModel){
        final Map<Integer, Integer> itemPopularityMap = new LinkedHashMap<>();
        for (Entry<Integer, Item> entry: dataModel.getItems().entrySet()) {
            itemPopularityMap.put(entry.getKey(),
                    entry.getValue().getUserRated().size());
        }
        final Map<Integer, Integer> sortByValueDescending = MapUtil
                .sortByValueDescending(itemPopularityMap);

        int counter = 0;
        final IntArrayList result = new IntArrayList();
        for (Entry<Integer, Integer> entry: sortByValueDescending.entrySet()) {
            if (counter < numberOfItems) {
                result.add(entry.getKey());
                counter++;
            } else {
                break;
            }
        }
        return result;
    }
}
