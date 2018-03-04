import onethreeseven.clustering.command.ClusteringCommands;
import onethreeseven.trajsuitePlugin.model.EntityConsumer;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;

module onethreeseven.clustering {
    requires jcommander;
    requires jts;
    requires onethreeseven.common;
    requires onethreeseven.jclimod;
    requires onethreeseven.trajsuitePlugin;
    requires onethreeseven.datastructures;

    uses EntitySupplier;
    uses EntityConsumer;

    exports onethreeseven.clustering.model;
    exports onethreeseven.clustering.command;
    exports onethreeseven.clustering.algorithm;

    //for commands to work
    opens onethreeseven.clustering.command to jcommander, onethreeseven.jclimod;

    //for javafx to work
    exports onethreeseven.clustering to javafx.graphics;

    provides onethreeseven.jclimod.AbstractCommandsListing with ClusteringCommands;

}