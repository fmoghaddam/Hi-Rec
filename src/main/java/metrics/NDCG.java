package metrics;

import java.util.Map;
import java.util.Map.Entry;

import interfaces.ListEvaluation;
import model.DataModel;
import model.Globals;
import model.User;

/**
 * @author FBM
 *
 */
public class NDCG
        implements ListEvaluation
{

    float ndcg = 0;
    long counter = 0;

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.ListEvaluation#addRecommendations(model.User,
     * java.util.Map)
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

        int listLengthThreshold = 0;
        float dcg = 0;
        final double log2 = Math.log(2);
        for (final Entry<Integer, Float> entry: list.entrySet()) {
            if (listLengthThreshold >= Globals.AT_N) {
                break;
            }
            if(entry.getValue()<Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING){
            	break;
            }
            if (user.getItemRating().containsKey(entry.getKey())) {
            	listLengthThreshold++;
                if (user.getItemRating().get((int)entry
                        .getKey()) >= Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING)
                {
                    dcg += (1.0 / (Math.log(listLengthThreshold+ 1) / log2));
                }
            }
        }

        final long count = user.getItemRating().entrySet()
                .stream().filter(p -> p
                        .getValue() >= Globals.MINIMUM_THRESHOLD_FOR_POSITIVE_RATING)
                .map(p -> p.getKey()).count();

        float idcg = 0;
        for (int i = 1; i <= count; i++) {
            idcg += (1.0 / (Math.log(i + 1) / log2));
        }

        ndcg += dcg / idcg;
        counter++;
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.ListEvaluation#getEvaluationResult()
     */
    @Override
    public
            float getEvaluationResult() {
        return ndcg / counter;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "NDCG";
    }
    
    @Override
    public
            int hashCode() {
        return 6;
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
