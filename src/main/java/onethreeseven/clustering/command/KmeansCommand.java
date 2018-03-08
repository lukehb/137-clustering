package onethreeseven.clustering.command;

import com.beust.jcommander.Parameter;
import onethreeseven.clustering.algorithm.KMeans;
import onethreeseven.clustering.model.Cluster;
import onethreeseven.clustering.model.KMeansCluster;

/**
 * The CLI command to run K-means from {@link KMeans}.
 * @author Luke Bermingham
 */
public class KmeansCommand extends AbstractClusteringCommand {

    @Parameter(names = {"-k", "--kClusters"}, description = "The desired number of clusters to find.")
    private int k;

    private double[][] points2d;

    @Override
    protected String getUsage() {
        return "kmeans -k 3";
    }

    @Override
    protected double[] getClusterAnnotationCartesianCoord(Cluster cluster) {
        if(cluster instanceof KMeansCluster){
            return ((KMeansCluster) cluster).getCentroid();
        }
        return null;
    }

    @Override
    protected Cluster[] doClustering() {
        return KMeans.run2d(points2d, k);
    }

    @Override
    protected boolean clusterShouldBeVisibleOnLoad(Cluster cluster) {
        return true;
    }

    @Override
    protected boolean parametersValid() {

        this.points2d = getPointsToCluster();
        if(points2d.length < 2){
            System.err.println("There must be more than two points to perform clustering");
            return false;
        }

        if(k < 1 || k > points2d.length){
            System.err.println("In k-means k must be greater than 1 and less than then number of points, " +
                    "was passed: " + k);
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldStoreRerunAlias() {
        return false;
    }

    @Override
    public String generateRerunAliasBasedOnParams() {
        return null;
    }

    @Override
    public String getCategory() {
        return "Mining";
    }

    @Override
    public String getCommandName() {
        return "kmeans";
    }

    @Override
    public String[] getOtherCommandNames() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Runs k-means clustering on all selected entities with coordinates.";
    }

    @Override
    protected String getClusterLayerName() {
        return "K-Means Clusters";
    }

    @Override
    protected String getClusterPrefix(Cluster cluster) {
        return "Cluster k-";
    }
}
