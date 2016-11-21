package metrics;

import static org.junit.Assert.*;

import org.junit.Test;

import model.DataModel;
import model.User;

/**
 * Test class for {@link DiversityLowLevel}
 * @author FBM
 *
 */
public class DiversityLowLevelTest {

    @Test
    public
            void test() {
        final User user = TestDataGenerator.getUser1();
        final DataModel trainData = TestDataGenerator.getTrainData();
        final DiversityLowLevel diversityLowLevel = new DiversityLowLevel();
        diversityLowLevel.setTrainData(trainData);
        diversityLowLevel.addRecommendations(user,TestDataGenerator.generateList1());
        assertEquals(0.004, diversityLowLevel.getEvaluationResult(),0.001);
        diversityLowLevel.addRecommendations(user,TestDataGenerator.generateList1());
        assertEquals(0.004, diversityLowLevel.getEvaluationResult(),0.001);
        
    }

}
