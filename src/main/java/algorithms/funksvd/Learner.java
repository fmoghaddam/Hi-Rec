/**
 *
 */
package algorithms.funksvd;

/**
 * @author Farshad Moghaddam
 *
 */
public interface Learner {

    /**
     * @param i
     * @param j
     * @param k
     * @param value
     */
    void
    train(
            int i, int j, int k, double value);

    /**
     * @param i
     * @param j
     * @return
     */
    double
    getResult(
            int i, int j);


}
