package algorithms.fm;

import java.io.File;

/**
 * A class that contains static methods, that existed outside of classes in the
 * original implementation.
 * 
 */
public class StaticFunctions {
	///Everything that concerns randomness is handled by the Randomness class.
	private static Randomness _rand = new Randomness();

	/**
	 * See Javadoc for Random.ranGaussian() for more info.
	 * 
	 * @param mean
	 * @param stdev
	 * @return
	 */
	public static double ran_gaussian(double mean, double stdev) {
		if ((stdev == 0.0) || (Double.isNaN(stdev))) {
			return mean;
		} else {
			return mean + stdev * ran_gaussian();
		}
	}

	private static double ran_gaussian() {
		return _rand.nextGaussian();
	}

	/**
	 * Generates a uniform random double value between 0 and 1
	 * 
	 * @return
	 */
	private static double ran_uniform() {
		return _rand.nextDouble();
	}

	public static double ran_gamma(double alpha, double beta) {
		return ran_gamma(alpha) / beta;
	}

	public static double ran_gamma(double alpha) {
		if (alpha <= 0)
			throw new IllegalArgumentException("alpha has to be bigger than 0");
		if (alpha < 1.0) {
			double u;
			do {
				u = ran_uniform();
			} while (u == 0.0);
			return ran_gamma(alpha + 1.0) * Math.pow(u, 1.0 / alpha);
		} else {
			// Marsaglia and Tsang: A Simple Method for Generating Gamma
			// Variables
			double d, c, x, v, u;
			d = alpha - 1.0 / 3.0;
			c = 1.0 / Math.sqrt(9.0 * d);
			do {
				do {
					x = ran_gaussian();
					v = 1.0 + c * x;
				} while (v <= 0.0);
				v = v * v * v;
				u = ran_uniform();
			} while (
			///TODO Check use of binary operator copied ignorantly from c++
			(u >= (1.0 - 0.0331 * (x * x) * (x * x)))
					&& (Math.log(u) >= (0.5 * x * x + d
							* (1.0 - v + Math.log(v)))));
			return d * v;
		}
	}

	/**
	 * Checks if a file exists
	 * 
	 * @param filename
	 *            The name of the file
	 * @return If the file exists
	 */
	public static boolean fileexists(String filename) {
		return new File(filename).exists();
	}

	/**
	 * Used by classification. If needed it has to be implemented. For now a
	 * stub suffices.
	 * 
	 * @param p
	 * @return
	 */
	public static double cdf_gaussian(double p) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Used by classification. If needed it has to be implemented. For now a
	 * stub suffices.
	 * 
	 * @param p
	 * @return
	 */
	public static double ran_left_tgaussian(double d, double e, double f) {
		throw new UnsupportedOperationException();
	}

}
