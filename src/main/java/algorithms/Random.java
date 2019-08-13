package algorithms;

import interfaces.AbstractRecommender;
import model.DataModel;
import model.Item;
import model.User;
import util.StatisticFunctions;

public class Random extends AbstractRecommender {

	private static final long serialVersionUID = -5700507438800148672L;

	@Override
	public Float predictRating(User user, Item item) {
		return (float)StatisticFunctions.generateRandomInt(1, 5);		
	}

	@Override
	public void train(DataModel trainData) {
		this.trainDataModel = trainData;
	}

	@Override
	public boolean isSimilairtyNeeded() {
		return false;
	}

}
