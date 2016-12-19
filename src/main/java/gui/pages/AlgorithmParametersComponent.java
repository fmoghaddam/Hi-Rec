package gui.pages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import gui.model.ConfigData;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author FBM
 *
 */
public class AlgorithmParametersComponent {

	private GridPane grid;
	private final int id;
	private Map<String,TextField> parametersMap;

	/**
	 * @param id
	 * 
	 */
	public AlgorithmParametersComponent(int id) {
		this.id = id;
		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		parametersMap= new HashMap<>();
	}

	/**
	 * @param map
	 */
	public void setParameters(final Map<String, Map<String, String>> map) {
		int i = 0;		
		this.grid.getChildren().clear();
		this.parametersMap.clear();
		for (Map<String, String> subMap : map.values()) {
			for (Entry<String, String> entity : subMap.entrySet()) {
				final String value = entity.getValue();
				final Label parameterName = new Label(value);
				final TextField parameterValue = new TextField();
				grid.add(parameterName, 0, i);
				grid.add(parameterValue, 1, i);
				i++;
				final String key = "ALGORITHM_" + id + "_" + entity.getKey();
				ConfigData.instance.ALGORITHM_PARAMETERS.put(key, new SimpleStringProperty(""));
				ConfigData.instance.ALGORITHM_PARAMETERS.get(key).bind(parameterValue.textProperty());
				this.parametersMap.put(value,parameterValue);
			}
		}
	}

	public Parent getLayout() {
		return grid;
	}

	/**
	 * @return
	 */
	public String validate() {
		final StringBuilder totalError = new StringBuilder();
		for(Entry<String, TextField> entry:this.parametersMap.entrySet()){
			try{
				Double.parseDouble(entry.getValue().getText());
			}catch(final Exception exception){
				totalError.append("The value of \""+entry.getKey()+"\" is not valid").append("\n");
			}
		}
		return totalError.toString();
	}
}
