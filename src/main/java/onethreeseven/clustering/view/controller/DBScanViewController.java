package onethreeseven.clustering.view.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import onethreeseven.clustering.command.DBScanCommand;
import onethreeseven.jclimod.CLIProgram;

import java.util.function.Consumer;

/**
 * View controller for ui of {@link DBScanCommand}
 * @author Luke Bermingham
 */
public class DBScanViewController extends AbstractClusterViewController {

    @FXML
    public Label nSelectedEntitiesLabel;
    @FXML
    public Spinner<Double> epsilonMetresSpinner;
    @FXML
    public Spinner<Integer> minPtsSpinner;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Button clusterBtn;

    private final Consumer<Double> progressListener = new Consumer<>() {
        @Override
        public void accept(Double progress) {
            if (progressBar != null) {
                Platform.runLater(() -> {
                    progressBar.setProgress(progress);
                });
            }
        }
    };

    @FXML
    protected void initialize(){

        this.epsilonMetresSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 1000000, 5, 1));

        this.minPtsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000000, 10, 1));

        this.clusterBtn.setDisable(true);

        progressBar.setDisable(true);

        calculatePointsToCluster();

    }

    public void onClusterClicked(ActionEvent actionEvent) {
        doClustering();
        progressBar.setProgress(0);
    }

    @Override
    protected void setSpinnersDisabled(boolean disabled) {
        epsilonMetresSpinner.setDisable(disabled);
        minPtsSpinner.setDisable(disabled);
    }

    @Override
    protected boolean doClusteringImpl(double[][] pts) {
        final double eps = epsilonMetresSpinner.getValue();
        final int minPts = minPtsSpinner.getValue();
        CLIProgram prog = new CLIProgram();
        DBScanCommand command = new DBScanCommand();
        command.setProgressListener(progressListener);
        prog.addCommand(command);
        String[] commandStr = new String[]{"dbscan", "-e", String.valueOf(eps), "-m", String.valueOf(minPts)};
        return prog.doCommand(commandStr);
    }

    @Override
    protected ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    protected Label getSelectedEntitiesLabel() {
        return nSelectedEntitiesLabel;
    }

    @Override
    protected Button getClusterButton() {
        return clusterBtn;
    }
}
