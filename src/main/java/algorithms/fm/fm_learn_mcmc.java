package algorithms.fm;

import org.apache.log4j.Logger;

import algorithms.LibFmRecommender.TaskType;

/*--- Original comment by Mr Rendle ---
	MCMC and ALS based learning for factorization machines
	- this file contains the sampler for a full sample of all model and prior parameters 

	Based on the publication(s):
	* Steffen Rendle (2010): Factorization Machines, in Proceedings of the 10th IEEE International Conference on Data Mining (ICDM 2010), Sydney, Australia.
	* Steffen Rendle, Zeno Gantner, Christoph Freudenthaler, Lars Schmidt-Thieme (2011): Fast Context-aware Recommendations with Factorization Machines, in Proceedings of the 34th international ACM SIGIR conference on Research and development in information retrieval (SIGIR 2011), Beijing, China.
	* Christoph Freudenthaler, Lars Schmidt-Thieme, Steffen Rendle (2011): Bayesian Factorization Machines, in NIPS Workshop on Sparse Representation and Low-rank Approximation (NIPS-WS 2011), Spain.
	* Steffen Rendle (2012): Factorization Machines with libFM, ACM Transactions on Intelligent Systems and Technology (TIST 2012).

	Author:   Steffen Rendle, http://www.libfm.org/
	modified: 2012-12-27

	Copyright 2010-2012 Steffen Rendle, see license.txt for more information
*/

/**
 * This class contains the prediction algorithms used for MCMC and ALS
 */
public class fm_learn_mcmc extends fm_learn {
	
	private static final Logger LOG = Logger.getLogger(fm_learn_mcmc.class.getCanonicalName());
    
        @Override
		public double evaluate(Data data) { 
    		return Double.NaN; 
        }
                
        @Override
		protected double predict_case(Data data) {
			throw new IllegalArgumentException("not supported for MCMC and ALS");
		}
                
		public int num_iter;
		public int num_eval_cases;

		public double alpha_0, gamma_0, beta_0, mu_0;
		public double alpha;
		
		public double w0_mean_0;
 
		public DVectorDouble w_mu = new DVectorDouble(), w_lambda = new DVectorDouble();

		public DMatrixDouble v_mu = new DMatrixDouble(), v_lambda = new DMatrixDouble();


		public boolean do_sample; // switch between choosing expected values and drawing from distribution 
		public boolean do_multilevel; // use the two-level (hierarchical) model (TRUE) or the one-level (FALSE)
		public int nan_cntr_v, nan_cntr_w, nan_cntr_w0, nan_cntr_alpha, nan_cntr_w_mu, nan_cntr_w_lambda, nan_cntr_v_mu, nan_cntr_v_lambda;
		public int inf_cntr_v, inf_cntr_w, inf_cntr_w0, inf_cntr_alpha, inf_cntr_w_mu, inf_cntr_w_lambda, inf_cntr_v_mu, inf_cntr_v_lambda;

		protected DVectorDouble cache_for_group_values = new DVectorDouble();
		protected DVectorDouble pred_sum_all = new DVectorDouble();
		protected DVectorDouble pred_sum_all_but5 = new DVectorDouble();
		protected DVectorDouble pred_this = new DVectorDouble();

		protected e_q_term[] cache;
		protected e_q_term[] cache_test;

		protected sparse_rowFloat empty_data_row = new sparse_rowFloat(); // this is a dummy row for attributes that do not exist in the training data (but in test data)		
		public boolean output = true;

		protected void _learn(Data train, Data test) {};
	

		/**
		 * This method predicts the ratings for train and test for the algorithms MCMC and ALS
		 * @param main_data A weird way to transmit the Data objects to this method
		 * @param main_cache A weird way to transmit a return vessel for the output values to this method
		 */
		public void predict_data_and_write_to_eterms(DVector<Data> main_data, DVector<e_q_term[]> main_cache) {

			//This function predicts all data sets mentioned in main_data.
			//it stores the prediction in the e-term.
			if (main_data.dim == 0) { return ; }
 

			// do this using only the transpose copy of the training data:
			for (int ds = 0; ds < main_cache.dim; ds++) {
				e_q_term[] m_cache = main_cache.get(ds);
				Data m_data = main_data.get(ds);
				for (int i = 0; i < m_data.num_cases; i++) {
					m_cache[i].e = 0.0;
					m_cache[i].q = 0.0;
				} 	
			}


			// (1) do the 1/2 sum_f (sum_i v_if x_i)^2 and store it in the e/y-term
			for (int f = 0; f < fm.num_factor; f++) {
				double[] v = fm.v.value[f];

				// calculate cache[i].q = sum_i v_if x_i (== q_f-term)
				// Complexity: O(N_z(X^M))
				for (int ds = 0; ds < main_cache.dim; ds++) {
					e_q_term[] m_cache = main_cache.get(ds);
					Data m_data = main_data.get(ds);
					m_data.data_t.begin();
					int row_index;
					sparse_rowFloat feature_data;
					for (int i = 0; i < m_data.data_t.getNumRows(); i++) {
						{
							row_index = m_data.data_t.getRowIndex();
							feature_data = m_data.data_t.getRow(); 
							m_data.data_t.next();
						}
						double v_if = v[row_index];
					
						for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
							int train_case_index = feature_data.data[i_fd].id;		
							float x_li = feature_data.data[i_fd].value;	
							m_cache[train_case_index].q += v_if * x_li;			
						}
					}
				}


				// add 0.5*q^2 to e and set q to zero.
				// O(n*|B|)
				for (int ds = 0; ds < main_cache.dim; ds++) {
					e_q_term[] m_cache = main_cache.get(ds);
					Data m_data = main_data.get(ds);
					for (int c = 0; c < m_data.num_cases; c++) {
						double q_all = m_cache[c].q;
						m_cache[c].e += 0.5 * q_all*q_all;
						m_cache[c].q = 0.0;
					}
				}

			}

			// (2) do -1/2 sum_f (sum_i v_if^2 x_i^2) and store it in the q-term
			for (int f = 0; f < fm.num_factor; f++) {
				double[] v = fm.v.value[f];

				// sum up the q^S_f terms in the main-q-cache: 0.5*sum_i (v_if x_i)^2 (== q^S_f-term)
				// Complexity: O(N_z(X^M))
				for (int ds = 0; ds < main_cache.dim; ds++) {
					e_q_term[] m_cache = main_cache.get(ds);
					Data m_data = main_data.get(ds);
		
					m_data.data_t.begin();
					int row_index;
					sparse_rowFloat feature_data;
					for (int i = 0; i < m_data.data_t.getNumRows(); i++) {
						{
							row_index = m_data.data_t.getRowIndex();
							feature_data = m_data.data_t.getRow(); 
							m_data.data_t.next();
						}
						double v_if = v[row_index];
			
						for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
							int train_case_index = feature_data.data[i_fd].id;		
							float x_li = feature_data.data[i_fd].value;	
							m_cache[train_case_index].q -= 0.5 * v_if * v_if * x_li * x_li;  
						}
					}
				}

			}	

			// (3) add the w's to the q-term	
			if (fm.k1) {
				for (int ds = 0; ds < main_cache.dim; ds++) {
					e_q_term[] m_cache = main_cache.get(ds);
					Data m_data = main_data.get(ds);
					m_data.data_t.begin();
					int row_index;
					sparse_rowFloat feature_data;
					for (int i = 0; i < m_data.data_t.getNumRows(); i++) {
						{
							row_index = m_data.data_t.getRowIndex();
							feature_data = m_data.data_t.getRow(); 
							m_data.data_t.next();
						}
						double w_i = fm.w.get(row_index);						

						for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
							int train_case_index = feature_data.data[i_fd].id;		
							float x_li = feature_data.data[i_fd].value;	
							m_cache[train_case_index].q += w_i * x_li;
						}
					}
				}
			}	
			// (3) merge both for getting the prediction: w0+e(c)+q(c)
			for (int ds = 0; ds < main_cache.dim; ds++) {
				e_q_term[] m_cache = main_cache.get(ds);
				Data m_data = main_data.get(ds);
			
				for (int c = 0; c < m_data.num_cases; c++) {
					double q_all = m_cache[c].q;
					m_cache[c].e = m_cache[c].e + q_all;
					if (fm.k0) {
						m_cache[c].e += fm.w0;
					}
					m_cache[c].q = 0.0;
				}
			}

		}

        @Override
		public void predict(Data data, DVectorDouble out) {
        	///Note: This method is not used by R101. It is merely here for legacy purposes. It only does the intended job, if the test data was passed to the algorithm BEFORE learning has begun, which cannot be fulfilled for R101.
			if (do_sample) {
				assert(data.num_cases == pred_sum_all.dim);
				for (int i = 0; i < out.dim; i++) {
					out.set(i, pred_sum_all.get(i) / num_iter);
				} 
			} else {
				assert(data.num_cases == pred_this.dim);
				for (int i = 0; i < out.dim; i++) {
					out.set(i, pred_this.get(i));
				} 
			}
			for (int i = 0; i < out.dim; i++) {
				if (task == TaskType.Regression ) {
					out.set(i, Math.min(max_target, out.get(i)));
					out.set(i, Math.max(min_target, out.get(i)));
				} else if (task == TaskType.Classification) {
                                        out.set(i, Math.min(1.0, out.get(i)));
					out.set(i, Math.max(0.0, out.get(i)));
				} else {
					throw new IllegalArgumentException();
				}
			}
		}

		protected void add_main_q(Data train, int f) {
			// add the q(f)-terms to the main relation q-cache (using only the transpose data)
			
			double[] v = fm.v.value[f];


			{
				train.data_t.begin();
				int row_index;
				sparse_rowFloat feature_data;
				for (int i = 0; i < train.data_t.getNumRows(); i++) {
					{
						row_index = train.data_t.getRowIndex();
						feature_data = train.data_t.getRow(); 
						train.data_t.next();
					}
					double v_if = v[row_index];
					for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
						int train_case_index = feature_data.data[i_fd].id;		
						float x_li = feature_data.data[i_fd].value;	
						cache[train_case_index].q += v_if * x_li;
					}

				}
			}
		}		

		protected void draw_all(Data train) {                        
            ///Different usage in comparison to original. Reason: call by reference of first parameter not possible. Therefore return value had to be added.		
			alpha = draw_alpha(alpha, train.num_cases);
                        
			if (log != null) {
				log.log("alpha", alpha);
			}

			if (fm.k0) {
				fm.w0 = draw_w0(fm.w0, fm.reg0, train);
			}
			if (fm.k1) {
                double[] arr = fm.w.getArray();
				draw_w_lambda(arr);
				draw_w_mu(fm.w.getArray());
				if (log != null) {
					for (int g = 0; g < meta.num_attr_groups; g++) {
						log.log("wmu[" + g + "]", w_mu.get(g));
						log.log("wlambda[" + g + "]", w_lambda.get(g));
					}
				}

				// draw the w from their posterior
				train.data_t.begin();
				int row_index;
				sparse_rowFloat feature_data;
				for (int i = 0; i < train.data_t.getNumRows(); i++) {	
					{
						row_index = train.data_t.getRowIndex();
						feature_data = train.data_t.getRow(); 
						train.data_t.next();
					}
					int g = meta.attr_group.get(row_index);
                    ///Different usage in comparison to original. Reason: call by reference of first parameter not possible. Therefore return value had to be created.
					double retVal = draw_w(fm.w.get(row_index), w_mu.get(g), w_lambda.get(g), feature_data);
                                        fm.w.set(row_index, retVal);
				}
				// draw w's for which there is no observation in the training data
				for (int i = train.data_t.getNumRows(); i < fm.num_attribute; i++) {
					row_index = i;
					feature_data = empty_data_row;
					int g = meta.attr_group.get(row_index);
                                        ///Different usage in comparison to original. Reason: call by reference of first parameter not possible. Therefore return value had to be created.
					double retVal = draw_w(fm.w.get(row_index), w_mu.get(g), w_lambda.get(g), feature_data);
                                        fm.w.set(row_index, retVal);
				}

			}

			if (fm.num_factor > 0) {
				draw_v_lambda();
				draw_v_mu();
				if (log != null) {
					for (int g = 0; g < meta.num_attr_groups; g++) {
						for (int f = 0; f < fm.num_factor; f++) {
							log.log("vmu[" + g + "," + f + "]", v_mu.get(g,f));
							log.log("vlambda[" + g + "," + f + "]", v_lambda.get(g,f));
						}
					}
				}
			}

			for (int f = 0; f < fm.num_factor; f++) {

				for (int c = 0; c < train.num_cases; c++) {
					cache[c].q = 0.0;
				}

				add_main_q(train, f);
			
				double[] v = fm.v.value[f];
				
				// draw the thetas from their posterior
				train.data_t.begin();
				int row_index;
				sparse_rowFloat feature_data;
				for (int i = 0; i < train.data_t.getNumRows(); i++) {
					{
						row_index = train.data_t.getRowIndex();
						feature_data = train.data_t.getRow(); 
						train.data_t.next();
					}
					int g = meta.attr_group.get(row_index);
                    ///Different usage in comparison to original. Reason call by reference of first parameter not possible. Therefore return value had to be created.
					v[row_index] = draw_v(v[row_index], v_mu.get(g,f), v_lambda.get(g,f), feature_data);
				}		
				// draw v's for which there is no observation in the test data
				for (int i = train.data_t.getNumRows(); i < fm.num_attribute; i++) {
					row_index = i;
					feature_data = empty_data_row;
					int g = meta.attr_group.get(row_index);
                                        ///Different usage in comparison to original. Reason call by reference of first parameter not possible. Therefor return value had to be instated.
					v[row_index] = draw_v(v[row_index], v_mu.get(g,f), v_lambda.get(g,f), feature_data);
				}
			}		
		}

		// Find the optimal value for the global bias (0-way interaction)
		protected double draw_w0(double w0, double reg, Data train) {
			///Note: Call by reference emulation via return value for variable w0 (reg is not set in this method)
			// h = 1
			// h^2 = 1
			// \sum e*h = \sum e
			// \sum h^2 = \sum 1
			double w0_sigma_sqr;
			double w0_mean = 0;
			for (int i = 0; i < train.num_cases; i++) {
				w0_mean += cache[i].e - w0;
			}
			w0_sigma_sqr = (double) 1.0 / (reg + alpha * train.num_cases);
			w0_mean = - w0_sigma_sqr * (alpha * w0_mean - w0_mean_0 * reg);
			// update w0
			double w0_old = w0;

			if (do_sample) {
				w0 = StaticFunctions.ran_gaussian(w0_mean, Math.sqrt(w0_sigma_sqr));
			} else {
				w0 = w0_mean;
			}

			// check for out of bounds values
			if (Double.isNaN(w0)) {
				nan_cntr_w0++;
				w0 = w0_old;
				return w0;
			}
			if (Double.isInfinite(w0)) {
				inf_cntr_w0++;
				w0 = w0_old;
				return w0;
			}
			// update error
			for (int i = 0; i < train.num_cases; i++) {
				cache[i].e -= (w0_old - w0);
			}
                        return w0;
		}

		// Find the optimal value for the 1-way interaction w
		protected double draw_w(double w, double w_mu, double w_lambda, sparse_rowFloat feature_data) {
			///Note: Call by reference emulation via return value for variable w (w_mu, w_lambda is not set in this method)
			double w_sigma_sqr = 0;
			double w_mean = 0;
			for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
				int train_case_index = feature_data.data[i_fd].id;		
				float x_li = feature_data.data[i_fd].value;	
				w_mean += x_li * (cache[train_case_index].e - w * x_li);
				w_sigma_sqr += x_li * x_li;
			}
			w_sigma_sqr = (double) 1.0 / (w_lambda + alpha * w_sigma_sqr);
			w_mean = - w_sigma_sqr * (alpha * w_mean - w_mu * w_lambda);

			// update w:
			double w_old = w; 

			if (Double.isNaN(w_sigma_sqr) || Double.isInfinite(w_sigma_sqr)) { 
				w = 0.0;
			} else {
				if (do_sample) {
					w = StaticFunctions.ran_gaussian(w_mean, Math.sqrt(w_sigma_sqr));
				} else {
					w = w_mean;
				}
			}
			
			// check for out of bounds values
			if (Double.isNaN(w)) {
				nan_cntr_w++;
				w = w_old;
                return w;
			}
			if (Double.isInfinite(w)) {
				inf_cntr_w++;
				w = w_old;
                return w;
			}
			// update error:
			for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
				int train_case_index = feature_data.data[i_fd].id;	
				float x_li = feature_data.data[i_fd].value;	
				double h = x_li;
				cache[train_case_index].e -= h * (w_old - w);	
			}
                        return w;
		}
                
                
                
		protected double draw_v(double v, double v_mu, double v_lambda, sparse_rowFloat feature_data) {
			///Note: Call by reference emulation via return value for variable v (w_mu, v_lambda are not set in this method)
			double v_sigma_sqr = 0;
			double v_mean = 0;
			// v_sigma_sqr = \sum h^2 (always)
			// v_mean = \sum h*e (for non_internlock_interactions)
			for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
				int train_case_index = feature_data.data[i_fd].id;		
				float x_li = feature_data.data[i_fd].value;
				e_q_term cache_li = cache[train_case_index];
				double h = x_li * ( cache_li.q - x_li * v);
				v_mean += h * cache_li.e;
				v_sigma_sqr += h * h;
			}
			v_mean -= v * v_sigma_sqr;
			v_sigma_sqr = (double) 1.0 / (v_lambda + alpha * v_sigma_sqr);
			v_mean = - v_sigma_sqr * (alpha * v_mean - v_mu * v_lambda);
			
			// update v:
			double v_old = v; 

			if (Double.isNaN(v_sigma_sqr) || Double.isInfinite(v_sigma_sqr)) { 
				v = 0.0;
			} else {
				if (do_sample) {
					v = StaticFunctions.ran_gaussian(v_mean, Math.sqrt(v_sigma_sqr));
				} else {
					v = v_mean;
				}		
			}
		
			// check for out of bounds values
			if (Double.isNaN(v)) {
				nan_cntr_v++;
				v = v_old;
				return v;
			}
			if (Double.isInfinite(v)) {
				inf_cntr_v++;
				v = v_old;
				return v;
			}

			// update error and q:
			for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
				int train_case_index = feature_data.data[i_fd].id;		
				float x_li = feature_data.data[i_fd].value;	
				e_q_term cache_li = cache[train_case_index];
				double h = x_li * ( cache_li.q - x_li * v_old);
				cache_li.q -= x_li * (v_old - v);
				cache_li.e -= h * (v_old - v);
			}
                        return v;
		}
	

                
		double draw_alpha(double alpha, int num_train_total) {
			///Call by reference -> changed to return value usage 
			if (! do_multilevel) {
				alpha = alpha_0;
				return alpha;
			}
			double alpha_n = alpha_0 + num_train_total;
			double gamma_n = gamma_0;
			for (int i = 0; i < num_train_total; i++) {
				gamma_n += cache[i].e*cache[i].e;
			}
			double alpha_old = alpha;
			alpha = StaticFunctions.ran_gamma(alpha_n / 2.0, gamma_n / 2.0);

			// check for out of bounds values
			if (Double.isNaN(alpha)) {
				nan_cntr_alpha++;
				alpha = alpha_old;
				return alpha;
			}
			if (Double.isInfinite(alpha)) {
				inf_cntr_alpha++;
				alpha = alpha_old;
				return alpha;
			}
                        return alpha;
		}

                
		protected void draw_w_mu(double[] w) {
			///Note: Call by reference replace with array
			if (! do_multilevel) {
				w_mu.init(mu_0);
				return;
			}
			DVectorDouble w_mu_mean = cache_for_group_values;
			w_mu_mean.init(0.0);
			for (int i = 0; i < fm.num_attribute; i++) {
				int g = meta.attr_group.get(i);
				w_mu_mean.set(g, w_mu_mean.get(g)+w[i]);
			}
			for (int g = 0; g < meta.num_attr_groups; g++) {
				w_mu_mean.set(g, (w_mu_mean.get(g)+beta_0 * mu_0) / (meta.num_attr_per_group.get(g) + beta_0));
				double w_mu_sigma_sqr = (double) 1.0 / ((meta.num_attr_per_group.get(g) + beta_0) * w_lambda.get(g));
				double w_mu_old = w_mu.get(g);
				if (do_sample) {
					w_mu.set(g, StaticFunctions.ran_gaussian(w_mu_mean.get(g), Math.sqrt(w_mu_sigma_sqr)));
				} else {
					w_mu.set(g,w_mu_mean.get(g));
				}			

				// check for out of bounds values
				if (Double.isNaN(w_mu.get(g))) {
					nan_cntr_w_mu++;
					w_mu.set(g, w_mu_old);
					return;
				}
				if (Double.isInfinite(w_mu.get(g))) {
					inf_cntr_w_mu++;
					w_mu.set(g, w_mu_old);
					return;
				}
			}
		}

		protected void draw_w_lambda(double[] w) {
			///Note: Call by reference replace with array
			if (! do_multilevel) {
				return;
			}
				
			DVectorDouble w_lambda_gamma = cache_for_group_values;
			for (int g = 0; g < meta.num_attr_groups; g++) {
				w_lambda_gamma.set(g, beta_0 * (w_mu.get(g) - mu_0) * (w_mu.get(g) - mu_0) + gamma_0); 
			}
			for (int i = 0; i < fm.num_attribute; i++) {
				int g = meta.attr_group.get(i);
				w_lambda_gamma.set(g,w_lambda_gamma.get(g) + (w[i] - w_mu.get(g)) * (w[i] - w_mu.get(g)));
			}
			for (int g = 0; g < meta.num_attr_groups; g++) {
				double w_lambda_alpha = alpha_0 + meta.num_attr_per_group.get(g) + 1;
				double w_lambda_old = w_lambda.get(g);
				if (do_sample) {
					w_lambda.set(g, StaticFunctions.ran_gamma(w_lambda_alpha / 2.0, w_lambda_gamma.get(g) / 2.0));
				} else {
					w_lambda.set(g, w_lambda_alpha/w_lambda_gamma.get(g));
				}
				// check for out of bounds values
				if (Double.isNaN(w_lambda.get(g))) {
					nan_cntr_w_lambda++;
					w_lambda.set(g, w_lambda_old);
					return;
				}
				if (Double.isInfinite(w_lambda.get(g))) {
					inf_cntr_w_lambda++;
					w_lambda.set(g, w_lambda_old);
					return;
				}
			}
		}


		protected void draw_v_mu() {
			if (! do_multilevel) {
				v_mu.init(mu_0);
				return;
			}

			DVectorDouble v_mu_mean = cache_for_group_values;
			for (int f = 0; f < fm.num_factor; f++) {
				v_mu_mean.init(0.0);
				for (int i = 0; i < fm.num_attribute; i++) {
					int g = meta.attr_group.get(i);
					v_mu_mean.set(g, v_mu_mean.get(g) + fm.v.get(f,i));
				}
				for (int g = 0; g < meta.num_attr_groups; g++) {
					v_mu_mean.set(g, (v_mu_mean.get(g) + beta_0 * mu_0) / (meta.num_attr_per_group.get(g) + beta_0));
					double v_mu_sigma_sqr = (double) 1.0 / ((meta.num_attr_per_group.get(g) + beta_0) * v_lambda.get(g,f));
					double v_mu_old = v_mu.get(g,f);
					if (do_sample) {
						v_mu.set(g,f , StaticFunctions.ran_gaussian(v_mu_mean.get(g), Math.sqrt(v_mu_sigma_sqr)));
					} else {
						v_mu.set(g,f, v_mu_mean.get(g));
					}
					if (Double.isNaN(v_mu.get(g,f))) {
						nan_cntr_v_mu++;
						v_mu.set(g,f, v_mu_old);
						return;
					}
					if (Double.isInfinite(v_mu.get(g,f))) {
						inf_cntr_v_mu++;
						v_mu.set(g,f, v_mu_old);
						return;
					}
				}
			}
		}

		protected void draw_v_lambda() {
			if (! do_multilevel) {
				return;
			}

			DVectorDouble v_lambda_gamma = cache_for_group_values;
			for (int f = 0; f < fm.num_factor; f++) {
				for (int g = 0; g < meta.num_attr_groups; g++) {
					v_lambda_gamma.set(g, beta_0 * (v_mu.get(g,f) - mu_0) * (v_mu.get(g,f) - mu_0) + gamma_0); 
				}
				for (int i = 0; i < fm.num_attribute; i++) {
					int g = meta.attr_group.get(i);
					v_lambda_gamma.set(g, v_lambda_gamma.get(g) +  (fm.v.get(f,i) - v_mu.get(g,f)) * (fm.v.get(f,i) - v_mu.get(g,f)));
				}
				for (int g = 0; g < meta.num_attr_groups; g++) {
					double v_lambda_alpha = alpha_0 + meta.num_attr_per_group.get(g) + 1;
					double v_lambda_old = v_lambda.get(g,f);
					if (do_sample) {
						v_lambda.set(g,f, StaticFunctions.ran_gamma(v_lambda_alpha / 2.0, v_lambda_gamma.get(g) / 2.0));
					} else {
						v_lambda.set(g,f, v_lambda_alpha / v_lambda_gamma.get(g));
					}
					if (Double.isNaN(v_lambda.get(g,f))) {
						nan_cntr_v_lambda++;
						v_lambda.set(g,f, v_lambda_old);
						return;
					}
					if (Double.isInfinite(v_lambda.get(g,f))) {
						inf_cntr_v_lambda++;
						v_lambda.set(g,f, v_lambda_old);
						return;
					}
				}
			}
		}

        @Override
		public void init() {
			super.init();

			cache_for_group_values.setSize(meta.num_attr_groups);
                        cache_for_group_values.init(0.0);
			empty_data_row.size = 0;
			empty_data_row.data = null;

			alpha_0 = 1.0;
			gamma_0 = 1.0;
			beta_0 = 1.0;
			mu_0 = 0.0;

			alpha = 1;
			
			w0_mean_0 = 0.0;

			w_mu.setSize(meta.num_attr_groups);
			w_lambda.setSize(meta.num_attr_groups);
			w_mu.init(0.0); 
			w_lambda.init(0.0);
		
			v_mu.setSize(meta.num_attr_groups, fm.num_factor);
			v_lambda.setSize(meta.num_attr_groups, fm.num_factor);
			v_mu.init(0.0);
			v_lambda.init(0.0);
		}
		
        @Override
		public void learn(Data train, Data test) {
			pred_sum_all.setSize(test.num_cases);
			pred_sum_all_but5.setSize(test.num_cases);
			pred_this.setSize(test.num_cases);
			pred_sum_all.init(0.0);
			pred_sum_all_but5.init(0.0);
			pred_this.init(0.0);

			// init caches data structure
			cache = new e_q_term[train.num_cases];
			cache_test = new e_q_term[test.num_cases];
                        ///these arrays have to be initialized, because structs in c++ are instantiated, when arrays of them are allocated
                        for(int i = 0; i< train.num_cases;i++){
                            cache[i] = new e_q_term();
                        }
                        for(int i = 0; i< test.num_cases;i++){
                            cache_test[i] = new e_q_term();
                        }
			_learn(train, test);
		}
                
        @Override
		public void debug() { 
			super.debug();
			LOG.debug("do_multilevel=" + do_multilevel);
			LOG.debug("do_sampling=" + do_sample );
			LOG.debug("num_eval_cases=" + num_eval_cases);
		}

        /**
         * A version of the predict function that is used by R101. This function was modified because to original used a transposed matrix. It assumed that this matrix is densly packed, which it is not in the case of R101. 
         * Look at the method create_data_t to see the problem. It transposes the matrix. A (not transposed) matrix with one element with the id 3000 becomes a transposed matrix with 3000 rows this way. They were all iterated in the old method.
         * @param feature_data The data necessary for prediction
         * @return The prediction.
         */
		public float predict_data_and_write_to_eterms(sparse_rowFloat feature_data) {

			e_q_term term = new e_q_term();

			term.e = 0.0; 
			term.q = 0.0;
			

			// (1) do the 1/2 sum_f (sum_i v_if x_i)^2 and store it in the e/y-term
			for (int f = 0; f < fm.num_factor; f++) {
				double[] v = fm.v.value[f];

				// calculate cache[i].q = sum_i v_if x_i (== q_f-term)
				// Complexity: O(N_z(X^M))
				for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
					double v_if = v[feature_data.data[i_fd].id];
					float x_li = feature_data.data[i_fd].value;	
					term.q += v_if * x_li;			
				}
				
				// add 0.5*q^2 to e and set q to zero.
				// O(n*|B|)
				
				
				double q_all = term.q;
				term.e += 0.5 * q_all*q_all;
				term.q = 0.0;
				
				

			}

			// (2) do -1/2 sum_f (sum_i v_if^2 x_i^2) and store it in the q-term
			for (int f = 0; f < fm.num_factor; f++) {
				double[] v = fm.v.value[f];

				// sum up the q^S_f terms in the main-q-cache: 0.5*sum_i (v_if x_i)^2 (== q^S_f-term)
				// Complexity: O(N_z(X^M))
				
				
				for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
					double v_if = v[feature_data.data[i_fd].id];
					float x_li = feature_data.data[i_fd].value;	
					term.q -= 0.5 * v_if * v_if * x_li * x_li;  
				}
				
				
			}	

			// (3) add the w's to the q-term	
			if (fm.k1) {
										

				for (int i_fd = 0; i_fd < feature_data.size; i_fd++) {	
					double w_i = fm.w.get(feature_data.data[i_fd].id);
					float x_li = feature_data.data[i_fd].value;	
					term.q += w_i * x_li;
				}
			}	
			// (3) merge both for getting the prediction: w0+e(c)+q(c)
			
			double q_all = term.q;
			term.e = term.e + q_all;
			if (fm.k0) {
				term.e += fm.w0;
			}
				

			return (float) term.e;
		}

}
