/*
 * JLibFM
 *
 * Copyright (c) 2017, Jinbo Chen(gaterslebenchen@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the docume
 *    ntation and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUD
 * ING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN N
 * O EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR C
 * ONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR P
 * ROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 *  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBI
 *  LITY OF SUCH DAMAGE.
 */
package com.github.gaterslebenchen.libfm.core;

import com.github.gaterslebenchen.libfm.data.DataPointMatrix;
import com.github.gaterslebenchen.libfm.data.LibSVMDataProvider;
import com.github.gaterslebenchen.libfm.data.SparseEntry;
import com.github.gaterslebenchen.libfm.data.SparseRow;
import com.github.gaterslebenchen.libfm.tools.Debug;
import interfaces.AbstractRecommender;
import model.DataModel;
import model.Item;
import model.User;
import util.MapUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FactorizationMachine
        extends
        AbstractRecommender {
    private static final long serialVersionUID = 1L;

    public double[] m_sum, m_sum_sqr;
    public double w0;
    public double[] w;
    public DataPointMatrix v;
    public int num_attribute;
    public boolean k0, k1;
    public int num_factor;

    public double reg0;
    public double regw, regv;

    public double initstdev;
    public double initmean;

    public double learn_rate;
    public int numberOfIteration;

    public DataModel hirecModel;

    public FactorizationMachine() {
        num_factor = 0;
        initmean = 0;
        initstdev = 0.01;
        reg0 = 0.0;
        regw = 0.0;
        regv = 0.0;
        k0 = true;
        k1 = true;

        final Map<String, String> h1 = new HashMap<>();
        h1.put("NUMBER_OF_FEATURES_FOR_FM", "Number of latent factor");
        this.configurableParametersMap.put("num_factor", h1);

        final Map<String, String> h2 = new HashMap<String, String>();
        h2.put("NUMBER_OF_ITERATION_FOR_FM", "Number of iteration");
        this.configurableParametersMap.put("numberOfIteration", h2);

        final Map<String, String> h3 = new HashMap<String, String>();
        h3.put("LEARNING_RATE_FOR_FM", "Learning rate");
        this.configurableParametersMap.put("learn_rate", h3);
    }

    public double getLearn_rate() {
        return learn_rate;
    }

    public void setLearn_rate(double learn_rate) {
        this.learn_rate = learn_rate;
    }

    public int getNumberOfIteration() {
        return numberOfIteration;
    }

    public void setNumberOfIteration(int numberOfIteration) {
        this.numberOfIteration = numberOfIteration;
    }

    public int getNum_factor() {
        return num_factor;
    }

    public void setNum_factor(int num_factor) {
        this.num_factor = num_factor;
    }

    public void debug() {
        Debug.println("num_attributes=" + num_attribute);
        Debug.println("use w0=" + k0);
        Debug.println("use w1=" + k1);
        Debug.println("dim v =" + num_factor);
        Debug.println("reg_w0=" + reg0);
        Debug.println("reg_w=" + regw);
        Debug.println("reg_v=" + regv);
        Debug.println("init ~ N(" + initmean + "," + initstdev + ")");
    }

    public void init() {
        w0 = 0;
        w = new double[num_attribute];
        v = new DataPointMatrix(num_factor, num_attribute);
        Arrays.fill(w, 0);
        v.init(initmean, initstdev);
        m_sum = new double[num_factor];
        m_sum_sqr = new double[num_factor];
    }

    public double predict(
            SparseRow x) {
        return predict(x, m_sum, m_sum_sqr);
    }

    public double predict(
            SparseRow x,
            double[] sum,
            double[] sum_sqr) {
        double result = 0;
        if (k0) {
            result += w0;
        }
        if (k1) {
            for (int i = 0; i < x.getSize(); i++) {
                result += w[x.getData()[i].getId()] * x.getData()[i].getValue();
            }
        }
        for (int f = 0; f < num_factor; f++) {
            sum[f] = 0;
            sum_sqr[f] = 0;
            for (int i = 0; i < x.getSize(); i++) {
                double d = v.get(f, x.getData()[i].getId())
                        * x.getData()[i].getValue();
                sum[f] = sum[f] + d;
                sum_sqr[f] = sum_sqr[f] + d * d;
            }
            result += 0.5 * (sum[f] * sum[f] - sum_sqr[f]);
        }

        return result;
    }

    @Override
    public Float predictRating(
            final User user,
            final Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item is null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        SparseEntry e1 = new SparseEntry(LibSVMDataProvider.valueidmap
                .get(Integer.toString(0) + " " + user.getId()), 1);
        final Integer itemId = LibSVMDataProvider.valueidmap
                .get(Integer.toString(0) + " " + item.getId());
        if (itemId == null) {
            return Float.NaN;
        }
        SparseEntry e2 = new SparseEntry(itemId, 1);

        SparseRow row = new SparseRow(new SparseEntry[]{
                e1, e2
        });
        return (float) predict(row);
    }

    /*
     * (non-Javadoc)
     *
     * @see interfaces.Recommender#recommendItems(model.User)
     */
    @Override
    public Map<Integer, Float> recommendItems(
            final User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        final Map<Integer, Float> predictions = new LinkedHashMap<Integer, Float>();

        for (final Item item : hirecModel.getItems().values()) {
            final int itemId = item.getId();
            final float predictRating = predictRating(user, item);
            if (!Float.isNaN(predictRating)) {
                predictions.put(itemId, predictRating);
            }
        }
        final Map<Integer, Float> sortByComparator = MapUtil
                .sortByValueDescending(predictions);
        return sortByComparator;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FactorizationMachine";
    }

    @Override
    public Map<String, Map<String, String>> getConfigurabaleParameters() {
        return configurableParametersMap;
    }

    /*
     * (non-Javadoc)
     *
     * @see interfaces.Recommender#isSimilairtyNeeded()
     */
    @Override
    public boolean isSimilairtyNeeded() {
        return false;
    }
}
