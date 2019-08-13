package metrics;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import interfaces.ListEvaluation;
import model.DataModel;
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
        final List<Integer> userOriginalList = user.getItemRating().entrySet()
                .stream().filter(p -> p.getValue() >= Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING)
                .map(p -> p.getKey()).collect(Collectors.toList());
        
        float truePositive = 0;
        int listLengthThreshold = 0;
        for (final Entry<Integer, Float> entry: list.entrySet()) {
            if (listLengthThreshold>=Globals.AT_N) {
                break;
            }
//            if(entry.getValue()<Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING){
//            	break;
//            }
            if (user.getItemRating().containsKey(entry.getKey())) {
                if (user.getItemRating().get((int)entry.getKey()) >= Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING &&
                		entry.getValue()>=Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING ) {
                    truePositive++;
                }
                listLengthThreshold++;
            }
        }
        
        recall = (recall + truePositive / userOriginalList.size());
        counter++;

    }

    /*
     * @see interfaces.ListEvaluation#getEvaluationResult()
     */
    @Override
    public
            float getEvaluationResult() {
        final float result = recall / counter;
        if(Float.isNaN(result)){
            return 0;
        }
        return result;
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
        return 14;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public
            boolean equals(
                    Object obj)
    {
        if(obj==null){
            throw new IllegalArgumentException("Obj is null");
        }
        if (this.toString().equals(obj.toString())) {
            return true;
        } else {
            return false;
        }
    }

	@Override
	public void setTrainData(DataModel trainData) {
		// Empty function
		
	}
}
