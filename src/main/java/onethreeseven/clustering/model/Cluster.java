package onethreeseven.clustering.model;

import java.util.Collection;

public abstract class Cluster implements Iterable<double[]> {
    public abstract Collection<double[]> getPoints2d();
}
