package metrics;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import interfaces.ListEvaluation;
import model.Globals;
import model.User;

/**
 * Recall
 * 
 * @author FBM
 *
 */
public final class Recall
        implements ListEvaluation
{

    float recall = 0;
    long counter = 0;

    /*
     * @see interfaces.ListEvaluation#addRecommendations(model.User,
     * java.util.Map)
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
        float falseNegative = 0;
        int recCounter = 0;
        final List<Integer> userOriginalList = user.getItemRating().entrySet()
                .stream().filter(p -> p.getValue() >= 4)
                .map(p -> p.getKey()).collect(Collectors.toList());
        for (final Entry<Integer, Float> entry: list.entrySet()) {
            if (userOriginalList.contains(entry.getKey())) {
                recCounter++;
                if (entry.getValue() >= 4) {
                    truePositive++;
                } else {
                    falseNegative++;
                }
            }
            if (recCounter == Globals.TOP_N) {
                break;
            }
        }

        if (truePositive == 0 && falseNegative == 0) {
            return;
        }
        recall = (recall + truePositive / (truePositive + falseNegative));
        counter++;

    }

    /*
     * @see interfaces.ListEvaluation#getEvaluationResult()
     */
    @Override
    public
            float getEvaluationResult() {
        return recall / counter;
    }

    @Override
    public
            String toString() {
        return "Recall";
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public
            int hashCode() {
        return 8;
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
}
