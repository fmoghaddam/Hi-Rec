/**
 * 
 */
package metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import controller.similarity.LowLevelSimilarityRepository;
import interfaces.ListEvaluation;
import interfaces.SimilarityInterface;
import model.DataModel;
import model.Globals;
import model.User;

/**
 * @author FBM
 *
 */
public class DiversityLowLevel
        implements ListEvaluation
{

    private SimilarityInterface similarityRepository;
    private float diversityValue;
    private int n;
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
        int listLengthThreshold = 0;
        final List<Integer> hitList = new ArrayList<>();
        for (final Entry<Integer, Float> entry: list.entrySet()) {
            if (listLengthThreshold>=Globals.AT_N) {
                break;
            }
            hitList.add(entry.getKey());
            listLengthThreshold++;
        }
        float sum = 0;
        for(int i=0;i<hitList.size();i++){
            for(int j=i+1;j<hitList.size();j++){
                int itemId1 = hitList.get(i);
                int itemId2 = hitList.get(j);
                final Float itemSimilairty = similarityRepository.getItemSimilairty(itemId1, itemId2);
                if(!Float.isNaN(itemSimilairty)){
                    sum +=2*(1-itemSimilairty);
                }
            }
        }
        diversityValue += (sum*1.0)/(Globals.AT_N*(Globals.AT_N-1));
        n++;
    }

    /* (non-Javadoc)
     * @see interfaces.ListEvaluation#getEvaluationResult()
     */
    @Override
    public
            float getEvaluationResult() {
        final float result = (float)(diversityValue/n*1.0);
        if(Float.isNaN(result)){
            return 0;
        }
        return result;
    }
    
    /*
     * (non-Javadoc)
     * @see interfaces.ListEvaluation#setTrainData(model.DataModel)
     */
    @Override
    public
            void setTrainData(
                    DataModel trainData)
    {
        this.similarityRepository = new LowLevelSimilarityRepository(trainData);        
    }
    
    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public
            int hashCode() {
        return 5640;
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
        return "DiversityLowLevel";
    }
}
