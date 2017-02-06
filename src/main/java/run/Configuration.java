package run;

import java.io.Serializable;

import interfaces.AbstractRecommender;
import model.DataType;
import util.TimeUtil;

/**
 * Running Configuration
 * 
 * @author FBM
 *
 */
public final class Configuration implements Serializable {

	/**
	 * Unique id used for serialization
	 */
	private static final long serialVersionUID = -8054655100563267008L;
	private final int id;
	private final AbstractRecommender algorithm;
	private final DataType dataType;
	private transient final TimeUtil timeUtil= new TimeUtil();

	/**
	 * @param algorithm 
	 * @param dataType
	 */
	public Configuration(
			int id, AbstractRecommender algorithm, DataType dataType)
	{
		if (algorithm == null) {
			throw new IllegalArgumentException("Algorithm is null");
		}
		this.id = id;
		this.algorithm = algorithm;
		this.dataType = dataType;
	}
	/**
	 * @return the algorithm
	 */
	public final
	AbstractRecommender getAlgorithm() {
		return algorithm;
	}

	/**
	 * @return the id
	 */
	public final
	int getId() {
		return id;
	}

	/**
	 * @return the timeUtil
	 */
	public TimeUtil getTimeUtil() {
		return timeUtil;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Configuration other = (Configuration) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (dataType != other.dataType)
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Configuration [id=" + id + ", algorithm=" + algorithm + ", dataType=" + dataType + "]";
	}
	public DataType getDataType() {
		return dataType;
	}
	
}
