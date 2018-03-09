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
import onethreeseven.trajsuitePlugin.view.*;

import javax.swing.plaf.ViewportUI;
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
        return () -> ViewUtil.loadUtilityView(ClusteringMenuSupplier.class, primaryStage, title, resource);
    }

    private Runnable makeKMeansMenuItem(Stage primaryStage){
        return makeView(primaryStage, "/onethreeseven/clustering/view/KMeans.fxml", "KMeans Clustering");
    }

    private Runnable makeDBScanMenuItem(Stage primaryStage){
        return makeView(primaryStage, "/onethreeseven/clustering/view/DBScan.fxml", "DBScan Clustering");
    }

}