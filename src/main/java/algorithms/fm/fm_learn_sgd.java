package algorithms.fm;

import org.apache.log4j.Logger;

import algorithms.LibFmRecommender.TaskType;

/*--- Original comment by Mr Rendle ---
	Stochastic Gradient Descent based learning

	Based on the publication(s):
	Steffen Rendle (2010): Factorization Machines, in Proceedings of the 10th IEEE International Conference on Data Mining (ICDM 2010), Sydney, Australia.

	Author:   Steffen Rendle, http://www.libfm.org/
	modified: 2012-01-03

	Copyright 2010-2012 Steffen Rendle, see license.txt for more information
*/

/**
 * This class contains some fields and management methods that are common to SGD and SGDA
 */
public class fm_learn_sgd extends fm_learn {
		private static final Logger LOG = Logger.getLogger(fm_learn_sgd.class.getCanonicalName());
		protected DVectorDouble sum = new DVectorDouble(), sum_sqr = new DVectorDouble();
                
		public int num_iter;
		public double learn_rate;
		public DVectorDouble learn_rates = new DVectorDouble();		

		@Override
		public void init() {		
			super.init();	
			learn_rates.setSize(3);
			sum.setSize(fm.num_factor);
            sum.init(0.0);
			sum_sqr.setSize(fm.num_factor);
            sum_sqr.init(0.0);
		}		

        @Override
		public void learn(Data train, Data test) {
        	///Note: This method is only called explicitly by "super.learn" from the subclasses. It cannot be called from outside, because no instance of this class is ever created and every subclass overwrites this method.
			LOG.debug("learnrate=" + learn_rate );
			LOG.debug( "learnrates=" + learn_rates.get(0) + "," + learn_rates.get(1) + "," + learn_rates.get(2) );
			LOG.debug( "#iterations=" + num_iter );
		}

		public void SGD(sparse_rowFloat x, double multiplier, DVectorDouble sum) {
			fm_SGD(fm, learn_rate, x, multiplier, sum); 
		}
		
        @Override
		public void debug() {
        	LOG.debug("num_iter=" + num_iter );
			super.debug();			
		}

        @Override
		public void predict(Data data, DVectorDouble out) {
        	///In contrast to the MCMC and ALS implementation this method is actually used by the R101 workflow to obtain the predictions
        	///For MCMC and ALS a workaround had to be used. The following method is called instead: predict_data_and_write_to_eterms
                    for (data.data.begin(); !data.data.end(); data.data.next()) {
                            double p = predict_case(data);
                            if (task == TaskType.Regression ) {
                                    p = Math.min(max_target, p);
                                    p = Math.max(min_target, p);
                            } else if (task == TaskType.Classification) {
                                    p = 1.0/(1.0 + Math.exp(-p));
                            } else {
                                    throw new IllegalArgumentException();
                            }
                            out.set(data.data.getRowIndex(), p);
                    }				
		} 
                
       private static void fm_SGD(fm_model fm, double learn_rate, sparse_rowFloat x, double multiplier, DVectorDouble sum) {
                        if (fm.k0) {
                                fm.w0 -= learn_rate * (multiplier + fm.reg0 * fm.w0);
                        }
                        if (fm.k1) {
                                for (int i = 0; i < x.size; i++) {
                                        double w = fm.w.get(x.data[i].id);
                                        w -= learn_rate * (multiplier * x.data[i].value + fm.regw * w);
                                        fm.w.set(x.data[i].id, w);
                                }
                        }	
                        for (int f = 0; f < fm.num_factor; f++) {
                                for (int i = 0; i < x.size; i++) {
                                        double v = fm.v.get(f,x.data[i].id);
                                        double grad = sum.get(f) * x.data[i].value - v * x.data[i].value * x.data[i].value; 
                                        v -= learn_rate * (multiplier * grad + fm.regv * v);
                                        fm.v.set(f,x.data[i].id, v);
                                }
                        }	
                }

}
