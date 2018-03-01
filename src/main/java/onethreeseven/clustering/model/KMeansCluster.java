package onethreeseven.clustering.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A K-means clusters. A cluster of 2d points with a centroid.
 * @author Luke Bermingham
 */
public class KMeansCluster extends Cluster {

    private final LinkedList<double[]> points2d;

    private double[] centroid;

    public KMeansCluster(double[] centroid) {
        this.points2d = new LinkedList<>();
        this.centroid = centroid.clone();
    }

    public double[] getCentroid(){
        return centroid;
    }

    public void setCentroid(double[] centroid){
        this.centroid = centroid.clone();
    }

    public void recomputeCentroid(){
        double[] sumPoints = { 0.0f, 0.0f };

        for (double[] point : this.points2d) {
            sumPoints[0] += point[0];
            sumPoints[1] += point[1];
        }

        this.centroid[0] = sumPoints[0] / this.points2d.size();
        this.centroid[1] = sumPoints[1] / this.points2d.size();
    }

    public double distSqToCentroid(double[] pt){
        double a = centroid[0] - pt[0];
        double b = centroid[1] - pt[1];
        return a*a + b*b;
    }

    public void add(double[] pt){ this.points2d.add(pt); }

    public Collection<double[]> getPoints2d() {
        return points2d;
    }

    @Override
    public Iterator<double[]> iterator(){
        return points2d.iterator();
    }

}
