package algorithms.fm;

import java.util.Random;

/**
 * A class that centralizes random number generation and also provides the ability to make a random run reusable for debugging purposes.
 */
public class Randomness {
    private Random _rand = new Random();
    /**
     * See Javadoc of Random.nextGaussian()
     * @return
     */
    double nextGaussian() {
    	return _rand.nextGaussian();
    }
    
    /**
     * Retrieves a pseudo random double value between 0 and 1
     * @return A random double
     */
    double nextDouble(){
        return ((double)_rand.nextInt(Integer.MAX_VALUE-1))/(Integer.MAX_VALUE);
    }
}
