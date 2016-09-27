package metrics;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import interfaces.ListEvaluation;
import model.Globals;
import model.Rating;
import model.User;

/**
 * Calculates the NDCG (normalized discounted cumulative gain) value. 
 * The code based is from Recommneder101:
 * http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
public final class NDCG
        implements ListEvaluation
{

    private double accumulatedNDCGValue = 0.0;
    private int count = 0;

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.ListEvaluation#getEvaluationResult()
     */
    @Override
    public
            float getEvaluationResult() {
        return ((float)accumulatedNDCGValue) / ((float)count);
    }

    /*
     * @see interfaces.ListEvaluation#addRecommendations(model.User,
     * java.util.List)
     */
    @Override
    public
            void addRecommendations(
                    User user, Map<Integer, Float> list)
    {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        if (list == null) {
            throw new IllegalArgumentException("Recommended list is null");
        }
        int depth = Math.min(Globals.TOP_N, list.size());

        double dcg = 0.0;
        int loopCount = 0;
        for (int item: list.keySet()) {
            if (user.getItemRating().keySet().contains(item)) {
                double a = Math.pow(2, user.getItemRating().get(item));
                double b = Math.log(2 + loopCount) / Math.log(2);
                loopCount++;
                dcg += a / b;
            }

            if (loopCount >= depth)
                break;
        }

        final List<Rating> ratings = user.getItemRating().entrySet().stream()
                .map(p -> new Rating(user.getId(), p.getKey(), p.getValue()))
                .collect(Collectors.toList());
        Collections.sort(ratings, new Comparator<Rating>() {
            @Override
            public
                    int compare(
                            Rating o1, Rating o2)
            {
                return Float.compare(o2.getRating(), o1.getRating());
            }
        });

        double ideal_dcg = 0.0;
        depth = loopCount;
        for (int i = 0; i < depth; i++) {
            double a = Math.pow(2, ratings.get(i).getRating());
            double b = Math.log(2 + i) / Math.log(2);
            ideal_dcg += a / b;
        }

        double nDCG = dcg / ideal_dcg;
        if (!Double.isNaN(nDCG)) {
            count++;
            accumulatedNDCGValue += nDCG;
        }
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public
            int hashCode() {
        return 2;
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
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "NDCG";
    }

}
