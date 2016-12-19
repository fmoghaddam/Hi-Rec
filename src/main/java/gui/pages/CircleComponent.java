package gui.pages;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * @author Farshad Moghaddam
 *
 */
public class CircleComponent {
	private Label foldNameLabel;
	private Label statusLabel;
	private Circle circle;
	private boolean blinking;
	private Timeline timeline;
	private Color color = Color.RED;

	/**
	 * @param label
	 */
	public CircleComponent(final String label) {
		this.foldNameLabel = new Label(label);
		this.circle = new Circle(10);
		this.circle.setFill( Color.TRANSPARENT ) ;
		this.circle.setStroke(Color.BLACK);
		this.blinking = false;
		this.statusLabel = new Label();
	}

	/**
	 * @param color the color to set
	 */
	public final void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Stop circle blinking by set its color to transparent.
	 */
	public void stopBlinking() {
		if(this.blinking){	
			this.blinking = false;
			timeline.stop();			
			this.circle.setFill(color) ;
		}		
	}

	/**
	 * Change the color of circle and make it to blink.
	 */
	public void startBlinking() {		
		if(!this.blinking) {
			this.blinking = true;
			timeline = new Timeline();
			this.timeline.setCycleCount(Animation.INDEFINITE) ;
			final EventHandler<ActionEvent> onFinished = (ActionEvent event) ->
			{
				if (this.circle.getFill()==color){
					this.circle.setFill(Color.TRANSPARENT) ;
				}
				else{
					this.circle.setFill(color) ;
				}	        
			};
			final KeyFrame keyframe = new KeyFrame(Duration.millis(500),onFinished ) ;
			this.timeline.getKeyFrames().add( keyframe ) ;
			this.timeline.play();			
		}
	}
	
	public Parent getLayout(){
		final HBox hBox = new HBox(10.0,foldNameLabel,circle,statusLabel);
		return hBox;
	}

	/**
	 * @param string
	 */
	public void setStatusLabel(String string) {
		statusLabel.setText(string);
	}
	
}
