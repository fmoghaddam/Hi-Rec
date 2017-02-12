package algorithms.fm;

/**
 * A specialized Version of the generic DVector for the primitive data type. Same functionality. Better performance (no autoboxing).
 */
public class DVectorInt {

	public int dim;
	private int[] _payload;

	public int get(int rowIndex) {
		return _payload[rowIndex];
	}

	public void setSize(int numRows) {
		dim = numRows;
		_payload = new int[numRows];
	}

	public void init(int value) {
		for(int i = 0; i<dim;i++){
			_payload[i] = value;
		}
	}

	public void set(int row_id, int value) {
		_payload[row_id] = value;
	}

}
