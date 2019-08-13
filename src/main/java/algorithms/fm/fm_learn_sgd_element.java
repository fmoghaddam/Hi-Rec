package algorithms.fm;

import org.apache.log4j.Logger;

import algorithms.LibFmRecommender.TaskType;

/*--- Original comment by Mr Rendle ---
	Stochastic Gradient Descent based learning for classification and regression

	Based on the publication(s):
	Steffen Rendle (2010): Factorization Machines, in Proceedings of the 10th IEEE International Conference on Data Mining (ICDM 2010), Sydney, Australia.

	Author:   Steffen Rendle, http://www.libfm.org/
	modified: 2012-01-04

	Copyright 2010-2012 Steffen Rendle, see license.txt for more information
*/

/**
 * This class contains the learning an prediction algorithms for the factorization method SGD
 */
public class fm_learn_sgd_element extends fm_learn_sgd {

		private static final Logger LOG = Logger.getLogger(fm_learn_sgd_element.class.getCanonicalName());
	
        public fm_learn_sgd_element(){
            super();
        }
                
        @Override
		public void init() {
			super.init();

			if (log != null) {
				log.addField("rmse_train", Double.NaN);
			}
		}
                
        @Override
		public void learn(Data train, Data test) {
			super.learn(train, test);
                        
			LOG.debug("SGD: DON'T FORGET TO SHUFFLE THE ROWS IN TRAINING DATA TO GET THE BEST RESULTS.");
			// SGD
			for (int i = 0; i < num_iter; i++) {
				for (train.data.begin(); !train.data.end(); train.data.next()) {
					
					double p = fm.predict(train.data.getRow(), sum, sum_sqr);
					double mult = 0;
					if (task == TaskType.Regression) {
						p = Math.min(max_target, p);
						p = Math.max(min_target, p);
						mult = -(train.target.get(train.data.getRowIndex())-p);
					} else if (task == TaskType.Classification) {
						mult = -train.target.get(train.data.getRowIndex())*(1.0-1.0/(1.0+Math.exp(-train.target.get(train.data.getRowIndex())*p)));
					}				
					SGD(train.data.getRow(), mult, sum);					
				}				
				double rmse_train = evaluate(train);
				LOG.debug("#Iter="  + String.format("%3d",i) + " Train=" + String.format("%.5f",rmse_train));
			}
		}
		
}
