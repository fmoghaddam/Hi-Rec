package controller.similarity;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.log4j.Logger;

import interfaces.SimilarityInterface;
import model.DataModel;
import model.Globals;

/**
 * Calculate low level feature similarity between items on demand
 * 
 * @author FBM
 *
 */
public final class PersonalitySimilarityRepository
implements SimilarityInterface
{

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
	.getLogger(PersonalitySimilarityRepository.class.getCanonicalName());
	/**
	 * Train data
	 */
	private final DataModel dataModel;

	/**
	 * Constructor
	 * 
	 * @param dataModel
	 */
	public PersonalitySimilarityRepository(
			final DataModel dataModel)
	{
		this.dataModel = dataModel;
	}

	@Override
	public Float getUserSimilarity(int userId1, int userId2) {
		switch (Globals.SIMILAIRTY_FUNCTION) {
		case COSINE:
			return calculateUserCosineSimilarity(userId1, userId2);
		case PEARSON:
			return calculateUserPearsonSimilarity(userId1, userId2);
		default:
			return calculateUserCosineSimilarity(userId1, userId2);
		}
	}

	private
	Float calculateUserPearsonSimilarity(
			int userId1, int userId2)
	{
		if (this.dataModel.getUser(userId1) != null
				&& this.dataModel.getUser(userId2) != null)
		{
			final double[] user1Array = this.dataModel.getUser(userId1)
					.getPersonalityAsArray();
			final double[] user2Array = this.dataModel.getUser(userId2)
					.getPersonalityAsArray();

			return (float)new PearsonsCorrelation().correlation(user1Array,
					user2Array);

		} else {
			return Float.NaN;
		}
	}
	
	private
	Float calculateUserCosineSimilarity(
			final int userId1, final int userId2)
	{
		if (this.dataModel.getUser(userId1) != null
				&& this.dataModel.getUser(userId2) != null)
		{
			final double[] user1Array = this.dataModel.getUser(userId1)
					.getPersonalityAsArray();
			final double[] user2Array = this.dataModel.getUser(userId2)
					.getPersonalityAsArray();

			float dotProduct = 0;
			float normA = 0;
			float normB = 0;
			for (int i = 0; i < user1Array.length; i++) {
				dotProduct += user1Array[i] * user2Array[i];
				normA += user1Array[i] * user1Array[i];
				normB += user2Array[i] * user2Array[i];
			}
			if (dotProduct == 0) {
				return Float.NaN;
			}
			return (float)(dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));

		} else {
			return Float.NaN;
		}
	}

	@Override
	public Float getItemSimilairty(int itemId1, int itemId2) {
		// TODO Auto-generated method stub
		return null;
	}
}
