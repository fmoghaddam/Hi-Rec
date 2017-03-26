package interfaces;

import model.DataModel;

public interface DataSplitterInterface {

	void shuffle();

	DataModel getTrainData(int i);

	DataModel getTestData(int i);

}
