package gui.controller.executor;

import com.google.common.eventbus.Subscribe;
import gui.messages.FoldLevelUpdateMessage;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Callback;
import run.ConfigRunResult;
import util.MessageBus;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AlgorithmExecutorController implements Initializable {

    @FXML
    private Label algorithmNameLabel;

    @FXML
    private TableView<String[]> accuracyResultTableView;

    @FXML
    private FlowPane foldStatusHolderBox;

    private Tab tabHolder;

    private List<Label> progressIndicatorList;

    private int algorithmId;
    private int numberOfFolds;

    private ConfigRunResult configRunResult;

    public Tab getTabHolder() {
        return tabHolder;
    }

    public void setTabHolder(Tab tabHolder) {
        this.tabHolder = tabHolder;
    }

    public void setDetail(String algorithmName, int id, int numberOfFolds) {
        algorithmId = id;
        this.numberOfFolds = numberOfFolds;
        Platform.runLater(() -> {
            algorithmNameLabel.setText("ID : " + id + " - Algorithm: " + algorithmName);
            for (int i = 0; i < numberOfFolds; i++) {
                Label label = new Label();
                label.setFont(Font.font("Calibri", 16));
                ProgressIndicator progressIndicator = new ProgressIndicator();
                progressIndicator.setMinSize(30, 30);
                progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                label.setGraphicTextGap(25);
                label.setGraphic(progressIndicator);
                progressIndicatorList.add(label);
                label.setText("Fold " + (i + 1) + " : WAITING FOR SCHEDULE");
                foldStatusHolderBox.getChildren().add(label);
            }
        });

    }

    @Subscribe
    public void update(final FoldLevelUpdateMessage message) {
        Platform.runLater(() -> {
            if (message.getAlgorithmId() == algorithmId) {
                Label label = progressIndicatorList.get(message.getFoldId() - 1);
                if (!tabHolder.isSelected()) {
                    Circle notificationCircle = (Circle) tabHolder.getGraphic();
                    notificationCircle.setFill(Color.RED);
                }
                ProgressIndicator progressIndicator = (ProgressIndicator) label.getGraphic();
                label.setText("Fold " + message.getFoldId() + " : " + message.getStatus());
                switch (message.getStatus()) {
                    case STARTED:
                        progressIndicator.setStyle("-fx-progress-color: BLUE;");
                        break;
                    case TRAINING:
                        progressIndicator.setStyle("-fx-progress-color: Yellow;");
                        break;
                    case TESTING:
                        progressIndicator.setStyle("-fx-progress-color: RED;");
                        break;
                    case FINISHED:
                        progressIndicator.setStyle("-fx-progress-color: GREEN;");
                        progressIndicator.setProgress(100);
                    default:
                        break;
                }

            }
        });
    }

    public void showResult(ConfigRunResult configRunResult) {
        this.configRunResult = configRunResult;
        Platform.runLater(() -> {
            String[][] resultTable = configRunResult.asTable();
            ObservableList<String[]> data = FXCollections.observableArrayList();
            data.addAll(Arrays.asList(resultTable));
            data.remove(0);//remove titles from data
            for (int i = 0; i < resultTable[0].length; i++) {
                TableColumn tc = new TableColumn(resultTable[0][i]);
                final int colNo = i;
                tc.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) p ->
                        new SimpleStringProperty((p.getValue()[colNo])));
                tc.setPrefWidth(90);
                accuracyResultTableView.getColumns().add(tc);
            }
            accuracyResultTableView.setItems(data);
        });
    }

    @FXML
    void exportToTableButtonOnAction(ActionEvent event) {
        if (configRunResult != null) {
            String toTabSeperatedTable = configRunResult.toTabSeperatedTable();
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(toTabSeperatedTable);
            clipboard.setContent(content);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Result as Table");
            alert.setHeaderText("Table Copied Into Your Clipboard");
            alert.setContentText(toTabSeperatedTable);
            alert.showAndWait();
        }
    }

    @FXML
    void exportToLatexButtonOnAction(ActionEvent event) {
        if (configRunResult != null) {
            String toLatexTable = configRunResult.toLatexTable();
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(toLatexTable);
            clipboard.setContent(content);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Result as Latex Table");
            alert.setHeaderText("Latex Table Copied Into Your Clipboard");
            alert.setContentText(toLatexTable);
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressIndicatorList = new ArrayList<>();
        MessageBus.getInstance().register(this);
        accuracyResultTableView.setPlaceholder(new Label("Waiting for results ..."));
    }
}
