/**
 * 
 */
package gui.messages;

/**
 * @author Farshad Moghaddam
 *
 */
public class AlgorithmLevelUpdateMessage {
	private final int id;
	private final String algorithmName;
	private final int numberOfFold;
	/**
	 * @param id
	 * @param algorithmName
	 * @param nuberOfFold
	 */
	public AlgorithmLevelUpdateMessage(int id, String algorithmName, int nuberOfFold) {
		super();
		this.id = id;
		this.algorithmName = algorithmName;
		this.numberOfFold = nuberOfFold;
	}
	/**
	 * @return the id
	 */
	public final int getId() {
		return id;
	}
	/**
	 * @return the algorithmName
	 */
	public final String getAlgorithmName() {
		return algorithmName;
	}
	/**
	 * @return the nuberOfFold
	 */
	public final int getNumberOfFold() {
		return numberOfFold;
	}
}
