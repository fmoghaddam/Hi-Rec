/**
 * 
 */
package gui.model;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author Farshad Moghaddam
 *
 */
public class ErrorMessage extends Label {

	public ErrorMessage() {		
		this.setTextFill(Color.web("#FF0000"));
		this.setFont(new Font("Arial", 16));
	}
}
