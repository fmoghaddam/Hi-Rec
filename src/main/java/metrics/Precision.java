/**
 * 
 */
package metrics;

import java.util.Map;
import java.util.Map.Entry;

import interfaces.ListEvaluation;
import model.DataModel;
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
        int listLengthThreshold = 0;
        for (final Entry<Integer, Float> entry: list.entrySet()) {
            if (listLengthThreshold>=Globals.AT_N) {
                break;
            }
            if (user.getItemRating().containsKey(entry.getKey())) {
                if (user.getItemRating().get((int)entry.getKey()) >= Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING) {
                    truePositive++;
                }
            }
            listLengthThreshold++;
        }
        precision = (precision + truePositive / Globals.AT_N);
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
        return 11;
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

	@Override
	public void setTrainData(DataModel trainData) {
		//Empty function
		
	}

}
