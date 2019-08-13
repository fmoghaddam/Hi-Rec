package util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.Arrays;

public class FXUtil {

    public static TableView<String[]> convertString2DArrayToFXTable(String[][] resultTable) {
        TableView<String[]> tableView = new TableView<>();
        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(resultTable));
        data.remove(0);//remove titles from data
        for (int i = 0; i < resultTable[0].length; i++) {
            TableColumn tc = new TableColumn(resultTable[0][i]);
            final int colNo = i;
            tc.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) p ->
                    new SimpleStringProperty((p.getValue()[colNo])));
            tc.setPrefWidth(200);
            tableView.getColumns().add(tc);
        }
        tableView.setItems(data);
        return tableView;
    }
}
