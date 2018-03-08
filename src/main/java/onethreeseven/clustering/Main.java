package onethreeseven.clustering;

import javafx.stage.Stage;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.util.DataGeneratorUtil;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.model.WrappedEntity;
import onethreeseven.trajsuitePlugin.view.BasicFxApplication;
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

        String layername = "Trajectories";

        for (Map.Entry<String, STTrajectory> entry : trajs.entrySet()) {
            String entityId = entry.getKey();
            prog.getLayers().add(layername, entityId, entry.getValue());
            prog.getLayers().getEntity(layername, entityId).isSelectedProperty().setValue(true);
        }

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
