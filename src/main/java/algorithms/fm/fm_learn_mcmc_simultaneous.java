package algorithms.fm;

import org.apache.log4j.Logger;

import algorithms.LibFmRecommender.TaskType;

/*--- Original comment by Mr Rendle ---
 MCMC and ALS based learning for factorization machines
 - this file contains the learning procedure including evaluations

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
 * This class contains the learning algorithms for MCMC and ALS.
 * 
 */
public class fm_learn_mcmc_simultaneous extends fm_learn_mcmc {
	private static final Logger LOG = Logger.getLogger(fm_learn_mcmc_simultaneous.class.getCanonicalName());
	@Override
	protected void _learn(Data train, Data test) {

		int num_complete_iter = 0;

		// make a collection of datasets that are predicted jointly
		int num_data = 2;
		DVector<Data> main_data = new DVector<>(num_data);
		DVector<e_q_term[]> main_cache = new DVector<>(num_data);
		main_data.add(train);
		main_data.add(test);
		main_cache.add(cache);
		main_cache.add(cache_test);

		predict_data_and_write_to_eterms(main_data, main_cache);
		if (task == TaskType.Regression) {
			// remove the target from each prediction, because: e(c) :=
			// \hat{y}(c) - target(c)
			for (int c = 0; c < train.num_cases; c++) {
				cache[c].e = cache[c].e - train.target.get(c);
			}

		} else if (task == TaskType.Classification) {
			// for Classification: remove from e not the target but a sampled
			// value from a truncated normal
			// for initializing, they are not sampled but initialized with
			// meaningful values:
			// -1 for the negative class and +1 for the positive class (actually
			// these are the values that are already in the target and thus, we
			// can do the same as for regression; but note that other
			// initialization strategies would need other techniques here:
			for (int c = 0; c < train.num_cases; c++) {
				cache[c].e = cache[c].e - train.target.get(c);
			}

		} else {
			throw new IllegalArgumentException();
		}

		for (int i = num_complete_iter; i < num_iter; i++) {
			nan_cntr_w0 = 0;
			inf_cntr_w0 = 0;
			nan_cntr_w = 0;
			inf_cntr_w = 0;
			nan_cntr_v = 0;
			inf_cntr_v = 0;
			nan_cntr_alpha = 0;
			inf_cntr_alpha = 0;
			nan_cntr_w_mu = 0;
			inf_cntr_w_mu = 0;
			nan_cntr_w_lambda = 0;
			inf_cntr_w_lambda = 0;
			nan_cntr_v_mu = 0;
			inf_cntr_v_mu = 0;
			nan_cntr_v_lambda = 0;
			inf_cntr_v_lambda = 0;

			draw_all(train);

			if ((nan_cntr_alpha > 0) || (inf_cntr_alpha > 0)) {
				LOG.debug("#nans in alpha:\t" + nan_cntr_alpha
						+ "\t#inf_in_alpha:\t" + inf_cntr_alpha);
			}
			if ((nan_cntr_w0 > 0) || (inf_cntr_w0 > 0)) {
				LOG.debug("#nans in w0:\t" + nan_cntr_w0 + "\t#inf_in_w0:\t"
						+ inf_cntr_w0);
			}
			if ((nan_cntr_w > 0) || (inf_cntr_w > 0)) {
				LOG.debug("#nans in w:\t" + nan_cntr_w + "\t#inf_in_w:\t"
						+ inf_cntr_w);
			}
			if ((nan_cntr_v > 0) || (inf_cntr_v > 0)) {
				LOG.debug("#nans in v:\t" + nan_cntr_v + "\t#inf_in_v:\t"
						+ inf_cntr_v);
			}
			if ((nan_cntr_w_mu > 0) || (inf_cntr_w_mu > 0)) {
				LOG.debug("#nans in w_mu:\t" + nan_cntr_w_mu
						+ "\t#inf_in_w_mu:\t" + inf_cntr_w_mu);
			}
			if ((nan_cntr_w_lambda > 0) || (inf_cntr_w_lambda > 0)) {
				LOG.debug("#nans in w_lambda:\t" + nan_cntr_w_lambda
						+ "\t#inf_in_w_lambda:\t" + inf_cntr_w_lambda);
			}
			if ((nan_cntr_v_mu > 0) || (inf_cntr_v_mu > 0)) {
				LOG.debug("#nans in v_mu:\t" + nan_cntr_v_mu
						+ "\t#inf_in_v_mu:\t" + inf_cntr_v_mu);
			}
			if ((nan_cntr_v_lambda > 0) || (inf_cntr_v_lambda > 0)) {
				LOG.debug("#nans in v_lambda:\t" + nan_cntr_v_lambda
						+ "\t#inf_in_v_lambda:\t" + inf_cntr_v_lambda);
			}

			// predict test and train
			predict_data_and_write_to_eterms(main_data, main_cache);
			// (prediction of train is not necessary but it increases numerical
			// stability)

			double acc_train = 0.0;
			double rmse_train = 0.0;
			if (task == TaskType.Regression) {
				// evaluate test and store it
				for (int c = 0; c < test.num_cases; c++) {
					double p = cache_test[c].e;
					pred_this.set(c, p);
					p = Math.min(max_target, p);
					p = Math.max(min_target, p);
					pred_sum_all.set(c, pred_sum_all.get(c) + p);
					if (i >= 5) {
						pred_sum_all_but5.set(c, pred_sum_all_but5.get(c) + p);
					}
				}

				// Evaluate the training dataset and update the e-terms
				for (int c = 0; c < train.num_cases; c++) {
					double p = cache[c].e;
					p = Math.min(max_target, p);
					p = Math.max(min_target, p);
					double err = p - train.target.get(c);
					rmse_train += err * err;
					cache[c].e = cache[c].e - train.target.get(c);
				}
				rmse_train = Math.sqrt(rmse_train / train.num_cases);

			} else if (task == TaskType.Classification) {
				// evaluate test and store it
				for (int c = 0; c < test.num_cases; c++) {
					double p = cache_test[c].e;
					p = StaticFunctions.cdf_gaussian(p);
					pred_this.set(c, p);
					pred_sum_all.set(c, pred_sum_all.get(c) + p);
					if (i >= 5) {
						pred_sum_all_but5.set(c, pred_sum_all_but5.get(c) + p);
					}
				}

				// Evaluate the training dataset and update the e-terms
				int _acc_train = 0;
				for (int c = 0; c < train.num_cases; c++) {
					double p = cache[c].e;
					p = StaticFunctions.cdf_gaussian(p);
					if (((p >= 0.5) && (train.target.get(c) > 0.0))
							|| ((p < 0.5) && (train.target.get(c) < 0.0))) {
						_acc_train++;
					}

					double sampled_target;
					if (train.target.get(c) >= 0.0) {
						if (do_sample) {
							sampled_target = StaticFunctions
									.ran_left_tgaussian(0.0, cache[c].e, 1.0);
						} else {
							// the target is the expected value of the truncated
							// normal
							double mu = cache[c].e;
							double phi_minus_mu = Math.exp(-mu * mu / 2.0)
									/ Math.sqrt(3.141 * 2);
							double Phi_minus_mu = StaticFunctions
									.cdf_gaussian(-mu);
							sampled_target = mu + phi_minus_mu
									/ (1 - Phi_minus_mu);
						}
					} else {
						if (do_sample) {
							sampled_target = StaticFunctions
									.ran_left_tgaussian(0.0, cache[c].e, 1.0);
						} else {
							// the target is the expected value of the truncated
							// normal
							double mu = cache[c].e;
							double phi_minus_mu = Math.exp(-mu * mu / 2.0)
									/ Math.sqrt(3.141 * 2);
							double Phi_minus_mu = StaticFunctions
									.cdf_gaussian(-mu);
							sampled_target = mu - phi_minus_mu / Phi_minus_mu;
						}
					}
					cache[c].e = cache[c].e - sampled_target;
				}
				acc_train = (double) _acc_train / train.num_cases;

			} else {
				throw new IllegalArgumentException();
			}

			// Evaluate the test data sets
			if (task == TaskType.Regression) {
				double rmse_test_this = 0, mae_test_this = 0, rmse_test_all = 0, mae_test_all = 0, rmse_test_all_but5 = 0, mae_test_all_but5 = 0;
				double[] rmseWrapperThis = new double[] { rmse_test_this };
				double[] maeWrapperThis = new double[] { mae_test_this };
				double[] rmseWrapperAll = new double[] { rmse_test_all };
				double[] maeWrapperAll = new double[] { mae_test_all };
				double[] maeWrapperAllBut5 = new double[] { mae_test_all_but5 };
				double[] rmseWrapperAllBut5 = new double[] { rmse_test_all_but5 };

				_evaluate(pred_this, test.target, 1.0, rmseWrapperThis,
						maeWrapperThis, num_eval_cases);
				_evaluate(pred_sum_all, test.target, 1.0 / (i + 1),
						rmseWrapperAll, maeWrapperAll, num_eval_cases);
				_evaluate(pred_sum_all_but5, test.target, 1.0 / (i - 5 + 1),
						rmseWrapperAllBut5, maeWrapperAllBut5, num_eval_cases);

				if (output)
					LOG.debug("#Iter=" + String.format("%3d", i) + " Train="
							+ String.format("%.5f", rmse_train));

				if (log != null) {
					log.log("rmse", rmseWrapperAll[0]);
					log.log("mae", maeWrapperAll[0]);
					log.log("rmse_mcmc_this", rmseWrapperThis[0]);
					log.log("rmse_mcmc_all", rmseWrapperAll[0]);
					log.log("rmse_mcmc_all_but5", rmseWrapperAllBut5[0]);

					if (num_eval_cases < test.target.dim) {
						double rmse_test2_this = 0, mae_test2_this = 0, rmse_test2_all = 0, mae_test2_all = 0;// ,
																												// rmse_test2_all_but5,
																												// mae_test2_all_but5;
						rmseWrapperThis = new double[] { rmse_test2_this };
						maeWrapperThis = new double[] { mae_test2_this };
						rmseWrapperAll = new double[] { rmse_test2_all };
						maeWrapperAll = new double[] { mae_test2_all };
						_evaluate(pred_this, test.target, 1.0, rmseWrapperThis,
								maeWrapperThis, num_eval_cases, test.target.dim);
						_evaluate(pred_sum_all, test.target, 1.0 / (i + 1),
								rmseWrapperAll, maeWrapperAll, num_eval_cases,
								test.target.dim);
					}
					log.newLine();
				}
			} else if (task == TaskType.Classification) {
				double acc_test_this = 0, acc_test_all = 0, acc_test_all_but5 = 0, ll_test_this = 0, ll_test_all = 0, ll_test_all_but5 = 0;
				double[] accWrapper = new double[] { acc_test_this };
				double[] llWrapper = new double[] { ll_test_this };
				double[] acc2Wrapper = new double[] { acc_test_all };
				double[] ll2Wrapper = new double[] { ll_test_all };
				double[] accWrapperBut5 = new double[] { acc_test_all_but5 };
				double[] llWrapperBut5 = new double[] { ll_test_all_but5 };
				_evaluate_class(pred_this, test.target, 1.0, accWrapper,
						llWrapper, num_eval_cases);
				_evaluate_class(pred_sum_all, test.target, 1.0 / (i + 1),
						acc2Wrapper, ll2Wrapper, num_eval_cases);
				_evaluate_class(pred_sum_all_but5, test.target,
						1.0 / (i - 5 + 1), accWrapperBut5, llWrapperBut5,
						num_eval_cases);

				LOG.debug("#Iter=" + i + "\tTrain=" + acc_train + "\tTest="
						+ acc_test_all + "\tTest(ll)=" + ll_test_all);
				if (log != null) {
					log.log("accuracy", acc2Wrapper[0]);
					log.log("acc_mcmc_this", accWrapper[0]);
					log.log("acc_mcmc_all", acc2Wrapper[0]);
					log.log("acc_mcmc_all_but5", accWrapperBut5[0]);
					log.log("ll_mcmc_this", llWrapper[0]);
					log.log("ll_mcmc_all", ll2Wrapper[0]);
					log.log("ll_mcmc_all_but5", llWrapperBut5[0]);

					if (num_eval_cases < test.target.dim) {

						double acc_test2_this = 0, acc_test2_all = 0, ll_test2_this = 0, ll_test2_all = 0;
						accWrapper = new double[] { acc_test2_this };
						llWrapper = new double[] { ll_test2_this };
						acc2Wrapper = new double[] { acc_test2_all };
						ll2Wrapper = new double[] { ll_test2_all };
						_evaluate_class(pred_this, test.target, 1.0,
								accWrapper, llWrapper, num_eval_cases,
								test.target.dim);
						_evaluate_class(pred_sum_all, test.target,
								1.0 / (i + 1), acc2Wrapper, ll2Wrapper,
								num_eval_cases, test.target.dim);
					}
					log.newLine();
				}

			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	void _evaluate(DVectorDouble pred, DVectorFloat target, double normalizer, double[] rmse, double[] mae, int from_case, int to_case) {
		// /Note: Call by reference emulation via arrays for variables rmse, mae
		double _rmse = 0;
		double _mae = 0;
		int num_cases = 0;
		for (int c = Math.max((int) 0, from_case); c < Math.min((int) pred.dim,
				to_case); c++) {
			double p = pred.get(c) * normalizer;
			p = Math.min(max_target, p);
			p = Math.max(min_target, p);
			double err = p - target.get(c);
			_rmse += err * err;
			_mae += Math.abs((double) err);
			num_cases++;
		}

		rmse[0] = Math.sqrt(_rmse / num_cases);
		mae[0] = _mae / num_cases;

	}

	void _evaluate_class(DVectorDouble pred, DVectorFloat target, double normalizer, double[] accuracy, double[] loglikelihood, int from_case, int to_case) {
		///Note: Call by reference emulation via arrays for variables accuracy,
		// loglikelihood
		double _loglikelihood = 0.0;
		int _accuracy = 0;
		int num_cases = 0;
		for (int c = Math.max((int) 0, from_case); c < Math.min((int) pred.dim,
				to_case); c++) {
			double p = pred.get(c) * normalizer;
			if (((p >= 0.5) && (target.get(c) > 0.0))
					|| ((p < 0.5) && (target.get(c) < 0.0))) {
				_accuracy++;
			}
			double m = (target.get(c) + 1.0) * 0.5;
			double pll = p;
			if (pll > 0.99) {
				pll = 0.99;
			}
			if (pll < 0.01) {
				pll = 0.01;
			}
			_loglikelihood -= m * Math.log10(pll) + (1 - m)
					* Math.log10(1 - pll);
			num_cases++;
		}
		loglikelihood[0] = _loglikelihood / num_cases;
		accuracy[0] = (double) _accuracy / num_cases;
	}

	void _evaluate(DVectorDouble pred, DVectorFloat target, double normalizer, double[] rmse, double[] mae, int num_eval_cases) {
		///Note: Call by reference emulation via arrays for variables rmse, mae
		_evaluate(pred, target, normalizer, rmse, mae, 0, num_eval_cases);
	}

	void _evaluate_class(DVectorDouble pred, DVectorFloat target, double normalizer, double[] accuracy, double[] loglikelihood, int num_eval_cases) {
		///Note: Call by reference emulation via arrays for variables accuracy,
		// loglikelihood
		_evaluate_class(pred, target, normalizer, accuracy, loglikelihood, 0,
				num_eval_cases);
	}
}
