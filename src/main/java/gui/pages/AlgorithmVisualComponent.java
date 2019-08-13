package gui.pages;

import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.Subscribe;

import gui.messages.FoldLevelUpdateMessage;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import util.MessageBus;

/**
 * @author FBM
 *
 */
public class AlgorithmVisualComponent {

	private int algorithmId;
	private Label algorithmName;

	private Map<Integer,CircleComponent> circleMap;

	/**
	 * @param id
	 * @param algorithmName
	 * @param numberOfFold
	 */
	public AlgorithmVisualComponent(int id, String algorithmName, int numberOfFold) {
		this.algorithmId = id;
		this.algorithmName = new Label(algorithmName);
		this.circleMap = new HashMap<>();
		for(int i=1;i<=numberOfFold;i++){
			circleMap.put(i,new CircleComponent("Fold "+i));
		}
		MessageBus.getInstance().register(this);
	}

	public Parent getLayout(){
		final VBox circles = new VBox(5.0);
		for(CircleComponent c:circleMap.values()){
			circles.getChildren().add(c.getLayout());
		}

		final VBox main = new VBox(5.0, algorithmName, circles);
		main.setMinWidth(320);
		main.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
				+ "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: gray;");
		return main;
	}

	@Subscribe
	public void update(final FoldLevelUpdateMessage message) {
		Platform.runLater(() -> {
		if(message.getAlgorithmId() == algorithmId){
			final CircleComponent circleComponent = circleMap.get(message.getFoldId());
			if(circleComponent!=null){
				switch (message.getStatus()) {
				case STARTED:
					circleComponent.setColor(Color.RED);
					circleComponent.startBlinking();					
					circleComponent.setStatusLabel(FoldStatus.STARTED.getText());
					break;
				case TRAINING:
					circleComponent.setColor(Color.YELLOW);
					circleComponent.setStatusLabel(FoldStatus.TRAINING.getText());
					break;
				case TESTING:
					circleComponent.setColor(Color.AQUA);
					circleComponent.setStatusLabel(FoldStatus.TESTING.getText());
					break;
				case FINISHED:
					circleComponent.setColor(Color.GREENYELLOW);
					circleComponent.stopBlinking();
					circleComponent.setStatusLabel(FoldStatus.FINISHED.getText());
					break;
				default:
					break;
				}
			}
		}
		});
	}
}
