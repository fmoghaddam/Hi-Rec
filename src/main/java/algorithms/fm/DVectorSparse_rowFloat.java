package algorithms.fm;

/**
 * A specialized Version of the generic DVector. Same functionality. Better performance (no autoboxing).
 */
public class DVectorSparse_rowFloat {

	public int dim;
	
	private sparse_rowFloat[] _payload;

	public sparse_rowFloat get(int index) {
		return _payload[index];
	}

	public void setSize(int size) {
		dim = size;
		_payload = new sparse_rowFloat[size];
	}
	
	public void init(){
		for(int i = 0 ; i< dim;i++){
	        _payload[i] = new sparse_rowFloat();
	    }
	}
}
