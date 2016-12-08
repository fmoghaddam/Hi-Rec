/**
 * 
 */
package gui.pages;

/**
 * @author Farshad Moghaddam
 *
 */
public class FoldLevelUpdateMessage {
	private final int algorithmId;
	private final int foldId;
	private final FoldStatus status;
	/**
	 * @param algorithmId
	 * @param foldId
	 * @param status
	 */
	public FoldLevelUpdateMessage(int algorithmId, int foldId, FoldStatus status) {
		super();
		this.algorithmId = algorithmId;
		this.foldId = foldId;
		this.status = status;
	}
	/**
	 * @return the algorithmId
	 */
	public final int getAlgorithmId() {
		return algorithmId;
	}
	/**
	 * @return the foldId
	 */
	public final int getFoldId() {
		return foldId;
	}
	/**
	 * @return the status
	 */
	public final FoldStatus getStatus() {
		return status;
	}
	
}
