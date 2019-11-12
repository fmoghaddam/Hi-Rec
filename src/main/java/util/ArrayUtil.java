package util;

import java.util.Arrays;

/**
 * Contains all the functionality needed for arrays
 *
 * @author FBM
 */
public class ArrayUtil {

    /**
     * Concatenate two arrays into one array
     *
     * @param first
     * @param rest
     * @return
     */
    public static double[] concatAll(
            double[] first, double[]... rest) {
        int totalLength = first.length;
        for (double[] array : rest) {
            totalLength += array.length;
        }
        final double[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (double[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
