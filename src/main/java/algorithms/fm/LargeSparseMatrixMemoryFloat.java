package algorithms.fm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple sparse matrix that holds float values and stores them in memory.
 */
public class LargeSparseMatrixMemoryFloat extends LargeSparseMatrixFloat{
    int index;
    public DVectorSparse_rowFloat data;
    public long num_values;
    public int num_cols;

    public LargeSparseMatrixMemoryFloat() {
        data = new DVectorSparse_rowFloat();
    }
    
    @Override
    public void begin() { index = 0; };
    @Override
    public boolean end() { return index >= data.dim; }
    @Override
    public void next() { index++;}
    @Override
    public sparse_rowFloat getRow() { 
        return data.get(index); 
    };
    @Override
    public int getRowIndex() { return index; };
    @Override
    public int getNumRows() { return data.dim; };
    @Override
    public int getNumCols() { return num_cols; };
    @Override
    public long getNumValues() { return num_values; };

    @Override
    public void saveToTextFile(String filename) {
       PrintWriter out = null;
        try {
            out = new PrintWriter(filename);
            for (begin(); !end(); next()) {
                    for (int i = 0; i < getRow().size; i++) {
                            out.print(getRow().data[i].id + ":" + getRow().data[i].value);
                            if ((i+1) < getRow().size) {
                                    out.print(" ");
                            } else {
                                    out.print("\n");
                            }
                    }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LargeSparseMatrixMemoryFloat.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }

    }
}
