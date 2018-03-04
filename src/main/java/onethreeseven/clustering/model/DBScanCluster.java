package onethreeseven.clustering.model;

import java.util.*;

public class DBScanCluster extends Cluster {
    private boolean isNoise;
    private ArrayList<double[]> points2d;

    public DBScanCluster(boolean isNoise){
        this();
        this.isNoise = isNoise;
    }

    public DBScanCluster(){
        this.points2d = new ArrayList<>();
        this.isNoise = false;
    }

    public void add(double[] pt){
        this.points2d.add(pt);
    }

    public boolean IsNoise(){
        return isNoise;
    }

    protected void setIsNoise(boolean isNoise){
        this.isNoise = isNoise;
    }

    public Collection<double[]> getPoints2d() {
        return points2d;
    }

    @Override
    public Iterator<double[]> iterator(){
        return points2d.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBScanCluster)) return false;
        DBScanCluster otherCluster = (DBScanCluster) o;

        if(isNoise != otherCluster.isNoise){
            return false;
        }

        //compare cluster size and ensure they are same size
        if(this.points2d.size() != otherCluster.points2d.size()){
            return false;
        }

        int nMatches = 0;

        for (double[] thisPt : points2d) {
            Iterator<double[]> otherPtIter = otherCluster.points2d.iterator();
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
        return nMatches == this.points2d.size();
    }

    @Override
    public int hashCode() {
        return Objects.hash(isNoise, points2d);
    }
}
