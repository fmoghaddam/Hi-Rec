package algorithms.fm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple high level matrix class, for double values.
 */
public class DMatrixDouble {
    public double[][] value;

    /**
     * Retrieves a specific value from the matrix.
     * @param idx Horizontal index.
     * @param idy Vertical index.
     * @return The value at the specific position.
     */
    public double get(int idx, int idy) {
        return value[idx][idy];
    }
    
    /**
     * This method initializes the whole matrix with random values. Current content is lost. Sizes have to be set beforehand.
     * @param mean The mean of the random values that are to be inserted into the matrix.
     * @param stdev The standard deviation from the mean of the random values that are to be inserted into the matrix.
     */
    public void init(double mean, double stdev) {	
            for (int i_1 = 0; i_1 < value.length; i_1++) {
                    for (int i_2 = 0; i_2 < value[i_1].length; i_2++) {
                            value[i_1][i_2] = StaticFunctions.ran_gaussian(mean, stdev);
                    }
            }
    }

    /**
     * This method initializes the whole matrix with the same value. Current content is lost. Sizes have to be set beforehand.
     * @param initVal The value that is to be inserted into every cell of the matrix.
     */
    public void init(double initVal) {
        for(int x = 0; x < value.length; x++){
            for(int y = 0; y < value[x].length; y++){
                value[x][y] = initVal;
            }
        }
    }

    /**
     * Set a specific value in the array.
     * @param idx Horizontal index.
     * @param idy Vertical index.
     * @param value The value which is to be set.
     */
    public void set(int idx, int idy, double value) {
        this.value[idx][idy] = value;
    }

    /**
     * Resets the size of the array. Current content is lost.
     * @param idx Horizontal dimension.
     * @param idy Vertical dimension.
     */
    public void setSize(int idx, int idy) {
        value = new double[idx][idy];
    }
    
    /**
     * This method is used to export the matrix data in a libfm compatible text format. The exported data can be imported by the C++ implementation of libfm for debugging purposes.
     * @param filename Name of the file to which is to be exported.
     */
    public void save(String filename) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(filename);
            for (int i_1 = 0; i_1 < this.value.length; i_1++) {
                    for (int i_2 = 0; i_2 < this.value[i_1].length; i_2++) {
                            if (i_2 > 0) {
                                    out.print("\t");
                            }
                            out.print(value[i_1][i_2]);
                    }
                    out.println();
            }
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DMatrixDouble.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }

    /**
     * This method is used to load the matrix from a libfm formatted text file.
     * @param filename File name from where to load the matrix data.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void load(String filename) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
           lines.add(line);
        }
        br.close();
        value = new double[lines.size()][];
        for (int i_1 = 0; i_1 < this.value.length; i_1++) {
            line = lines.get(i_1).trim();
            String[] split = line.split("\t");
            value[i_1] = new double[split.length];
            for (int i_2 = 0; i_2 < this.value[i_1].length; i_2++) {
                        value[i_1][i_2] = Double.parseDouble(split[i_2]);
            }
        }
    }
}
