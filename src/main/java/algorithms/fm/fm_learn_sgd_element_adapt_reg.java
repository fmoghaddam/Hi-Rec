package algorithms.fm;

import org.apache.log4j.Logger;

import algorithms.LibFmRecommender.TaskType;

/*--- Original comment by Mr Rendle ---
	Stochastic Gradient Descent based learning for classification and regression using adaptive shrinkage

	Based on the publication(s):
	Steffen Rendle (2012): Learning Recommender Systems with Adaptive Regularization, in Proceedings of the 5th ACM International Conference on Web Search and Data Mining (WSDM 2012), Seattle, USA. 

	Author:   Steffen Rendle, http://www.libfm.org/
	modified: 2011-11-17

	theta'	= theta - alpha*(grad_theta + 2*lambda*theta)
		= theta(1-2*alpha*lambda) - alpha*grad_theta

	lambda^* = lambda - alpha*(grad_lambda)
	with 	
		grad_lambdaw0 = (grad l(y(x),y)) * (-2 * alpha * w_0)
		grad_lambdawg = (grad l(y(x),y)) * (-2 * alpha * (\sum_{l \in group(g)} x_l * w_l))
		grad_lambdafg = (grad l(y(x),y)) * (-2 * alpha * (\sum_{l} x_l * v'_lf) * \sum_{l \in group(g)} x_l * v_lf) - \sum_{l \in group(g)} x^2_l * v_lf * v'_lf)

	Copyright 2011 Steffen Rendle, see license.txt for more information
*/

/**
 * This class contains the learning and prediction algorithms for SGDA (a factorization method with self regularization)
 */
public class fm_learn_sgd_element_adapt_reg extends fm_learn_sgd {
	
		private static final Logger LOG = Logger.getLogger(fm_learn_sgd_element_adapt_reg.class.getCanonicalName());
		// regularization parameter
		public double reg_0; // shrinking the bias towards the mean of the bias (which is the bias) is the same as no regularization.

		public DVectorDouble reg_w = new DVectorDouble();
		public DMatrixDouble reg_v = new DMatrixDouble();

		public double mean_w, var_w;
		public DVectorDouble mean_v = new DVectorDouble(), var_v= new DVectorDouble();

		// for each parameter there is one gradient to store
		public DVectorDouble grad_w = new DVectorDouble(); 
		public DMatrixDouble grad_v = new DMatrixDouble();

		public Data validation;

		// local parameters in the lambda_update step
		public DVectorDouble lambda_w_grad= new DVectorDouble();
		public DVectorDouble sum_f= new DVectorDouble(), sum_f_dash_f= new DVectorDouble();


        @Override
		public void init() {
            ///The following line should be "super.super.init()" which is impossible in java. But this doesn't matter, because the sidestep into super.super only does one log entry.
			super.init();

			reg_0 = 0;
			reg_w.setSize(meta.num_attr_groups);
			reg_v.setSize(meta.num_attr_groups, fm.num_factor);

			mean_v.setSize(fm.num_factor);
			var_v.setSize(fm.num_factor);

			grad_w.setSize(fm.num_attribute);
			grad_v.setSize(fm.num_factor, fm.num_attribute);

			grad_w.init(0.0);
			grad_v.init(0.0);

			lambda_w_grad.setSize(meta.num_attr_groups);
			sum_f.setSize(meta.num_attr_groups);
			sum_f_dash_f.setSize(meta.num_attr_groups);


			if (log != null) {
				log.addField("rmse_train", Double.NaN);
				log.addField("rmse_val", Double.NaN);	
				
				log.addField("wmean", Double.NaN);
				log.addField("wvar", Double.NaN);
				for (int f = 0; f < fm.num_factor; f++) {
                                    log.addField("vmean" + f, Double.NaN);
                                    log.addField("vvar" + f, Double.NaN);
				}
				for (int g = 0; g < meta.num_attr_groups; g++) {
                                        log.addField("regw[" + g + "]", Double.NaN);
					for (int f = 0; f < fm.num_factor; f++) {
                                            log.addField("regv[" + g + "," + f + "]", Double.NaN);
					}
				}
			}
		}


		public void sgd_theta_step(sparse_rowFloat x, float target) {
			double p = fm.predict(x, sum, sum_sqr);
			double mult = 0;
			if (task == TaskType.Regression) {
				p = Math.min(max_target, p);
				p = Math.max(min_target, p);
				mult = 2 * (p - target);
			} else if (task == TaskType.Classification) {
				mult = target * (  (1.0/(1.0+Math.exp(-target*p))) - 1.0 );
			}

			// make the update with my regularization constants:
			if (fm.k0) {
				double grad_0 = mult;
                ///In this line the original uses a local variable that is a pointer to fm.w0 (mutable).
                ///All usages of this local var were substituted by the real thing
				fm.w0 -= learn_rate * (grad_0 + 2 * reg_0 * fm.w0);
			}
			if (fm.k1) {
				for (int i = 0; i < x.size; i++) {
					int g = meta.attr_group.get(x.data[i].id);
                    ///original: double& w = fm->w(x.data[i].id);
                    ///As before mentioned: substituted with the real thing
					grad_w.set(x.data[i].id,  mult * x.data[i].value);
					fm.w.set(x.data[i].id, fm.w.get(x.data[i].id) - learn_rate * (grad_w.get(x.data[i].id) + 2 * reg_w.get(g) * fm.w.get(x.data[i].id)));
				}
			}	
			for (int f = 0; f < fm.num_factor; f++) {
				for (int i = 0; i < x.size; i++) {
					int g = meta.attr_group.get(x.data[i].id);
                    ///Original: double& v = fm->v(f,x.data[i].id);
                    ///As before mentioned: substituted with the real thing
					grad_v.set(f,x.data[i].id, mult * (x.data[i].value * (sum.get(f) - fm.v.get(f,x.data[i].id) * x.data[i].value))); // grad_v_if = (y(x)-y) * [ x_i*(\sum_j x_j v_jf) - v_if*x^2 ]			
					fm.v.set(f,x.data[i].id,fm.v.get(f,x.data[i].id) - learn_rate * (grad_v.get(f,x.data[i].id) + 2 * reg_v.get(g,f) * fm.v.get(f,x.data[i].id)));
				}
			}	
		}

		double predict_scaled(sparse_rowFloat x) {
			double p = 0.0;
			if (fm.k0) {	
				p += fm.w0; 
			}
			if (fm.k1) {
				for (int i = 0; i < x.size; i++) {
					assert(x.data[i].id < fm.num_attribute);
					int g = meta.attr_group.get(x.data[i].id);
					double w = fm.w.get(x.data[i].id); 
					double w_dash = w - learn_rate * (grad_w.get(x.data[i].id) + 2 * reg_w.get(g) * w);
					p += w_dash * x.data[i].value; 
				}
			}
			for (int f = 0; f < fm.num_factor; f++) {
				sum.set(f, 0.0);
				sum_sqr.set(f, 0.0);
				for (int i = 0; i < x.size; i++) {
					int g = meta.attr_group.get(x.data[i].id);
					double v = fm.v.get(f,x.data[i].id); 
					double v_dash = v - learn_rate * (grad_v.get(f,x.data[i].id) + 2 * reg_v.get(g,f) * v);
					double d = v_dash * x.data[i].value;
					sum.set(f, sum.get(f)+ d);
					sum_sqr.set(f, sum_sqr.get(f) + d*d);
				}
				p += 0.5 * (sum.get(f)*sum.get(f) - sum_sqr.get(f));
			}
			return p;
		}

		void sgd_lambda_step(sparse_rowFloat x, float target) {
			double p = predict_scaled(x);
			double grad_loss = 0;
			if (task == TaskType.Regression) {
				p = Math.min(max_target, p);
				p = Math.max(min_target, p);
				grad_loss = 2 * (p - target);
			} else if (task == TaskType.Classification) {
				grad_loss = target * ( (1.0/(1.0+Math.exp(-target*p))) -  1.0);
			}		
					
			if (fm.k1) {
				lambda_w_grad.init(0.0);
				for (int i = 0; i < x.size; i++) {
					int g = meta.attr_group.get(x.data[i].id);
					lambda_w_grad.set(g, lambda_w_grad.get(g)+ x.data[i].value * fm.w.get(x.data[i].id)); 
				}
				for (int g = 0; g < meta.num_attr_groups; g++) {
					lambda_w_grad.set(g, -2 * learn_rate * lambda_w_grad.get(g)); 
					reg_w.set(g, reg_w.get(g)- learn_rate * grad_loss * lambda_w_grad.get(g));
					reg_w.set(g, Math.max(0.0, reg_w.get(g)));
				}
			}	
			for (int f = 0; f < fm.num_factor; f++) {
				// grad_lambdafg = (grad l(y(x),y)) * (-2 * alpha * (\sum_{l} x_l * v'_lf) * (\sum_{l \in group(g)} x_l * v_lf) - \sum_{l \in group(g)} x^2_l * v_lf * v'_lf)
				// sum_f_dash      := \sum_{l} x_l * v'_lf, this is independent of the groups
				// sum_f(g)        := \sum_{l \in group(g)} x_l * v_lf
				// sum_f_dash_f(g) := \sum_{l \in group(g)} x^2_l * v_lf * v'_lf
				double sum_f_dash = 0.0;
                                
                sum_f.init(0.0);
				sum_f_dash_f.init(0.0);
				for (int i = 0; i < x.size; i++) {
					// v_if' =  [ v_if * (1-alpha*lambda_v_f) - alpha * grad_v_if] 
					int g = meta.attr_group.get(x.data[i].id);
					double v = fm.v.get(f,x.data[i].id); 
					double v_dash = v - learn_rate * (grad_v.get(f,x.data[i].id) + 2 * reg_v.get(g,f) * v);
					
					sum_f_dash += v_dash * x.data[i].value;
					sum_f.set(g, sum_f.get(g)+ v * x.data[i].value); 
					sum_f_dash_f.set(g ,sum_f_dash_f.get(g)+ v_dash * x.data[i].value * v * x.data[i].value);
				}
				for (int g = 0; g < meta.num_attr_groups; g++) {
					double lambda_v_grad = -2 * learn_rate *  (sum_f_dash * sum_f.get(g) - sum_f_dash_f.get(g));  
					reg_v.set(g,f ,reg_v.get(g,f)- learn_rate * grad_loss * lambda_v_grad);
					reg_v.set(g,f, Math.max(0.0, reg_v.get(g,f)));
				}
			}

		}

		void update_means() {
			mean_w = 0;
			mean_v.init(0.0);
			var_w = 0;
			var_v.init(0.0);
			for (int j = 0; j < fm.num_attribute; j++) {
				mean_w += fm.w.get(j);
				var_w += fm.w.get(j)*fm.w.get(j);
				for (int f = 0; f < fm.num_factor; f++) {
					mean_v.set(f, mean_v.get(f) + fm.v.get(f,j));
					var_v.set(f, var_v.get(f)+ fm.v.get(f,j)*fm.v.get(f,j));
				}
			}
			mean_w /= (double) fm.num_attribute;
			var_w = var_w/fm.num_attribute - mean_w*mean_w;
			for (int f = 0; f < fm.num_factor; f++) {
				mean_v.set(f, mean_v.get(f) / fm.num_attribute);
				var_v.set(f, var_v.get(f)/fm.num_attribute - mean_v.get(f)*mean_v.get(f));
			}

			mean_w = 0;
			for (int f = 0; f < fm.num_factor; f++) {
				mean_v.set(f,0.0);
			}			
		}

        @Override
		public void learn(Data train, Data test) {
            ///Next (out commented) line was super.super.learn() int he original code, which is not possible in java. In this case: Not that important, because super.super.learn does basically nothing.
			///super.learn(train, test);

            LOG.debug("Training using self-adaptive-regularization SGD.");
    		LOG.debug("DON'T FORGET TO SHUFFLE THE ROWS IN TRAINING AND VALIDATION DATA TO GET THE BEST RESULTS."); 

			// make sure that fm-parameters are initialized correctly (no other side effects)
			fm.w.init(0.0);
			fm.reg0 = 0;
			fm.regw = 0; 
			fm.regv = 0; 

			// start with no regularization
			reg_w.init(0.0);
			reg_v.init(0.0);
			
            LOG.debug("Using " + train.data.getNumRows() + " rows for training model parameters and " + validation.data.getNumRows() + " for training shrinkage." );

			// SGD
			for (int i = 0; i < num_iter; i++) {
				long iteration_time = System.currentTimeMillis();

				// SGD-based learning: both lambda and theta are learned
				update_means();
				validation.data.begin();
				for (train.data.begin(); !train.data.end(); train.data.next()) {
					sgd_theta_step(train.data.getRow(), train.target.get(train.data.getRowIndex()));
					
					if (i > 0) { // make no lambda steps in the first iteration, because some of the gradients (grad_theta) might not be initialized. 
						if (validation.data.end()) {
							update_means();
							validation.data.begin();					
						}
						sgd_lambda_step(validation.data.getRow(), validation.target.get(validation.data.getRowIndex()));
						validation.data.next();
					}
				}								
				

				// (3) Evaluation					
				iteration_time = (System.currentTimeMillis() - iteration_time);
	
				double rmse_val = evaluate(validation);
				double rmse_train = evaluate(train);
				///double rmse_test = evaluate(test);
				LOG.debug("#Iter="  + String.format("%3d",i) + " Train=" + String.format("%.5f",rmse_train));
				if (log != null) {
					log.log("wmean", mean_w);						
					log.log("wvar", var_w);					
					for (int f = 0; f < fm.num_factor; f++) {
                                                log.log("vmean" + f, mean_v.get(f));
                                                log.log("vvar" + f, var_v.get(f));
					}
					for (int g = 0; g < meta.num_attr_groups; g++) {
						log.log("regw[" + g + "]", reg_w.get(g));
						for (int f = 0; f < fm.num_factor; f++) {
							log.log("regv[" + g + "," + f + "]", reg_v.get(g,f));
						}
					}
					log.log("time_learn", iteration_time);
					log.log("rmse_train", rmse_train);
					log.log("rmse_val", rmse_val);
					log.newLine();	
				}
			}		
		}

        @Override
		public void debug() {
			LOG.debug("method=sgda");
			//Should be super.super but that's ok for this debug method
			super.debug();			
		}

		
}
