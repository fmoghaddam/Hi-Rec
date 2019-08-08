package gui.controller.executor;

import com.google.common.eventbus.Subscribe;
import controller.DataLoader;
import gui.controller.HomePageController;
import gui.messages.AlgorithmLevelUpdateMessage;
import gui.messages.CalculationDoneMessage;
import gui.messages.ShutdownFinishedMessage;
import gui.messages.StopAllRequestMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.DataModel;
import model.Globals;
import org.apache.commons.math3.stat.Frequency;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import run.ConfigRunResult;
import run.ParallelEvaluator;
import util.MessageBus;
import util.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.Future;

import static util.FXUtil.convertString2DArrayToFXTable;

public class ExecutorMainPageController implements Initializable {
    private Map<String, Pair<Parent, AlgorithmExecutorController>> algorithms;
    private Pair<Parent, HomePageController> homePage;
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab dataModelTab;

    @FXML
    private VBox dataModelSummaryVBox;

    @FXML
    private TextArea logTextArea;

    @FXML
    private Button stopButton;

    @FXML
    private Button homePageButton;

    private Thread thread;

    private static String[][] convertFrequencyTableToString2DTable(DataModel dataModel) {
        Frequency freq = dataModel.getFreq();
        String[][] table = new String[freq.getUniqueCount() + 1][];
        NumberFormat nf = NumberFormat.getPercentInstance();

        table[0] = new String[]{"Value", "Frequency", "Percentage", "Cumulative Percentage"};
        Iterator<Comparable<?>> iter = freq.valuesIterator();
        int row = 1;
        while (iter.hasNext()) {
            Comparable<?> value = iter.next();
            table[row] = new String[]{String.valueOf(value), String.valueOf(freq.getCount(value))
                    , nf.format(freq.getPct(value)), nf.format(freq.getCumPct(value))};
            row++;
        }

        return table;
    }

    private static String[][] getDataModelStatisticTable(DataModel dataModel) {
        String[][] table = new String[9][2];
        table[0] = new String[]{" ", "Value"};

        final DecimalFormat decimalFormat = new DecimalFormat("#.####");

        table[1] = new String[]{"#Users: ", String.valueOf(dataModel.getNumberOfUsers())};
        table[2] = new String[]{"#Items: ", String.valueOf(dataModel.getNumberOfRatings())};
        table[3] = new String[]{"#Ratings: ", String.valueOf(dataModel.getNumberOfRatings())};


        table[4] = new String[]{"Density: ", decimalFormat.format((double) dataModel.getNumberOfRatings() * 1.0
                / (dataModel.getNumberOfItems() * 1.0
                * dataModel.getNumberOfUsers() * 1.0))};

        table[5] = new String[]{"Avg. Ratings/user: ", decimalFormat
                .format((double) dataModel.getNumberOfRatings() / dataModel.getNumberOfUsers())};

        table[6] = new String[]{"Avg. Ratings/item: ", decimalFormat
                .format((double) dataModel.getNumberOfRatings() / dataModel.getNumberOfItems())};

        table[7] = new String[]{"Max rating: ", String.valueOf(Globals.MAX_RATING)};
        table[8] = new String[]{"Min rating: ", String.valueOf(Globals.MIN_RATING)};

        return table;
    }

    @FXML
    void homePageButtonOnAction(ActionEvent event) {
        homePage.getSecond().goToHomePage();
    }

    @FXML
    void onStopButtonAction(ActionEvent event) {
        thread.interrupt();
        MessageBus.getInstance().getBus().post(new StopAllRequestMessage());
        stopButton.setDisable(true);
        homePageButton.setDisable(false);
    }

    public void setHomePage(Pair<Parent, HomePageController> homePage) {
        this.homePage = homePage;
    }

    @FXML
    void runConfigMenuOnAction(ActionEvent event) {
        execute();
    }

    private void CleanLogFile() {
        try {
            Files.deleteIfExists(Paths.get("log/Recommender.log"));
        } catch (NoSuchFileException x) {
        } catch (DirectoryNotEmptyException x) {
        } catch (IOException x) {
        }
    }

    /**
     * Start the application in integrated mode
     */
    public void execute() {
        if (homePageButton.isDisable()) {
            return;
        }
        if (thread != null && thread.isAlive()) {
            return;
        }
        thread = new Thread(() -> {
            try {
                CleanLogFile();
                Globals.readData();
                final DataLoader loader = new DataLoader();
                final DataModel dataModel = loader.readData();
                String[][] dataModelStatisticTable = getDataModelStatisticTable(dataModel);
                TableView<String[]> tableView = convertString2DArrayToFXTable(dataModelStatisticTable);

                TableView<String[]> freqTable =
                        convertString2DArrayToFXTable(convertFrequencyTableToString2DTable(dataModel));
                Platform.runLater(() -> {
                    dataModelSummaryVBox.getChildren().add(tableView);
                    dataModelSummaryVBox.getChildren().add(freqTable);
                });

                if (!Thread.interrupted()) {
                    final ParallelEvaluator evaluator = new ParallelEvaluator(dataModel);
                    List<Future<ConfigRunResult>> evaluate = evaluator.evaluate();
                    for (Future<ConfigRunResult> configRunResultFuture : evaluate) {
                        ConfigRunResult configRunResult = configRunResultFuture.get();
                        ExecutorMainPageController.this.algorithms.get(configRunResult.getConfiguration().getAlgorithm() + "#" +
                                configRunResult.getConfiguration().getId()).getSecond().showResult(configRunResult);
                    }
                }
            } catch (final Exception exception) {

            }
        });
        thread.setDaemon(true);
        thread.start();
        homePageButton.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithms = new HashMap<>();
        MessageBus.getInstance().register(this);
        AppenderSkeleton appenderSkeleton = new AppenderSkeleton() {
            @Override
            public void close() {
            }

            @Override
            public boolean requiresLayout() {
                return false;
            }

            @Override
            protected void append(LoggingEvent event) {
                Platform.runLater(() -> {
                    if (event == null || event.getMessage() == null) {
                        return;
                    }
                    logTextArea.appendText("\n" + event.getMessage().toString());
                });
            }
        };
        Logger.getRootLogger().addAppender(appenderSkeleton);
    }


    @Subscribe
    private void enableGoToFirstPgaeButton(final CalculationDoneMessage message) {
        logTextArea.appendText("Calculation Done!");
        stopButton.setDisable(true);
        homePageButton.setDisable(false);
    }

    @Subscribe
    private void enableStopButton(final ShutdownFinishedMessage message) {
        stopButton.setDisable(false);
        homePage.getSecond().initializeExecutorPage();
    }


    @Subscribe
    private void addAlgorithmComponent(final AlgorithmLevelUpdateMessage message) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/AlgorithmExecutorFXMLView.fxml"));
            try {

                Parent root = fxmlLoader.load();
                AlgorithmExecutorController controller = fxmlLoader.getController();
                controller.setDetail(message.getAlgorithmName(), message.getId(), message.getNumberOfFold());
                ScrollPane scrollPane = new ScrollPane();

                scrollPane.setFitToHeight(true);
                scrollPane.setFitToWidth(true);
                scrollPane.setContent(root);

                Tab tab = new Tab(message.getAlgorithmName(), scrollPane);
                tab.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        Circle notificationCircle = (Circle) tab.getGraphic();
                        notificationCircle.setFill(Color.TRANSPARENT);
                    }
                });

                tab.setGraphic(new Circle(4, Color.RED));
                tab.setClosable(false);
                controller.setTabHolder(tab);
                tabPane.getTabs().add(tab);
                algorithms.put(message.getAlgorithmName() + "#" + message.getId(), new Pair<>(root, controller));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
