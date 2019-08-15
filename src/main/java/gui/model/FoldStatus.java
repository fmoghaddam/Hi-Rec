/**
 * 
 */
package gui.model;

/**
 * @author Farshad Moghaddam
 *
 */
public enum FoldStatus {
	STARTED("Started"),
	FINISHED("Finished"),
	TRAINING("Training"),
	TESTING("Testing");

	private String text;

    FoldStatus(final String text) {
		this.text = text;
	}

	public String getText(){
		return text;
	}
}
