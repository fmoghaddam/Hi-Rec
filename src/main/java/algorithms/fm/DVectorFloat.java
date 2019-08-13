package algorithms.fm;

/**
 * A specialized Version of the generic DVector for the primitive data type. Same functionality. Better performance (no autoboxing).
 */
public class DVectorFloat {

	public int dim;
	private float[] _payload;

	public float get(int rowIndex) {
		return _payload[rowIndex];
	}

	public void setSize(int numRows) {
		dim = numRows;
		_payload = new float[numRows];
	}

	public void init(float value) {
		for(int i = 0; i<dim;i++){
			_payload[i] = value;
		}
	}

	public void set(int row_id, float value) {
		_payload[row_id] = value;
	}

}
