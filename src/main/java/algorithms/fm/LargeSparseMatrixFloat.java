package algorithms.fm;

/**
 * A simple sparse matrix that holds float values. All used methods are overwritten by child class. In the original there was also a HDD cached subclass, which was not ported to date.
 */
public abstract class LargeSparseMatrixFloat {
    
	public abstract void begin();
    public abstract boolean end();
    public abstract void next();
    public abstract int getRowIndex();
    public abstract int getNumRows();
    public abstract int getNumCols();
    public abstract long getNumValues();
    public abstract sparse_rowFloat getRow();
    /**
     * This method can be used for debugging purposes or to reduce memory pressure. It saves a matrix to the hard disk.
     * @param filename The file name, to which the matrix is to be saved.
     */
    public abstract void saveToTextFile(String filename);
}
