package onethreeseven.clustering.command;

import com.beust.jcommander.Parameter;
import onethreeseven.clustering.algorithm.KMeans;
import onethreeseven.clustering.model.KMeansCluster;
import onethreeseven.jclimod.CLICommand;
import onethreeseven.trajsuitePlugin.model.BoundingCoordinates;
import onethreeseven.trajsuitePlugin.model.EntityConsumer;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;

import java.util.*;

/**
 * The CLI command to run K-means from {@link KMeans}.
 * @author Luke Bermingham
 */
public class KmeansCommand extends CLICommand {

    @Parameter(names = {"-k", "--kClusters"}, description = "The desired number of clusters to find.")
    private int k;

    private double[][] points2d;

    @Override
    protected String getUsage() {
        return "kmeans -k 3";
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

    protected double[][] getPointsToCluster(){
        //initialise points 2d
        int nPts = 0;
        ArrayList<double[]> pileOPoints = new ArrayList<>();

        boolean foundEntitySupplier = false;

        //get some points to cluster
        ServiceLoader<EntitySupplier> serviceLoader = ServiceLoader.load(EntitySupplier.class);
        for (EntitySupplier entitySupplier : serviceLoader) {
            foundEntitySupplier = true;

            Map<Class, Collection<Object>> selected = entitySupplier.supplyAllSelected();
            for (Collection<Object> selectedObjs : selected.values()) {
                for (Object selectedObj : selectedObjs) {
                    if(selectedObj instanceof BoundingCoordinates){
                        BoundingCoordinates coords = (BoundingCoordinates) selectedObj;
                        Iterator<double[]> coordIter = coords.coordinateIter();
                        while(coordIter.hasNext()){
                            pileOPoints.add(coordIter.next());
                            nPts++;
                        }
                    }
                }
            }
        }

        //if could not find entity supplier
        if(!foundEntitySupplier){
            System.err.println("No entity supplier found to supply selected entity for k-means clustering.");
            return new double[][]{};
        }

        double[][] points2d = new double[nPts][2];

        for (int i = 0; i < pileOPoints.size(); i++) {
            double[] point2d = pileOPoints.get(i);
            points2d[i] = point2d;
        }
        return points2d;
    }

    @Override
    protected boolean runImpl() {
        KMeansCluster[] clusters = KMeans.run2d(points2d, k);
        outputKClusters(clusters);
        return true;
    }

    protected void outputKClusters(KMeansCluster[] clusters){
        ServiceLoader<EntityConsumer> serviceLoader = ServiceLoader.load(EntityConsumer.class);
        for (EntityConsumer entityConsumer : serviceLoader) {
            for (int i = 0; i < clusters.length; i++) {
                KMeansCluster cluster = clusters[i];
                entityConsumer.consume("K-Means Clusters", "Cluster k-" + i, cluster);
            }
        }
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
}
