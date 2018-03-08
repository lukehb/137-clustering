package onethreeseven.clustering.command;

import com.beust.jcommander.Parameter;
import onethreeseven.clustering.algorithm.DBScan;
import onethreeseven.clustering.model.Cluster;
import onethreeseven.clustering.model.DBScanCluster;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Commands for {@link DBScan}
 * @author Luke Bermingham
 */
public class DBScanCommand extends AbstractClusteringCommand {

    @Parameter(names = {"-m", "--minPts"}, description = "The minimum number of points to expand a cluster.")
    private int minPts;

    @Parameter(names = {"-e", "--eps"}, description = "The proximity of points in space to be considered part of the same cluster.")
    private double epsMetres;

    private double[][] points2d;

    private Consumer<Double> progressListener;

    public void setProgressListener(Consumer<Double> progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    protected Cluster[] doClustering() {
        Collection<DBScanCluster> col = DBScan.run2d(points2d, epsMetres, minPts, progressListener);
        DBScanCluster[] arr = new DBScanCluster[col.size()];
        Iterator<DBScanCluster> iter = col.iterator();
        int i = 0;
        while(iter.hasNext()){
            arr[i] = iter.next();
            i++;
        }
        return arr;
    }

    @Override
    protected boolean clusterShouldBeVisibleOnLoad(Cluster cluster) {
        return !(cluster instanceof DBScanCluster) || !((DBScanCluster) cluster).IsNoise();
    }

    @Override
    protected String getUsage() {
        return "dbscan -e 3.14 -m 2";
    }

    @Override
    protected boolean parametersValid() {
        this.points2d = getPointsToCluster();
        if(points2d.length < 2){
            System.err.println("There must be more than two points to perform clustering");
            return false;
        }

        if(minPts < 1 || minPts > points2d.length){
            System.err.println("In dbscan minPts must be greater than 1 and less than then number of points, " +
                    "was passed: " + minPts);
            return false;
        }

        if(epsMetres < 0){
            System.err.println("In dbscan epsilon must be greater than zero, " +
                    "was passed: " + epsMetres);
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
        return "dbscan";
    }

    @Override
    public String[] getOtherCommandNames() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Runs DB-Scan clustering on all selected entities with coordinates.";
    }

    @Override
    protected String getClusterLayerName() {
        return "DB-Scan Clusters";
    }

    @Override
    protected String getClusterPrefix(Cluster cluster) {
        if(cluster instanceof DBScanCluster){
            if(((DBScanCluster) cluster).IsNoise()){
                return "NOISE-";
            }
        }
        return "Cluster-";
    }

    @Override
    protected double[] getClusterAnnotationCartesianCoord(Cluster cluster) {
        if(cluster instanceof DBScanCluster){
            if(!cluster.getPoints2d().isEmpty()){
                int midIdx = (int) (cluster.getPoints2d().size()-1 * 0.5);
                return cluster.getPoints2d().get(midIdx);
            }
        }
        return null;
    }
}
