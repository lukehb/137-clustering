package onethreeseven.clustering.command;

import com.beust.jcommander.JCommander;
import onethreeseven.jclimod.AbstractCommandsListing;
import onethreeseven.jclimod.CLICommand;

/**
 * Todo: write documentation
 *
 * @author Luke Bermingham
 */
public class ClusteringCommands extends AbstractCommandsListing {
    @Override
    protected CLICommand[] createCommands(JCommander jc, Object... args) {
        return new CLICommand[]{
                new KmeansCommand()
        };
    }
}
