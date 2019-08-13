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
 * @author Admin
 *
 */
public class MAP
implements ListEvaluation
{

    private float n = 0;
    private float sumOfAPs = 0;

    /* (non-Javadoc)
     * @see interfaces.ListEvaluation#addRecommendations(model.User, java.util.Map)
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
        if (list.size() == 0) {
            return;
        }

        float truePositive = 0;
        int listLengthThreshold = 0;
        float sum = 0;
        for (final Entry<Integer, Float> entry: list.entrySet()) {
            if (listLengthThreshold>=Globals.AT_N) {
                break;
            }     
//            if(entry.getValue()<Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING){
//            	break;
//            }
            if (user.getItemRating().containsKey(entry.getKey())) {
            	listLengthThreshold++;
                if(user.getItemRating().get((int)entry.getKey())>=Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING &&
                		entry.getValue()>=Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING ) {
                    truePositive++;
                    sum+=(truePositive/listLengthThreshold)*1.0;
                }
            }
        }
        final float min = Math.min(truePositive, Globals.AT_N);
        if(min!=0){
            sum = sum/min;
        }
        sumOfAPs+=sum;
        n++;
    }

    /* (non-Javadoc)
     * @see interfaces.ListEvaluation#getEvaluationResult()
     */
    @Override
    public
    float getEvaluationResult() {
        return sumOfAPs/n;
    }

    @Override
    public
    String toString() {
        return "MAP";
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
        //Empty function
    }

}
