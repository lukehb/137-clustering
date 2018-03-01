package onethreeseven.clustering.algorithm;

import onethreeseven.clustering.ClusterUtil;
import onethreeseven.clustering.model.KMeansCluster;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * K-means in 2d using Lloyd's algorithm.
 * Runs until convergence.
 * @author Luke Bermingham
 */
public class KMeans {

    public static KMeansCluster[] run2d(double[][] pts, int k, double[][] initialCentroids) {
        if(k > pts.length || k < 1){
            throw new IllegalArgumentException("K must be between 1 and " + pts.length);
        }

        KMeansCluster[] clusters = initClusters(pts, k, initialCentroids);
        doKMeans(clusters);
        return clusters;
    }

    public static KMeansCluster[] run2d(double[][] pts, int k, int iterations) {
        if(k > pts.length || k < 1){
            throw new IllegalArgumentException("K must be between 1 and " + pts.length);
        }

        return run2d(pts, k, calculateInitialCentroids(pts, k, iterations));
    }

    public static KMeansCluster[] run2d(double[][] pts, int k) {
        double sampleNum = pts.length * 0.1;
        int iterations = (int)Math.sqrt(sampleNum);
        return run2d(pts, k, iterations);
    }

    private static void doKMeans(KMeansCluster[] clusters) {
        //now move points around as long as there a better (closer) cluster to belong to
        boolean keepConverging = true;

        while(keepConverging){

            keepConverging = performIteration(clusters);

            if(keepConverging){
                for (KMeansCluster cluster : clusters) {
                    cluster.recomputeCentroid();
                }
            }
        }
    }

    private static void doKMeansMod(KMeansCluster[] clusters) {
        //now move points around as long as there a better (closer) cluster to belong to
        boolean keepConverging = true;

        while(keepConverging){
            keepConverging = performIteration(clusters);
            if(keepConverging){
                for (KMeansCluster cluster : clusters) {
                    cluster.recomputeCentroid();
                }
            } else {
                for (KMeansCluster cluster : clusters) {
                    if(cluster.getPoints2d().size() == 0){
                        keepConverging = true;
                        cluster.setCentroid(getFurthestPoint(clusters, cluster.getCentroid()));
                    }
                }
            }
        }
    }

    private static boolean performIteration(KMeansCluster[] clusters){
        boolean keepConverging = false;

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

        return keepConverging;
    }

    private static double[][] calculateInitialCentroids(double[][] pts, int k, int iterations) {
        int subsampleSize = iterations;
        KMeansCluster[][] candidateClusters = new KMeansCluster[iterations][];
        int[] randomPointIndices = findUniqueRandomInRange(pts.length, subsampleSize * iterations);
        int[] randomCentroidIndices = findUniqueRandomInRange(subsampleSize, k * iterations);
        double[][] allRandomPoints = new double[subsampleSize * iterations][];

        for(int i = 0; i < iterations; i++){
            double[][] randomPoints = new double[subsampleSize][];
            double[][] randomCentroids = new double[k][];

            for(int j = 0; j < subsampleSize; j++) {
                int pointIndex = j + i * subsampleSize;
                randomPoints[j] = pts[randomPointIndices[pointIndex]];
                allRandomPoints[pointIndex] = pts[randomPointIndices[pointIndex]];
            }

            for(int j = 0; j < k; j++) {
                int pointIndex = j + i * k;
                randomCentroids[j] = randomPoints[randomCentroidIndices[pointIndex]];
            }

            candidateClusters[i] = initClusters(randomPoints, k, randomCentroids);
            doKMeansMod(candidateClusters[i]);
        }

        KMeansCluster[][] secondPassClusters = new KMeansCluster[iterations][];
        for(int i = 0; i < iterations; i++) {
            double[][] preparedCentroids = new double[k][];
            for(int j = 0; j < k; j++){
                preparedCentroids[j] = candidateClusters[i][k].getCentroid();
            }

            secondPassClusters[i] = initClusters(allRandomPoints, k, preparedCentroids);
            doKMeans(secondPassClusters[i]);
        }

        return retrieveLeastDistortedCentroids(secondPassClusters, allRandomPoints);
    }

    private static double[][] retrieveLeastDistortedCentroids(KMeansCluster[][] clustersGroups, double[][] points) {
        double leastDistortion = Double.POSITIVE_INFINITY;
        KMeansCluster[] leastDistortedClusters = null;

        for (KMeansCluster[] clusters : clustersGroups){
            double currentDistortion = calculateDistortion(clusters, points);
            if(currentDistortion < leastDistortion){
                leastDistortion = currentDistortion;
                leastDistortedClusters = clusters;
            }
        }

        double[][] centroids = new double[leastDistortedClusters.length][];
        for (int i = 0; i < leastDistortedClusters.length; i++){
            centroids[i] = leastDistortedClusters[i].getCentroid();
        }

        return centroids;
    }

    private static double calculateDistortion(KMeansCluster[] clusters, double[][] points) {
        double distortion = 0.0;

        for (KMeansCluster cluster : clusters) {
            for(double[] point : points){
                distortion += cluster.distSqToCentroid(point);
            }
        }

        return distortion;
    }

    private static KMeansCluster[] initClusters(double[][] pts, int k, double[][] initialCentroids){
        //assign each a random centroid cluster
        KMeansCluster[] clusters = new KMeansCluster[k];

        for (int i = 0; i < k; i++) {
            clusters[i] = new KMeansCluster(initialCentroids[i]);
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

    private static int[] findUniqueRandomInRange(int upper, int numOutputs) {
        int[] outputs = new int[numOutputs];
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i< upper; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        for (int i = 0; i < numOutputs; i++) {
            outputs[i] = list.get(i);
        }

        return outputs;
    }

    private static double[] getFurthestPoint(KMeansCluster[] clusters, double[] refPt) {
        double furthestDistSq = 0.0;
        double[] furthestPoint = { 0.0, 0.0 };

        for (KMeansCluster cluster : clusters){
            for (double[] point : cluster.getPoints2d()){
                double distSq = ClusterUtil.euclidDistSq(refPt, point);
                if(distSq > furthestDistSq){
                    furthestDistSq = distSq;
                    furthestPoint = point;
                }
            }
        }

        return furthestPoint;
    }
}
