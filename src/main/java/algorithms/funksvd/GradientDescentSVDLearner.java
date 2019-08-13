package algorithms.funksvd;

import java.util.Random;

import interfaces.Learner;
import util.StatisticFunctions;

/**
 * Calculates the p and q matrices based on gradient descent. The code based is
 * from Recommneder101:
 * http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public final class GradientDescentSVDLearner implements Learner{

	private final Random random = StatisticFunctions.random;
	private final double learningRate;
	/** Parameter used to prevent over-fitting. 0.02 is a good value. */
	private static final double K = 0.02;
	/** Random noise applied to starting values. */
	private static final double r = 0.005;

	private final int k;

	/** User singular vector. */
	private final double[][] leftVector;

	/** Item singular vector. */
	private final double[][] rightVector;

	/**
	 * @param m
	 *            number of columns
	 * @param n
	 *            number of rows
	 * @param k
	 *            number of features
	 * @param defaultValue
	 *            default starting values for the SVD vectors
	 */
	public GradientDescentSVDLearner(final int m, final int n, final int k, final double defaultValue, double learningRate) {
		this(m, n, k, defaultValue, r, learningRate);
	}

	private GradientDescentSVDLearner(final int m, final int n, final int k, final double defaultValue, final double noise,
			final double learningRate) {
		this.k = k;
		this.learningRate = learningRate;
		leftVector = new double[m][k];
		rightVector = new double[n][k];

		for (int i = 0; i < k; i++) {
			for (int j = 0; j < m; j++) {
				leftVector[j][i] = defaultValue + (random.nextDouble() - 0.5) * noise;
			}
			for (int j = 0; j < n; j++) {
				rightVector[j][i] = defaultValue + (random.nextDouble() - 0.5) * noise;
			}
		}
	}

	/**
	 * Calculate the dot product of two vectors
	 * 
	 * @param i
	 *            index for user vector
	 * @param j
	 *            index for the item vector
	 * @return the dot product
	 */
	@Override
	public Float getResult(final int i, final int j) {
		double result = 0.0;
		double[] leftVectorI = leftVector[i];
		double[] rightVectorJ = rightVector[j];
		for (int k = 0; k < this.k; k++) {
			result += leftVectorI[k] * rightVectorJ[k];
		}
		return (float) result;
	}

	/**
	 * Training iteration
	 * 
	 * @param i
	 * @param j
	 * @param k
	 * @param value
	 */
	@Override
	public void train(final int i, final int j, final int k, final double value) {
		final double err = value - getResult(i, j);
		final double[] leftVectorI = leftVector[i];
		final double[] rightVectorJ = rightVector[j];
		leftVectorI[k] += learningRate * (err * rightVectorJ[k] - K * leftVectorI[k]);
		rightVectorJ[k] += learningRate * (err * leftVectorI[k] - K * rightVectorJ[k]);
	}

	/**
	 * Returns the left vector (user vector)
	 * 
	 * @param user
	 * @return the latent vector weights
	 */
	public double[] getLeftVector(final int user) {
		return this.leftVector[user];
	}

}
