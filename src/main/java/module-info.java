import onethreeseven.clustering.command.ClusteringCommands;
import onethreeseven.clustering.view.ClusteringMenuSupplier;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;
import onethreeseven.trajsuitePlugin.model.TransactionProcessor;
module onethreeseven.clustering {
    requires jcommander;
    requires jts;
    requires onethreeseven.common;
    requires java.desktop;
    requires onethreeseven.jclimod;
    requires onethreeseven.trajsuitePlugin;
    requires onethreeseven.datastructures;

    uses EntitySupplier;

    //for add entities from clustering commands
    uses TransactionProcessor;

    exports onethreeseven.clustering.model;
    exports onethreeseven.clustering.command;
    exports onethreeseven.clustering.algorithm;
    exports onethreeseven.clustering.graphic;

    //for commands to work
    opens onethreeseven.clustering.command to jcommander, onethreeseven.jclimod;

    //for javafx to work
    exports onethreeseven.clustering to javafx.graphics;

    //for commands
    provides onethreeseven.jclimod.AbstractCommandsListing with ClusteringCommands;

    //for clustering menu
    provides onethreeseven.trajsuitePlugin.view.MenuSupplier with ClusteringMenuSupplier;

    //for resource to loadable from other jars
    opens onethreeseven.clustering.view;

    exports onethreeseven.clustering.view.controller to javafx.fxml;
    opens onethreeseven.clustering.view.controller to javafx.fxml;

}