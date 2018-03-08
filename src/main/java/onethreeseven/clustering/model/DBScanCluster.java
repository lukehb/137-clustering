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

    public List<double[]> getPoints2d() {
        return points2d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBScanCluster)) return false;
        DBScanCluster otherCluster = (DBScanCluster) o;
        return isNoise == otherCluster.isNoise && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isNoise, points2d);
    }
}
