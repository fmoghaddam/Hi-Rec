package algorithms.implicitdatamatrixfactorization;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import interfaces.Learner;
import it.unimi.dsi.fastutil.ints.Int2FloatLinkedOpenHashMap;
import model.DataModel;
import util.StatisticFunctions;

/**
 * @author Farshad Moghaddam
 *
 */
public final class AlternatingLeastSquareLearner implements Learner{

    /**
     * Number of features
     */
    private int numberOfFeatures;
    /**
     * Number of iteration
     */
    private int numberOfIteration;
    /**
     * learning rate used in SGD
     */
    private final double regularizationCoefficient;
    private Map<Integer, Integer> userMap = new LinkedHashMap<>();
    private Map<Integer, Integer> itemMap = new LinkedHashMap<>();
    private final DataModel dataModel;
    private RealMatrix Y;
    private RealMatrix X;

    public AlternatingLeastSquareLearner(
            final DataModel dataModel,final double regularizationCoefficient, final int numberOfFeatures, final int numberOfIterations)
    {
        if(dataModel==null){
            throw new IllegalArgumentException("Data model is null");
        }
        this.dataModel = dataModel;
        this.numberOfFeatures = numberOfFeatures;
        this.regularizationCoefficient= regularizationCoefficient;
        this.numberOfIteration = numberOfIterations;
    }

    @Override
    public
    void train(
            int i, int j, int k, double value)
    {
        // TODO This function should be removed
    }

    @Override
    public
    	Float getResult(
            int userRealId, int itemRealId)
    {
        final Integer userId = userMap.get(userRealId);
        final Integer itemId = itemMap.get(itemRealId);
        if(userId==null || itemId==null){
            return Float.NaN;
        }else{
            final double[] row = X.getRow(userId);
            final double[] column = Y.getRow(itemId);
            final RealVector user = new ArrayRealVector(row.length);
            for (int i = 0; i < row.length; i++) {
                user.setEntry(i, row[i]);
            }

            final RealVector item = new ArrayRealVector(column.length);
            for (int i = 0; i < column.length; i++) {
                item.setEntry(i, column[i]);
            }
            return (float) user.dotProduct(item);
        }
    }

    public void learn(){
        int index = 0;
        for (final Integer userId: dataModel.getUsers().keySet()) {
            userMap.put(userId, index++);
        }
        X = MatrixUtils.createRealMatrix(userMap.size(), numberOfFeatures);
        for(int i=0;i<X.getRowDimension();i++){
            for(int j=0;j<X.getColumnDimension();j++){
                X.setEntry(i, j, StatisticFunctions.generateRandomNumber());
            }
        }

        index = 0;
        for (final Integer itemId: dataModel.getItems().keySet()) {
            itemMap.put(itemId, index++);
        }
        Y = MatrixUtils.createRealMatrix(itemMap.size(), numberOfFeatures);
        for(int i=0;i<Y.getRowDimension();i++){
            for(int j=0;j<Y.getColumnDimension();j++){
                Y.setEntry(i, j, StatisticFunctions.generateRandomNumber());
            }
        }
        for(int i=0;i<numberOfIteration;i++){
            calculateX();
//            System.err.println("X");
            calculateY();
//            System.err.println("Y");
        }
    }

    /**
     * 
     */
    private
    void calculateY() {
        final RealMatrix Xt = X.copy().transpose();
        final RealMatrix XtX = Xt.multiply(X);
        final RealMatrix I = MatrixUtils.createRealIdentityMatrix(numberOfFeatures).scalarMultiply(regularizationCoefficient);
        final RealMatrix XtXMinusI = XtX.add(I);
        final RealMatrix XtXMinusIInvese =  new LUDecomposition(XtXMinusI).getSolver().getInverse();
        final RealMatrix XtXMinusIInveseXt = XtXMinusIInvese.multiply(Xt); 
        for(Integer realItemID :itemMap.keySet()){
            final RealMatrix Pi = MatrixUtils.createRealMatrix(1, userMap.size());
            final Int2FloatLinkedOpenHashMap userRated = dataModel.getItem(realItemID).getUserRated();
            for(Entry<Integer, Float> c:userRated.entrySet()){
                final Integer k = userMap.get(c.getKey());
                if(k==null){
                    continue;
                }
                if(c.getValue()>3){
                    Pi.setEntry(0, k, 1);
                }else{
                    Pi.setEntry(0, k, 0);
                }
            }
            Y.setRowMatrix(itemMap.get(realItemID), Pi.multiply(XtXMinusIInveseXt.transpose()));
        }
    }


    private
    void calculateX() {
        final RealMatrix Yt = Y.copy().transpose();
        final RealMatrix YtY = Yt.multiply(Y);
        final RealMatrix I = MatrixUtils.createRealIdentityMatrix(numberOfFeatures).scalarMultiply(regularizationCoefficient);
        final RealMatrix YtYMinusI = YtY.add(I);
        final RealMatrix YtYMinusIInverse = new LUDecomposition(YtYMinusI).getSolver().getInverse();
        final RealMatrix YtYMinusIInverseYt =  YtYMinusIInverse.multiply(Yt);
        for(Integer realUserID :userMap.keySet()){
            final RealMatrix Pu = MatrixUtils.createRealMatrix(1, itemMap.size());
            final Int2FloatLinkedOpenHashMap itemRating = dataModel.getUser(realUserID).getItemRating();
            for(Entry<Integer, Float> c:itemRating.entrySet()){
                final Integer k = itemMap.get(c.getKey());
                if(c.getValue()>3){
                    Pu.setEntry(0, k, 1);
                }else{
                    Pu.setEntry(0, k, 0);
                }
            }
            X.setRowMatrix(userMap.get(realUserID), Pu.multiply(YtYMinusIInverseYt.transpose()));
        }
    }
}
