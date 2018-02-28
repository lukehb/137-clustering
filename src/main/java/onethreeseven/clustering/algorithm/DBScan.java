package onethreeseven.clustering.algorithm;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.kdtree.KdNode;
import com.vividsolutions.jts.index.kdtree.KdTree;
import onethreeseven.clustering.model.DBScanCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Todo: write documentation
 *
 * @author Nicholas Pace
 */
public class DBScan {
    private static final byte UNLABELED = (byte) 0;
    private static final byte NOISE = (byte) 1;
    private static final byte CLUSTER = (byte) 2;

    // Pass epsilon squared into run2d when using this distance function
    public static Double euclidDistSq(double[] pt1, double[] pt2){
        return pow(pt2[0] - pt1[0], 2) + pow(pt2[1] - pt1[1], 2);
    }

    public static Double euclidDist(double[] pt1, double[] pt2){
        return sqrt(pow(pt2[0] - pt1[0], 2) + pow(pt2[1] - pt1[1], 2));
    }

    public static DBScanCluster[] run2d(double[][] pts, double epsilon, int minPts){
        return run2d(pts, epsilon, minPts, DBScan::euclidDist);
    }

    public static DBScanCluster[] run2d(double[][] pts, double epsilon, int minPts, BiFunction<double[], double[], Double> distFunc) {
        if(epsilon < 0.0) {
            throw new IllegalArgumentException("Epsilon must not be less than 0");
        }

        if(minPts < 0){
            throw new IllegalArgumentException("MinPts must not be less than 0");
        }

        // Use kd-tree for efficient range queries
        KdTree ptsDatabase = new KdTree();
        for (int i = 0; i < pts.length; i++) {
            double[] pt = pts[i];
            ptsDatabase.insert(new Coordinate(pt[0], pt[1]), i);
        }

        // Unknown number of clusters, use ArrayList for convenience
        ArrayList<DBScanCluster> clusters = new ArrayList<>();

        // Keep track of processing state of each point
        byte[] labels = new byte[pts.length];

        for (int i = 0; i < pts.length; i++) {
            if(labels[i] != UNLABELED){
                continue;
            }

            double[] pt = pts[i];
            traversePoint(pt, i, clusters, ptsDatabase, labels, epsilon, minPts, distFunc);
        }

        // Create cluster containing all the left over noise points
        DBScanCluster noise = new DBScanCluster();
        clusters.add(noise);
        for (int i = 0; i < pts.length; i++) {
            if(labels[i] == NOISE){
                noise.add(pts[i]);
            }
        }

        return clusters.toArray( new DBScanCluster[clusters.size()] );
    }

    // Start of traversal, mark as noise if below number of minimum neighbours threshold, traverse neighbours otherwise
    private static void traversePoint(double[] pt, int index, ArrayList<DBScanCluster> clusters, KdTree ptsDatabase, byte[] labels, double epsilon, int minPts, BiFunction<double[], double[], Double> distFunc) {
        List<KdNode> neighbours = getNeighbours(pt, ptsDatabase, epsilon, distFunc);
        if(neighbours.size() < minPts){
            labels[index] = NOISE;
            return;
        }

        labels[index] = CLUSTER;

        DBScanCluster cluster = new DBScanCluster();
        clusters.add(cluster);
        cluster.add(pt);
        for (KdNode neighbour : neighbours) {
            int neighbourIndex = (int)neighbour.getData();

            if(labels[neighbourIndex] != UNLABELED){
                continue;
            }

            labels[neighbourIndex] = CLUSTER;

            double[] neighbourPt = {neighbour.getX(), neighbour.getY()};
            cluster.add(neighbourPt);
            traverseNeighbour(neighbourPt, cluster, ptsDatabase, labels, epsilon, minPts, distFunc);
        }
    }

    private static void traverseNeighbour(double[] neighbourPt, DBScanCluster cluster, KdTree ptsDatabase, byte[] labels, double epsilon, int minPts, BiFunction<double[], double[], Double> distFunc){
        List<KdNode> neighbours = getNeighbours(neighbourPt, ptsDatabase, epsilon, distFunc);
        if(neighbours.size() < minPts){
            return;
        }

        for (KdNode neighbour : neighbours) {
            int index = (int)neighbour.getData();

            if(labels[index] != UNLABELED){
                continue;
            }

            labels[index] = CLUSTER;
            double[] pt = {neighbour.getX(), neighbour.getY()};
            cluster.add(pt);
            traverseNeighbour(pt, cluster, ptsDatabase, labels, epsilon, minPts, distFunc);
        }
    }

    private static List<KdNode> getNeighbours(double[] pt, KdTree ptsDatabase, double epsilon, BiFunction<double[], double[], Double> distFunc){
        double x1 = pt[0] - epsilon;
        double x2 = pt[0] + epsilon;
        double y1 = pt[1] - epsilon;
        double y2 = pt[1] + epsilon;

        List<KdNode> nodesInBounds = ptsDatabase.query(new Envelope(x1, x2, y1, y2));
        List<KdNode> neighbours = new ArrayList<>();

        for (KdNode node : nodesInBounds){
            double[] nodePt = {node.getX(), node.getY()};
            if(distFunc.apply(nodePt, pt) <= epsilon) {
                neighbours.add(node);
            }
        }

        return neighbours;
    }
}
