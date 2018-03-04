package onethreeseven.clustering;

import javafx.stage.Stage;
import onethreeseven.clustering.model.KMeansCluster;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.util.DataGeneratorUtil;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.model.WrappedEntity;
import onethreeseven.trajsuitePlugin.model.WrappedEntityLayer;
import onethreeseven.trajsuitePlugin.view.BasicFxApplication;

import java.util.Collection;
import java.util.Map;

/**
 * Todo: write documentation
 *
 * @author Luke Bermingham
 */
public class Main extends BasicFxApplication {

    @Override
    protected BaseTrajSuiteProgram preStart(Stage stage) {
        BaseTrajSuiteProgram prog = BaseTrajSuiteProgram.getInstance();

        Map<String, STTrajectory> trajs = DataGeneratorUtil.generateSpatiotemporalTrajectories(25, 1000, 0, 0, 3, 10, 5000);

        for (Map.Entry<String, STTrajectory> entry : trajs.entrySet()) {
            WrappedEntity entity = prog.getLayers().add("Trajectories", entry.getKey(), entry.getValue());
            entity.isSelectedProperty().setValue(true);
        }

        //do kmeans by calling the command
        prog.getCLI().doCommand(new String[]{"kmeans", "-k", "3"});

        //should now have k-means cluster in layers

        prog.getCLI().doCommand(new String[]{"le"});

        prog.getCLI().startListeningForInput();

        return prog;

    }

    @Override
    public String getTitle() {
        return "Clustering";
    }

    @Override
    public int getStartWidth() {
        return 640;
    }

    @Override
    public int getStartHeight() {
        return 480;
    }
}
