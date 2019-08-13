package interfaces;

import controller.DataLoaderException;
import model.DataModel;

public interface DataLoaderInterface {
	DataModel readData() throws DataLoaderException;
}
