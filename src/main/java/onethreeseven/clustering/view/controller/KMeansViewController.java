package onethreeseven.clustering.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import onethreeseven.clustering.command.KmeansCommand;
import onethreeseven.jclimod.CLIProgram;

/**
 * Controller for k-means clustering ui.
 * @author Luke Bermingham
 */
public class KMeansViewController extends AbstractClusterViewController {

    @FXML
    public Spinner<Integer> kSpinner;

    @FXML
    public Label selectedEntitiesLabel;

    @FXML
    public Button clusterBtn;

    @FXML
    public ProgressBar progressBar;

    @FXML
    protected void initialize(){

        this.kSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 5, 1));

        this.clusterBtn.setDisable(true);

        progressBar.setDisable(true);

        calculatePointsToCluster();

    }

    @FXML
    public void onClusterClicked(ActionEvent actionEvent) {
        doClustering();
    }

    @Override
    protected void setSpinnersDisabled(boolean disabled) {
        kSpinner.setDisable(disabled);
    }

    @Override
    protected boolean doClusteringImpl(double[][] pts) {
        final int k = kSpinner.getValue();
        CLIProgram prog = new CLIProgram();
        KmeansCommand command = new KmeansCommand();
        prog.addCommand(command);
        String[] commandStr = new String[]{"kmeans", "-k", String.valueOf(k)};
        return prog.doCommand(commandStr);
    }

    @Override
    protected ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    protected Label getSelectedEntitiesLabel() {
        return selectedEntitiesLabel;
    }

    @Override
    protected Button getClusterButton() {
        return clusterBtn;
    }
}
