package onethreeseven.clustering.command;

import onethreeseven.clustering.graphic.ClusterGraphic;
import onethreeseven.clustering.model.Cluster;
import onethreeseven.common.util.ColorUtil;
import onethreeseven.geo.projection.AbstractGeographicProjection;
import onethreeseven.geo.projection.ProjectionEquirectangular;
import onethreeseven.jclimod.CLICommand;
import onethreeseven.trajsuitePlugin.graphics.GraphicsPayload;
import onethreeseven.trajsuitePlugin.graphics.LabelPrefab;
import onethreeseven.trajsuitePlugin.model.BoundingCoordinates;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;
import onethreeseven.trajsuitePlugin.model.TransactionProcessor;
import onethreeseven.trajsuitePlugin.transaction.AddEntitiesTransaction;
import java.util.*;

/**
 * Abstract command from cluster commands like {@link KmeansCommand} and {@link DBScanCommand}.
 * @author Luke Bermingham
 */
public abstract class AbstractClusteringCommand extends CLICommand{

    protected abstract String getClusterLayerName();
    protected abstract String getClusterPrefix(Cluster cluster);
    protected abstract double[] getClusterAnnotationCartesianCoord(Cluster cluster);
    protected abstract Cluster[] doClustering();
    protected abstract boolean clusterShouldBeVisibleOnLoad(Cluster cluster);

    @Override
    protected boolean runImpl() {
        Cluster[] clusters = doClustering();
        outputClusters(clusters);
        return true;
    }

    protected <T extends Cluster> void outputClusters(T[] clusters){

        AbstractGeographicProjection projection = new ProjectionEquirectangular();
        java.awt.Color[] nColors = ColorUtil.generateNColors(clusters.length);

        AddEntitiesTransaction transaction = new AddEntitiesTransaction();
        for (int i = 0; i < clusters.length; i++) {

            Cluster cluster = clusters[i];

            GraphicsPayload payload = new ClusterGraphic();
            String clusterPrefix = getClusterPrefix(cluster);
            String id = clusterPrefix + i;

            payload.fallbackColor.setValue(nColors[i]);

            //add annotation to the cluster
            double[] annotationCoord = getClusterAnnotationCartesianCoord(cluster);
            if(annotationCoord != null){
                double[] centroidLatLon = projection.cartesianToGeographic(annotationCoord);
                LabelPrefab labelPrefab = new LabelPrefab(id, centroidLatLon);
                labelPrefab.doesScale.setValue(true);
                payload.additionalPrefabs.add(labelPrefab);
            }

            String layername = getClusterLayerName();

            boolean isVisible = clusterShouldBeVisibleOnLoad(cluster);

            transaction.add(layername, id, cluster, false, isVisible, payload);
        }

        //process the transaction
        ServiceLoader<TransactionProcessor> serviceLoader = ServiceLoader.load(TransactionProcessor.class);
        for (TransactionProcessor entityConsumer : serviceLoader) {
            entityConsumer.process(transaction);
        }
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

}
