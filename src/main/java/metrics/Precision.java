/**
 * 
 */
package metrics;

import java.util.Map;
import java.util.Map.Entry;

import interfaces.ListEvaluation;
import model.Globals;
import model.User;

/**
 * Precision
 * 
 * @author FBM
 *
 */
public final class Precision
        implements ListEvaluation
{

    float precision = 0;
    long counter = 0;

    /*
     * @see interfaces.ListEvaluation#addRecommendations(model.User,
     * java.util.List)
     */
    @Override
    public
            void addRecommendations(
                    final User user, final Map<Integer, Float> list)
    {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (list == null) {
            throw new IllegalArgumentException("Recommended list is null");
        }
        float truePositive = 0;
        float falsePositive = 0;
        int recCounter = 0;
        for (final Entry<Integer, Float> entry: list.entrySet()) {
            if (entry.getValue() < 4) {
                break;
            }
            if (user.getItemRating().containsKey(entry.getKey())) {
                recCounter++;
                if (user.getItemRating().get((int)entry.getKey()) >= 4) {
                    truePositive++;
                } else {
                    falsePositive++;
                }
            }
            if (recCounter == Globals.TOP_N) {
                break;
            }
        }
        if (truePositive == 0 && falsePositive == 0) {
            return;
        }
        precision = (precision + truePositive / (truePositive + falsePositive));
        counter++;
    }

    /*
     * @see interfaces.ListEvaluation#getEvaluationResult()
     */
    @Override
    public
            float getEvaluationResult() {
        return precision / counter;
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public
            int hashCode() {
        return 5;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public
            boolean equals(
                    Object obj)
    {
        if (this.toString().equals(obj.toString())) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "Precision";
    }

}
