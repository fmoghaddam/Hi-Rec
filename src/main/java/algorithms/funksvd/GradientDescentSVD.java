package algorithms.funksvd;

import java.util.Random;

import model.Globals;
import util.StaticFunctions;

/**
 * Calculates the p and q matrices based on gradient descent.
 * The code based is from Recommneder101: http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public final class GradientDescentSVD {

    private final Random random = StaticFunctions.random;
    private final double learningRate = Globals.LEARNING_RATE_FOR_FUNKSVD;
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
    public GradientDescentSVD(final int m,final int n,final int k,final double defaultValue) {
	this(m, n, k, defaultValue, r);
    }

    public GradientDescentSVD(final int m,final int n,final int k,final double defaultValue,final double noise) {
	this.k = k;

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
    public double getDotProduct(final int i,final int j) {
	double result = 0.0;
	double[] leftVectorI = leftVector[i];
	double[] rightVectorJ = rightVector[j];
	for (int k = 0; k < this.k; k++) {
	    result += leftVectorI[k] * rightVectorJ[k];
	}
	return result;
    }

    /**
     * Training iteration
     * 
     * @param i
     * @param j
     * @param k
     * @param value
     */
    public void train(final int i,final int j,final int k,final double value) {
	double err = value - getDotProduct(i, j);
	double[] leftVectorI = leftVector[i];
	double[] rightVectorJ = rightVector[j];
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
