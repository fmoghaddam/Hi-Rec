package algorithms.fm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * A specialized Version of the generic DVector for the primitive data type. Same functionality. Better performance (no autoboxing).
 */
public class DVectorDouble {
	public int dim;
	private double[] _payload;

	public double get(int rowIndex) {
		return _payload[rowIndex];
	}

	public void setSize(int numRows) {
		dim = numRows;
		_payload = new double[numRows];
	}

	public void init(double value) {
		for(int i = 0; i<dim;i++){
			_payload[i] = value;
		}
	}
	
	public void set(int row_id, double value) {
		_payload[row_id] = value;
	}
	
	/**
     * Initializes the vector with a random numbers. Current values are lost. Size has to be set beforehand.
     * @param init_mean The mean of the random values that are to be assigned to the vector.
     * @param init_stdev The standard deviation from the mean for the random values.
     */
	public void init_normal(double init_mean, double init_stdev) {
        for (int i = 0; i < dim; i++) {
            _payload[i] =  StaticFunctions.ran_gaussian(init_mean, init_stdev) ;
        }
    }

	/**
     * Save the vector to a text file. One line in the file represents one item in the vector. This can be used for debugging purposes.
     * @param filename The name of the file to be filled with the data from the vector.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void save(String filename) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        for(int i = 0; i<dim;i++){
            writer.print(_payload[i] + "\n");
        }
        writer.close();
    }

	public double[] getArray() {
		return _payload;
	}
}
