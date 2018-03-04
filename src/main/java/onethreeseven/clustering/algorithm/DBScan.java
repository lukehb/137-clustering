package onethreeseven.clustering.algorithm;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.kdtree.KdNode;
import com.vividsolutions.jts.index.kdtree.KdTree;
import onethreeseven.clustering.model.DBScanCluster;
import onethreeseven.common.util.Maths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        DBScan impl = new DBScan(points2d, epsilon, minPts);
        return impl.run();
    }

    //////////////////////////////////
    //Actual DBSCAN implementation
    /////////////////////////////////


    protected DBScan(double[][] pts, double epsilon, int minPts){

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

        ArrayList<DBScanCluster> clusters = new ArrayList<>();

        for (int i = 0; i < pts.length; i++) {
            if(labels[i] != UNLABELED){
                continue;
            }

            double[] pt = pts[i];
            traversePoint(pt, i, clusters);
        }

        // Create cluster containing all the left over noise points
        DBScanCluster noise = new DBScanCluster(true);
        clusters.add(noise);
        for (int i = 0; i < pts.length; i++) {
            if(labels[i] == NOISE){
                noise.add(pts[i]);
            }
        }

        return clusters;

    }

    // Start of traversal, mark as noise if below number of minimum neighbours threshold, traverse neighbours otherwise
    protected void traversePoint(double[] pt, int index, ArrayList<DBScanCluster> clusters) {
        List<KdNode> neighbours = getNeighbours(pt);
        if(neighbours.size() < minPts){
            labels[index] = NOISE;
            return;
        }

        labels[index] = CLUSTER;

        DBScanCluster cluster = new DBScanCluster();
        clusters.add(cluster);
        cluster.add(pt);
        addToCluster(cluster, neighbours);
    }

    protected List<KdNode> getNeighbours(double[] pt){
        double epsilonSq = epsilon * epsilon;

        double x1 = pt[0] - epsilon;
        double x2 = pt[0] + epsilon;
        double y1 = pt[1] - epsilon;
        double y2 = pt[1] + epsilon;

        List nodesInBounds = ptsDatabase.query(new Envelope(x1, x2, y1, y2));
        List<KdNode> neighbours = new ArrayList<>();

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

    protected void traverseNeighbour(DBScanCluster cluster, double[] neighbourPt){
        List<KdNode> neighbours = getNeighbours(neighbourPt);
        if(neighbours.size() < minPts){
            return;
        }
        addToCluster(cluster, neighbours);
    }

    protected void addToCluster(DBScanCluster cluster, List<KdNode> neighbours){
        for (KdNode neighbour : neighbours) {
            int index = (int)neighbour.getData();

            if(labels[index] != UNLABELED){
                continue;
            }

            labels[index] = CLUSTER;
            double[] pt = {neighbour.getX(), neighbour.getY()};
            cluster.add(pt);
            traverseNeighbour(cluster, pt);
        }
    }

}
