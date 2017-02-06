package controller.similarity;

import java.util.Map.Entry;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.log4j.Logger;

import interfaces.SimilarityInterface;
import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;
import model.DataModel;
import model.Globals;

/**
 * Calculate rating similarity between items on demand
 * 
 * @author FBM
 *
 */
public final class RatingSimilarityRepository
implements SimilarityInterface
{

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
	.getLogger(RatingSimilarityRepository.class.getCanonicalName());
	/**
	 * Train data
	 */
	private final DataModel dataModel;

	/**
	 * Constructor
	 * 
	 * @param dataModel
	 */
	public RatingSimilarityRepository(
			final DataModel dataModel)
	{
		this.dataModel = dataModel;
	}

	/*
	 * @see interfaces.SimilarityInterface#getItemSimilairty(int, int)
	 */
	@Override
	public
	Float getItemSimilairty(
			final int itemId1, final int itemId2)
	{
		switch (Globals.SIMILAIRTY_FUNCTION) {
		case COSINE:
			return calculateItemCosineSimilarity(itemId1, itemId2);
		case PEARSON:
			return calculateItemPearsonSimilarity(itemId1, itemId2);
		default:
			return calculateItemCosineSimilarity(itemId1, itemId2);
		}
	}

	/**
	 * Calculate Pearson correlation between two items
	 * 
	 * @param itemId1
	 * @param itemId2
	 * @return Pearson correlation of two items if they exist in train dataset,
	 *         O.W. NaN
	 */
	private
	Float calculateItemPearsonSimilarity(
			int itemId1, int itemId2)
	{
		if (this.dataModel.getItem(itemId1) != null
				&& this.dataModel.getItem(itemId2) != null)
		{
			final double[] item1List = this.dataModel.getItem(itemId1)
					.getUserRatedAsArray();
			final double[] item2List = this.dataModel.getItem(itemId2)
					.getUserRatedAsArray();
			return (float)new PearsonsCorrelation().correlation(item1List,
					item2List);
		} else {
			return Float.NaN;
		}
	}

	/**
	 * Calculate Cosine similarity between two items
	 * 
	 * @param itemId1
	 * @param itemId2
	 * @return Cosine similarity of two items if they exist in train dataset,
	 *         O.W. NaN
	 */
	private
	Float calculateItemCosineSimilarity(
			final int itemId1, final int itemId2)
	{
		if (this.dataModel.getItem(itemId1) != null
				&& this.dataModel.getItem(itemId2) != null)
		{

			final Int2FloatLinkedOpenHashMap item1List = this.dataModel
					.getItem(itemId1).getUserRated();
			final Int2FloatLinkedOpenHashMap item2List = this.dataModel
					.getItem(itemId2).getUserRated();

			float dotProduct = 0;
			float normA = 0;
			float normB = 0;
			if (item1List.entrySet().size() <= item2List.entrySet().size()) {
				for (final Entry<Integer, Float> entry: item1List.entrySet()) {

					final Integer key = entry.getKey();
					final Float value = entry.getValue();
					if (item2List.containsKey(key)) {
						dotProduct += value * item2List.get((int)key);
						normA += value * value;
						normB += item2List.get((int)key) * item2List.get((int)key);
					}
				}
			} else {
				for (final Entry<Integer, Float> entry: item2List.entrySet()) {
					final Integer key = entry.getKey();
					final Float value = entry.getValue();
					if (item1List.containsKey(key)) {
						dotProduct += value * item1List.get((int)key);
						normA += value * value;
						normB += item1List.get((int)key) * item1List.get((int)key);
					}
				}
			}
			if (dotProduct == 0) {
				return Float.NaN;
			}
			return (float)((dotProduct
					/ (Math.sqrt(normA) * Math.sqrt(normB))));
		} else {
			return Float.NaN;
		}
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
	Float calculateUserCosineSimilarity(
			final int userId1, final int userId2)
	{
		if (this.dataModel.getUser(userId1) != null
				&& this.dataModel.getUser(userId2) != null)
		{

			final Int2FloatLinkedOpenHashMap user1List = this.dataModel
					.getUser(userId1).getItemRating();
			final Int2FloatLinkedOpenHashMap user2List = this.dataModel
					.getUser(userId2).getItemRating();

			float dotProduct = 0;
			float normA = 0;
			float normB = 0;
			if (user1List.entrySet().size() <= user2List.entrySet().size()) {
				for (final Entry<Integer, Float> entry: user1List.entrySet()) {

					final Integer key = entry.getKey();
					final Float value = entry.getValue();
					if (user2List.containsKey(key)) {
						dotProduct += value * user2List.get((int)key);
						normA += value * value;
						normB += user2List.get((int)key) * user2List.get((int)key);
					}
				}
			} else {
				for (final Entry<Integer, Float> entry: user2List.entrySet()) {
					final Integer key = entry.getKey();
					final Float value = entry.getValue();
					if (user1List.containsKey(key)) {
						dotProduct += value * user1List.get((int)key);
						normA += value * value;
						normB += user1List.get((int)key) * user1List.get((int)key);
					}
				}
			}
			if (dotProduct == 0) {
				return Float.NaN;
			}
			return (float)((dotProduct
					/ (Math.sqrt(normA) * Math.sqrt(normB))));
		} else {
			return Float.NaN;
		}
	}
	
	private
	Float calculateUserPearsonSimilarity(
			int userId1, int userId2)
	{
		if (this.dataModel.getUser(userId1) != null
				&& this.dataModel.getUser(userId2) != null)
		{
			final double[] user1List = this.dataModel.getUser(userId1)
					.getRatingsInFullSizeArray();
			final double[] user2List = this.dataModel.getUser(userId2)
					.getRatingsInFullSizeArray();
			return (float)new PearsonsCorrelation().correlation(user1List,
					user2List);
		} else {
			return Float.NaN;
		}
	}
}
