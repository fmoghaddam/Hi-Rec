package algorithms.fm;

import org.apache.log4j.Logger;


/**
 * This class holds the model parameters for the factorization machine, that are needed to make predictions
 */
public class fm_model {
		private static final Logger LOG = Logger.getLogger(fm_model.class.getCanonicalName());
		public DVectorDouble m_sum = new DVectorDouble(), m_sum_sqr = new DVectorDouble();
    	public double w0;
		public DVectorDouble w = new DVectorDouble();
		public DMatrixDouble v = new DMatrixDouble();

		public int num_attribute;
		
		public boolean k0, k1;
		public int num_factor;
		
		public double reg0;
		public double regw, regv;
		
		public double init_stdev;
		public double init_mean;

		public fm_model(){
                    num_factor = 0;
                    init_mean = 0;
                    init_stdev = 0.0;
                    reg0 = 0.0;
                    regw = 0.0;
                    regv = 0.0; 
                    k0 = true;
                    k1 = true;
		}
                
		public void debug(){
                    LOG.info("num_attributes=" + num_attribute );
                    LOG.info("use w0=" + k0 );
                    LOG.info("use w1=" + k1 );
                    LOG.info("dim v =" + num_factor );
                    LOG.info("reg_w0=" + reg0 );
                    LOG.info("reg_w=" + regw );
                    LOG.info("reg_v=" + regv ); 
                    LOG.info("init ~ N(" + init_mean + "," + init_stdev + ")" );
        }
		
		public void init(){
                    w0 = 0;
                    w.setSize(num_attribute);
                    v.setSize(num_factor, num_attribute);
                    w.init(0.0);
                    v.init(init_mean, init_stdev);
                    m_sum.setSize(num_factor);
                    m_sum.init(0.0);
                    m_sum_sqr.setSize(num_factor);
                    m_sum_sqr.init(0.0);
        }
                
		/**
		 * This is the default prediction method for SGD and SGDA
		 * @param x The feature row, for which a prediction is to be made
		 * @param sum
		 * @param sum_sqr
		 * @return The prediction
		 */
		public double predict(sparse_rowFloat x, DVectorDouble sum, DVectorDouble sum_sqr){
                    double result = 0;
                    if (k0) {	
                            result += w0;
                    }
                    if (k1) {
                            for (int i = 0; i < x.size; i++) {
                                    result += w.get(x.data[i].id) * x.data[i].value;
                            }
                    }
                    for (int f = 0; f < num_factor; f++) {
                            sum.set(f,0.0);
                            sum_sqr.set(f, 0.0);
                            for (int i = 0; i < x.size; i++) {
                                    double d = v.get(f,x.data[i].id) * x.data[i].value;
                                    double tempSum = sum.get(f) + d;
                                    sum.set(f, tempSum);
                                    double tempSum2 = sum_sqr.get(f) + d*d;
                                    sum_sqr.set(f, tempSum2);
                            }
                            result += 0.5 * (sum.get(f)*sum.get(f) - sum_sqr.get(f));
                    }
                    return result;
                }
                
        public double predict(sparse_rowFloat x){
            return predict(x, m_sum, m_sum_sqr);
        }
}
