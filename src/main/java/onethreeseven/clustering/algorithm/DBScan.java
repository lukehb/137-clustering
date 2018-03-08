package onethreeseven.clustering.algorithm;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.kdtree.KdNode;
import com.vividsolutions.jts.index.kdtree.KdTree;
import onethreeseven.clustering.model.DBScanCluster;
import onethreeseven.common.util.Maths;

import java.util.*;
import java.util.function.Consumer;

/**
 * Classic DBSCAN for 2d points. Using Kd-tree as a spatial index.
 * @author Nicholas Pace
 * @author Luke Bermingham
 */
public class DBScan {
    private static final byte UNLABELED = (byte) 0;
    private static final byte NOISE = (byte) 1;
    private static final byte CLUSTER = (byte) 2;

    private final double epsilon;
    private final int minPts;
    private final KdTree ptsDatabase;
    private final byte[] labels;
    private final double[][] pts;
    private final Consumer<Double> progressListener;

    //////////////////////
    //static methods
    /////////////////////


    /**
     * Find density-based clusters using DBSCAN.
     * @param points2d The 2d points to cluster
     * @param epsilon How close points have to be to each other to be considered clusters.
     * @param minPts The number of points a point must have surrounding it to grow a cluster.
     * @return The clusters found.
     */
    public static Collection<DBScanCluster> run2d(double[][] points2d, double epsilon, int minPts) {
        DBScan impl = new DBScan(points2d, epsilon, minPts, null);
        return impl.run();
    }

    /**
     * Find density-based clusters using DBSCAN.
     * @param points2d The 2d points to cluster
     * @param epsilon How close points have to be to each other to be considered clusters.
     * @param minPts The number of points a point must have surrounding it to grow a cluster.
     * @param progressListener Progress listener for the algorithm, reports between 0 and 1, where 1 is finished processing.
     * @return The clusters found.
     */
    public static Collection<DBScanCluster> run2d(double[][] points2d, double epsilon, int minPts, Consumer<Double> progressListener) {
        DBScan impl = new DBScan(points2d, epsilon, minPts, progressListener);
        return impl.run();
    }

    //////////////////////////////////
    //Actual DBSCAN implementation
    /////////////////////////////////


    protected DBScan(double[][] pts, double epsilon, int minPts, Consumer<Double> progressListener){

        this.progressListener = progressListener;

        if(epsilon < 0.0) {
            throw new IllegalArgumentException("Epsilon must not be less than 0");
        }

        if(minPts < 0){
            throw new IllegalArgumentException("MinPts must not be less than 0");
        }

        if(pts == null || pts.length < 1){
            throw new IllegalArgumentException("Points must be non-empty.");
        }

        this.pts = pts;
        this.minPts = minPts;
        this.epsilon = epsilon;

        // Keep track of the state of each point, that is, is the points labelled as: CLUSTER|UNLABELED|NOISE
        this.labels = new byte[pts.length];

        // Use kd-tree as out spatial index for doing the neighbour queries
        this.ptsDatabase = new KdTree();
        for (int i = 0; i < pts.length; i++) {
            double[] pt = pts[i];
            this.ptsDatabase.insert(new Coordinate(pt[0], pt[1]), i);
        }
    }

    protected Collection<DBScanCluster> run(){

        LinkedList<DBScanCluster> clusters = new LinkedList<>();

        for (int i = 0; i < pts.length; i++) {
            if(labels[i] != UNLABELED){
                continue;
            }

            double[] pt = pts[i];
            traversePoint(pt, i, clusters);

            if(progressListener != null){
                double progress = (double)i / pts.length;
                progressListener.accept(progress);
            }

        }

        // Create cluster containing all the left over noise points
        DBScanCluster noise = new DBScanCluster(true);
        for (int i = 0; i < pts.length; i++) {
            if(labels[i] == NOISE){
                noise.add(pts[i]);
            }
        }

        //done clustering now
        //exclude any clusters with one points only and them to the noise cluster
        Iterator<DBScanCluster> iter = clusters.iterator();
        while(iter.hasNext()){
            DBScanCluster cluster = iter.next();
            if(cluster.getPoints2d().size() <= 1){
                iter.remove();
                noise.getPoints2d().addAll(cluster.getPoints2d());
            }
        }

        //add the noise cluster after removing the single point clusters
        clusters.add(noise);

        return clusters;

    }

    // Start of traversal, mark as noise if below number of minimum neighbours threshold, traverse neighbours otherwise
    protected void traversePoint(double[] pt, int index, List<DBScanCluster> clusters) {
        Queue<KdNode> neighbours = getNeighbours(pt);
        if(neighbours.size() < minPts){
            labels[index] = NOISE;
            return;
        }

        labels[index] = CLUSTER;

        DBScanCluster cluster = new DBScanCluster();
        clusters.add(cluster);
        cluster.add(pt);
        growCluster(cluster, neighbours);
    }

    protected Queue<KdNode> getNeighbours(double[] pt){
        double epsilonSq = epsilon * epsilon;

        double x1 = pt[0] - epsilon;
        double x2 = pt[0] + epsilon;
        double y1 = pt[1] - epsilon;
        double y2 = pt[1] + epsilon;

        List nodesInBounds = ptsDatabase.query(new Envelope(x1, x2, y1, y2));
        Queue<KdNode> neighbours = new ArrayDeque<>();

        for (Object nodeObj : nodesInBounds){
            if(nodeObj instanceof KdNode){
                KdNode node = (KdNode) nodeObj;
                double[] nodePt = {node.getX(), node.getY()};
                double distToNode = Maths.distSq(nodePt, pt);
                if(distToNode <= epsilonSq) {
                    neighbours.add(node);
                }
            }
        }

        return neighbours;
    }

    protected void addDensityConnectedPts(double[] neighbourPt, Queue<KdNode> neighbours){
        Queue<KdNode> moreNeighbours = getNeighbours(neighbourPt);
        //the density connected condition
        if(moreNeighbours.size() < minPts){
            return;
        }

        //go through neighbours and make sure they aren't already assigned to a cluster
        Iterator<KdNode> iter = moreNeighbours.iterator();
        while(iter.hasNext()){
            KdNode node = iter.next();
            Object data = node.getData();
            if(data instanceof Integer){
                Integer idx = (Integer) data;
                if(labels[idx] == CLUSTER){
                    iter.remove();
                }
            }
        }

        //add more neighbours to current
        neighbours.addAll(moreNeighbours);
    }

    protected void growCluster(DBScanCluster cluster, Queue<KdNode> neighbours){

        while(!neighbours.isEmpty()){
            //remove the current entry
            KdNode neighbour = neighbours.poll();
            int index = (int) neighbour.getData();

            //some other cluster has claimed this one
            if(labels[index] == CLUSTER){
                continue;
            }

            //but if label is NOISE or UNLABELLED, this cluster will take it
            labels[index] = CLUSTER;
            double[] pt = {neighbour.getX(), neighbour.getY()};
            cluster.add(pt);

            //have a look at neighbours of the current point
            addDensityConnectedPts(pt, neighbours);

        }

    }

}
