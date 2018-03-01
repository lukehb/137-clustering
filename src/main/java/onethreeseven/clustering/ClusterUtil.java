package onethreeseven.clustering;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ClusterUtil {
    public static Double euclidDistSq(double[] pt1, double[] pt2){
        return pow(pt2[0] - pt1[0], 2) + pow(pt2[1] - pt1[1], 2);
    }

    public static Double euclidDist(double[] pt1, double[] pt2){
        return sqrt(euclidDistSq(pt1, pt2));
    }
}
