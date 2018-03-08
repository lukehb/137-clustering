package onethreeseven.clustering.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import onethreeseven.clustering.view.controller.KMeansViewController;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.view.AbstractMenuBarPopulator;
import onethreeseven.trajsuitePlugin.view.MenuSupplier;
import onethreeseven.trajsuitePlugin.view.TrajSuiteMenu;
import onethreeseven.trajsuitePlugin.view.TrajSuiteMenuItem;
import java.io.IOException;
import java.net.URL;

public class ClusteringMenuSupplier implements MenuSupplier {

    @Override
    public void supplyMenus(AbstractMenuBarPopulator populator, BaseTrajSuiteProgram program, Stage stage) {

        TrajSuiteMenu clusteringMenu = new TrajSuiteMenu("Clustering", 3);

        TrajSuiteMenuItem kmeanMenuItem = new TrajSuiteMenuItem("KMeans", makeKMeansMenuItem(stage));
        TrajSuiteMenuItem dbscanMenuItem = new TrajSuiteMenuItem("DBScan", makeDBScanMenuItem(stage));

        clusteringMenu.addChild(kmeanMenuItem);
        clusteringMenu.addChild(dbscanMenuItem);

        //add the menu
        populator.addMenu(clusteringMenu);

    }

    private Runnable makeView(Stage primaryStage, String resource, String title){
        return () -> {

            URL res = ClusteringMenuSupplier.class.getResource(resource);

            if(res == null){
                throw new IllegalArgumentException(resource + " view fxml resource could not be found.");
            }

            try {

                FXMLLoader loader = new FXMLLoader(res);

                Parent view = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.NONE);
                //stage.setAlwaysOnTop(true);
                stage.initStyle(StageStyle.UTILITY);
                stage.setTitle(title);
                stage.setScene(new Scene(view));
                stage.initOwner(primaryStage);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        };
    }

    private Runnable makeKMeansMenuItem(Stage primaryStage){
        return makeView(primaryStage, "/onethreeseven/clustering/view/KMeans.fxml", "KMeans Clustering");
    }

    private Runnable makeDBScanMenuItem(Stage primaryStage){
        return makeView(primaryStage, "/onethreeseven/clustering/view/DBScan.fxml", "DBScan Clustering");
    }

}