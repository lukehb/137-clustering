package onethreeseven.clustering.algorithm;

import onethreeseven.clustering.model.KMeansCluster;
import java.util.Iterator;
import java.util.Random;

/**
 * K-means in 2d using Lloyd's algorithm.
 * Runs until convergence.
 * @author Luke Bermingham
 */
public class KMeans {


    public KMeansCluster[] run2d(double[][] pts, int k){

        if(k > pts.length || k < 1){
            throw new IllegalArgumentException("K must be between 1 and " + pts.length);
        }

        KMeansCluster[] clusters = initClusters(pts, k);

        //now move points around as long as there a better (closer) cluster to belong to
        boolean keepConverging = true;

        while(keepConverging){

            keepConverging = false;

            for (int i = 0; i < clusters.length; i++) {
                KMeansCluster cluster = clusters[i];
                Iterator<double[]> ptIter = cluster.iterator();
                while (ptIter.hasNext()) {

                    //get current point and distance to owning cluster's centroid
                    double[] pt = ptIter.next();
                    double curDistToCentroid = cluster.distSqToCentroid(pt);
                    KMeansCluster betterCluster = null;

                    //check if other clusters closer
                    for (int j = 0; j < clusters.length; j++) {
                        KMeansCluster otherCluster = clusters[j];
                        //we don't care about comparing to the cluster we are already in
                        if(j == i){
                            continue;
                        }
                        double distToOtherClusterCentroid = otherCluster.distSqToCentroid(pt);
                        if(distToOtherClusterCentroid < curDistToCentroid){
                            betterCluster = otherCluster;
                            curDistToCentroid = distToOtherClusterCentroid;
                        }
                    }

                    if(betterCluster != null){
                        //remove point from current cluster
                        ptIter.remove();
                        //add point to better cluster
                        betterCluster.add(pt);
                        //indicate that we moved points
                        keepConverging = true;
                    }
                }
            }

            if(keepConverging){
                for (KMeansCluster cluster : clusters) {
                    cluster.recomputeCentroid();
                }
            }

        }

        return clusters;

    }

    private KMeansCluster[] initClusters(double[][] pts, int k){
        //assign each a random centroid cluster
        KMeansCluster[] clusters = new KMeansCluster[k];

        Random rand = new Random();

        for (int i = 0; i < k; i++) {
            int randPtIdx = rand.nextInt(pts.length);
            double[] randCentroid = pts[randPtIdx];
            clusters[i] = new KMeansCluster(randCentroid);
        }

        //now assign each point to the closest (euclidean squared dist) cluster
        for (double[] pt : pts) {

            double smallestDist = Double.POSITIVE_INFINITY;
            KMeansCluster closestCluster = clusters[0];

            //check each cluster to find the closest one
            for (KMeansCluster cluster : clusters) {
                double dist = cluster.distSqToCentroid(pt);
                if(dist < smallestDist){
                    smallestDist = dist;
                    closestCluster = cluster;
                }
            }

            //assign that point to that cluster
            closestCluster.add(pt);

        }

        for (KMeansCluster cluster : clusters) {
            cluster.recomputeCentroid();
        }

        return clusters;

    }

}
