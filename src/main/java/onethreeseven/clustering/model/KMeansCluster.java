package onethreeseven.clustering.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A K-means clusters. A cluster of 2d points with a centroid.
 * @author Luke Bermingham
 */
public class KMeansCluster implements Iterable<double[]> {

    private final LinkedList<double[]> points2d;

    private double[] centroid;

    public KMeansCluster(double[] centroid) {
        this.points2d = new LinkedList<>();
        this.centroid = centroid;
    }

    public void recomputeCentroid(){
        //todo: compute the centroid of this cluster based on the points
        throw new UnsupportedOperationException("To do");
    }

    public double distSqToCentroid(double[] pt){
        double a = centroid[0] - pt[0];
        double b = centroid[1] - pt[1];
        return a*a + b*b;
    }

    public void add(double[] pt){
        this.points2d.add(pt);
    }

    public Collection<double[]> getPoints2d() {
        return points2d;
    }

    @Override
    public Iterator<double[]> iterator(){
        return points2d.iterator();
    }

}
