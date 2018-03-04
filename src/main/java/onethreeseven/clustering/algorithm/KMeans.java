package onethreeseven.clustering.algorithm;

import onethreeseven.clustering.model.KMeansCluster;
import onethreeseven.common.util.Maths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * K-means in 2d using Lloyd's algorithm.
 * Runs until convergence.
 * Uses Bradley and Fayyad's technique for selection reasonable starting centroids.
 * @see "Refining Initial Points for K-Means Clustering"
 * @author Luke Bermingham
 * @author Nicholas Pace
 */
public class KMeans {

    ////////////////////////////
    //PUBLIC STATIC methods
    ///////////////////////////

    /**
     * Run k-means on the data.
     * @param pts The points to cluster.
     * @param k The number of clusters to produce.
     * @param initialCentroids The intial centroids to use when initialising k-means.
     * @return The k clusters.
     */
    public static KMeansCluster[] run2d(double[][] pts, int k, double[][] initialCentroids) {
        if(k > pts.length || k < 1){
            throw new IllegalArgumentException("K must be between 1 and " + pts.length);
        }

        KMeans impl = new KMeans();
        KMeansCluster[] clusters = impl.initClusters(pts, k, initialCentroids);
        impl.doKMeans(clusters, true);
        return clusters;
    }

    /**
     * Runs k-means on the data.
     * @param pts The points to cluster.
     * @param k The desired number of clusters.
     * @param j The number of starting configurations to evaluate using Bradley and Fayyad's method.
     * @return The k clusters.
     */
    public static KMeansCluster[] run2d(double[][] pts, int k, int j) {
        if(j < 1 || j > pts.length){
            throw new IllegalArgumentException("J must be between 1 and " + pts.length);
        }

        KMeans impl = new KMeans();
        double[][] initialCentroids = impl.calculateInitialCentroids(pts, k, j, j);
        return run2d(pts, k, initialCentroids);
    }

    /**
     * Runs k-means and initialises the starting centroids using Bradley and Fayyad's method.
     * The number of starting configurations is explored is 5% of the data-set or 10, whichever is larger.
     * @param pts The points we wish to cluster.
     * @param k The number of clusters we want.
     * @return K clusters.
     */
    public static KMeansCluster[] run2d(double[][] pts, int k) {
        double sampleNum = pts.length * 0.05;
        //ensure we evaluate at least 10 different starting configurations
        int j = (int) Math.max(10, sampleNum);
        return run2d(pts, k, j);
    }

    //////////////////////////////
    //Class methods
    /////////////////////////////


    protected void doKMeans(KMeansCluster[] clusters, boolean allowEmptyClusters) {
        //now move points around as long as there a better (closer) cluster to belong to
        boolean keepConverging = true;

        while(keepConverging){

            keepConverging = performIteration(clusters);

            if(keepConverging){
                for (KMeansCluster cluster : clusters) {
                    cluster.recomputeCentroid();
                }
            }

            //finished converging
            if(!keepConverging){
                //check if empty clusters are allowed or not
                if(!allowEmptyClusters){
                    //if they are not allowed we set the centroid to the furthest point from the centroid.
                    for (KMeansCluster cluster : clusters) {
                        if(cluster.getPoints2d().size() == 0){
                            keepConverging = true;
                            cluster.setCentroid(getFurthestPoint(clusters, cluster.getCentroid()));
                        }
                    }
                }
            }

        }
    }

    /**
     * Converge points to reside in the cluster with the centroid that they are closest to.
     * @param clusters The input clusters with points already assigned.
     * @return True if any points swapped clusters.
     */
    protected boolean performIteration(KMeansCluster[] clusters){
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

    /**
     * Evaluates which solution (group of clusters) is least distorted in relation to some input
     * point data-set.
     * @param clustersGroups The solutions (various clusters) we wish to evaluate the distortion of.
     * @param ptsDataset The point data-set we use to evaluate to the distortion of the solutions.
     * @return The centroids of the least distorted solution.
     */
    protected double[][] retrieveLeastDistortedCentroids(KMeansCluster[][] clustersGroups, double[][] ptsDataset) {
        double leastDistortion = Double.POSITIVE_INFINITY;
        KMeansCluster[] leastDistortedClusters = null;

        //get best solution
        for (KMeansCluster[] clusters : clustersGroups){
            double currentDistortion = calculateDistortion(clusters, ptsDataset);
            if(currentDistortion < leastDistortion){
                leastDistortion = currentDistortion;
                leastDistortedClusters = clusters;
            }
        }

        //get centroid from best solution
        if(leastDistortedClusters != null){
            double[][] centroids = new double[leastDistortedClusters.length][];
            for (int i = 0; i < leastDistortedClusters.length; i++){
                centroids[i] = leastDistortedClusters[i].getCentroid();
            }
            return centroids;
        }
        return null;
    }

    protected double calculateDistortion(KMeansCluster[] clusters, double[][] points) {
        double distortion = 0.0;

        for (KMeansCluster cluster : clusters) {
            for(double[] point : points){
                distortion += cluster.distSqToCentroid(point);
            }
        }

        return distortion;
    }

    /**
     * As an initial step, assign each point in the data-set to its closest cluster centroid.
     * @param pts The points to assign to clusters.
     * @param k The number of clusters.
     * @param initialCentroids The initial centroid of the clusters.
     * @return The initialised k-clusters, ready for recomputing centroids and converging.
     */
    protected KMeansCluster[] initClusters(double[][] pts, int k, double[][] initialCentroids){
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

    protected double[] getFurthestPoint(KMeansCluster[] clusters, double[] refPt) {
        double furthestDistSq = 0.0;
        double[] furthestPoint = { 0.0, 0.0 };

        for (KMeansCluster cluster : clusters){
            for (double[] point : cluster.getPoints2d()){
                double distSq = Maths.distSq(refPt, point);
                if(distSq > furthestDistSq){
                    furthestDistSq = distSq;
                    furthestPoint = point;
                }
            }
        }

        return furthestPoint;
    }

    /**
     * Using the technique described in Bradley and Fayyad's paper "Refining Initial Points for K-Means Clustering"
     * this method picks some "reasonable starting centroid" by essentially running k-means using random sub-samples
     * of the data.
     * @param pts The whole data-set we wish to finding starting centroids for.
     * @param k The number of clusters we have for k-means.
     * @param subsampleSize The size of the sub-samples we are taking from the data-set (called j in the paper).
     * @param nSolutions The number of solutions (different starting configurations to evaluate).
     * @return The centroids of the best (least distorted) set of clusters.
     */
    protected double[][] calculateInitialCentroids(double[][] pts, int k, int subsampleSize, int nSolutions) {
        KMeansCluster[][] candidateClusters = new KMeansCluster[nSolutions][];
        int[] randomPointIndices = findUniqueRandomInRange(pts.length, subsampleSize * nSolutions);
        int[] randomCentroidIndices = findUniqueRandomInRange(subsampleSize, k * nSolutions);
        double[][] allRandomPoints = new double[subsampleSize * nSolutions][];

        for(int i = 0; i < nSolutions; i++){
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
            //in "Refining Initial Points for K-Means Clustering" this step is called "KMeansMod"
            //because it does not allow empty clusters
            doKMeans(candidateClusters[i], false);
        }

        KMeansCluster[][] secondPassClusters = new KMeansCluster[nSolutions][];
        for(int i = 0; i < nSolutions; i++) {
            double[][] preparedCentroids = new double[k][];
            for(int j = 0; j < k; j++){
                preparedCentroids[j] = candidateClusters[i][k].getCentroid();
            }

            secondPassClusters[i] = initClusters(allRandomPoints, k, preparedCentroids);
            doKMeans(secondPassClusters[i], true);
        }

        return retrieveLeastDistortedCentroids(secondPassClusters, allRandomPoints);
    }

    protected int[] findUniqueRandomInRange(int upper, int numOutputs) {
        int[] outputs = new int[numOutputs];
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < upper; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        for (int i = 0; i < numOutputs; i++) {
            outputs[i] = list.get(i);
        }

        return outputs;
    }



}
