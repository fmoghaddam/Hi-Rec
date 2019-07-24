package gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author FBM
 *
 */
public enum Metrics {
	RMSE("RMSE"),
	MAE("MAE"),
	Precision("Precision"),
	Recall("Recall"),
	PrecisionCoverage("PredictionCoverage"),
	PredictionCoverageOnlyPositive("PredictionCoverageOnlyPositive"),
	MAP("MAP"),
	NDCG("NDCG"),
	NoveltyOnAll("NoveltyOnAll"),
	NoveltyOnHit("NoveltyOnHit"),
	DiversityLowLevel("DiversityLowLevel"),
	DiversityGenre("DiversityGenre"),
	DiversityTag("DiversityTag");

	private StringProperty text;

	Metrics(String text) {
		this.text = new SimpleStringProperty(text);
	}

	public StringProperty getText(){
		return text;
	}
}
