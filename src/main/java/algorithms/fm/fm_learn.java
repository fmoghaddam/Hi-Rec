package algorithms.fm;

import org.apache.log4j.Logger;

import algorithms.LibFmRecommender.TaskType;

/*--- Original comment by Mr Rendle ---
	Generic learning method for factorization machines

	Author:   Steffen Rendle, http://www.libfm.org/
	modified: 2012-01-04

	Copyright 2010-2012 Steffen Rendle, see license.txt for more information
*/

/**
 * This class contains some fields and management methods that are common all learning algorithms in libfm. All learning algorithms are derived from this class
 */
public abstract class fm_learn {
		
		private static final Logger LOG = Logger.getLogger(fm_learn.class.getCanonicalName());
	
		/**
		 * This method is called by the subclass (in the case of SGD and SGDA) to retrieve the next prediction. The whole data object is passed, because it also contains a pointer to the element, which is currently worked on.
		 * @param data The test data.
		 * @return The current prediction
		 */
		protected double predict_case(Data data) {
			// this function can be overwritten (e.g. for MCMC)
			return fm.predict(data.data.getRow());
		}
		
		public DataMetaInfo meta;
		public fm_model fm;
		public double min_target;
		public double max_target;

		public TaskType task;
 
		public Data validation;	
		
		public RLog log;

		public fm_learn() { 
			log = null; task = TaskType.Regression; meta = null;
        } 
		
		/**
		 * This method mostly contains logging output, but depending on the learning method field variables might be initialized in overloads of the method.
		 */
		public void init() {
			if (log != null) {
				if (task == TaskType.Regression) {
					log.addField("rmse", Double.NaN);
					log.addField("mae", Double.NaN);
				} else if (task == TaskType.Classification) {
					log.addField("accuracy", Double.NaN);
				} else {
					throw new java.lang.IllegalArgumentException();
				}
				log.addField("time_pred", Double.NaN);
				log.addField("time_learn", Double.NaN);
				log.addField("time_learn2", Double.NaN);
				log.addField("time_learn4", Double.NaN);
			}
		}

		/**
		 * This method is used to calculate the RMSE in every iteration
		 * @param data The data on which to calculate the error upon
		 * @return The calculated error
		 */
		public double evaluate(Data data) {
			assert(data.data != null);
			if (task == TaskType.Regression) {
				return evaluate_regression(data);
			} else if (task == TaskType.Classification) {
				return evaluate_classification(data);
			} else {
				throw new java.lang.IllegalArgumentException();
			}
		}

		/**
		 * This method is overwritten by the subclasses and contains the learning algorithm. This is the main part of the factorization machine. When this method is called from R101 the test set is only passed pro forma. It will be empty every time. It can be filled however to simulate a run comparable to the original implementation of libfm.
		 * @param train The training set.
		 * @param test The test set. For R101 usage an empty set.
		 */
		public abstract void learn(Data train, Data test);
		/**
		 * This method is overwritten by the subclasses and contains the prediction algorithm. In case of MCMC and ALS this method does only contain an method, that grabs the prerendered value from an array, because the algorithm thinks, that prediction were made simultanious to the learning. This is not possible with R101 however, so this method is not used for MCMC and ALS, if R101 is used.
		 * @param data The data for which predictions are to be made
		 * @param out The resulting predictions
		 */
		public abstract void predict(Data data, DVectorDouble out);
		
		/**
		 * This method and its overloads are only used for output. Setting the verbosity to false will suppress this output.
		 */
		public void debug() { 
                        LOG.debug("task=" + task );
                        LOG.debug("min_target=" + min_target );
                        LOG.debug("max_target=" + max_target );		
		}
             
		/**
		 * Determines classification accuracy. Is currently not used, but should work fine. 
		 * @param data
		 * @return
		 */
		protected double evaluate_classification(Data data) {
			int num_correct = 0;
			long eval_time = System.currentTimeMillis();
			for (data.data.begin(); !data.data.end(); data.data.next()) {
				double p = predict_case(data);
				if (((p >= 0) && (data.target.get(data.data.getRowIndex()) >= 0)) || ((p < 0) && (data.target.get(data.data.getRowIndex()) < 0))) {
					num_correct++;
				}	
			}	
			eval_time = (System.currentTimeMillis() - eval_time);
			// log the values
			if (log != null) {
				log.log("accuracy", (double) num_correct / (double) data.data.getNumRows());
				log.log("time_pred", eval_time);
			}

			return (double) num_correct / (double) data.data.getNumRows();
		}
               
		/**
		 * A simple method that calculates error values in each learning iteration. When used with R101, this only displays the error for the training set, as test data is available only later.
		 * @param data The data on which to calculate the error
		 * @return The RMSE
		 */
		protected double evaluate_regression(Data data) {
			double rmse_sum_sqr = 0;
			double mae_sum_abs = 0;
			for (data.data.begin(); !data.data.end(); data.data.next()) {
				double p = predict_case(data); 
				p = Math.min(max_target, p);
				p = Math.max(min_target, p);
				double err = p - data.target.get(data.data.getRowIndex());
				rmse_sum_sqr += err*err;
				mae_sum_abs += Math.abs((double)err);	
			}	
			// log the values
			if (log != null) {
				log.log("rmse", Math.sqrt(rmse_sum_sqr/data.data.getNumRows()));
				log.log("mae", mae_sum_abs/data.data.getNumRows());
			}

			return Math.sqrt(rmse_sum_sqr/data.data.getNumRows());
		}

}
