package onethreeseven.clustering.model;

import onethreeseven.geo.model.LatLonBounds;
import onethreeseven.trajsuitePlugin.model.BoundingCoordinates;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class Cluster implements BoundingCoordinates {
    public abstract List<double[]> getPoints2d();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cluster)) return false;
        Cluster otherCluster = (Cluster) o;


        //compare cluster size and ensure they are same size
        if(this.getPoints2d().size() != otherCluster.getPoints2d().size()){
            return false;
        }

        int nMatches = 0;

        for (double[] thisPt : getPoints2d()) {
            Iterator<double[]> otherPtIter = otherCluster.getPoints2d().iterator();
            boolean foundMatch = false;
            while(otherPtIter.hasNext() && !foundMatch){
                double[] otherPt = otherPtIter.next();
                if(Arrays.equals(thisPt, otherPt)){
                    foundMatch = true;
                    nMatches++;
                }
            }
            if(!foundMatch){
                return false;
            }
        }

        //found a match for every point in this cluster in the other cluster
        return nMatches == this.getPoints2d().size();
    }

    @Override
    public int hashCode() {
        return getPoints2d().hashCode();
    }

    @Override
    public Iterator<double[]> geoCoordinateIter() {
        return null;
    }

    @Override
    public LatLonBounds getLatLonBounds() {
        return null;
    }

    @Override
    public Iterator<double[]> coordinateIter() {
        return getPoints2d().iterator();
    }

    @Override
    public String toString(){
        return "Cluster (" + getPoints2d().size() + " points)";
    }

}
