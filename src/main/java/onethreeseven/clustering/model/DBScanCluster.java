package onethreeseven.clustering.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class DBScanCluster extends Cluster {
    private boolean isNoise;
    private ArrayList<double[]> points2d;

    public DBScanCluster(){
        this.points2d = new ArrayList<>();
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
}
