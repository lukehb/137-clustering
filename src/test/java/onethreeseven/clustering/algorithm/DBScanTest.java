package onethreeseven.clustering.algorithm;

import onethreeseven.clustering.model.DBScanCluster;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;


public class DBScanTest {
    private static final double testEpsilon = 10.0;
    private static final int testMinPts = 4;

    private static final double[][] testData = {
            new double [] { 146.0, 41.0 },
            new double [] { 143.0, 42.0 },
            new double [] { 141.0, 43.0 },
            new double [] { 140.0, 44.0 },
            new double [] { 143.0, 45.0 },
            new double [] { 146.0, 42.0 },
            new double [] { 141.0, 50.0 },
            new double [] { 1430.0, 421.0 },
            new double [] { 200.0, 142.0 },
            new double [] { 200.0, 145.0 },
            new double [] { 220.0, 142.0 },
            new double [] { 224.0, 145.0 },
            new double [] { 215.0, 141.0 },
            new double [] { 230.0, 148.0 },
            new double [] { 221.0, 141.0 },
            new double [] { 231.0, 142.0 },
            new double [] { 231.0, 141.0 },
            new double [] { 231.0, 143.0 },
            new double [] { 231.0, 144.0 },
            new double [] { 230.0, 140.0 },
            new double [] { 2301.0, 140.0 },
            new double [] { 2130.0, 140.0 },
            new double [] { 230.0, 1140.0 },
            new double [] { 2146.0, 141.0 },
            new double [] { 2143.0, 142.0 },
            new double [] { 2141.0, 143.0 },
            new double [] { 2140.0, 144.0 },
            new double [] { 2143.0, 145.0 },
            new double [] { 2146.0, 142.0 },
            new double [] { 2141.0, 150.0 },
    };

    private static final double[][][] testResult = {
            new double [][] {
                    new double [] { 146.0, 41.0 },
                    new double [] { 143.0, 42.0 },
                    new double [] { 141.0, 43.0 },
                    new double [] { 140.0, 44.0 },
                    new double [] { 143.0, 45.0 },
                    new double [] { 146.0, 42.0 },
                    new double [] { 141.0, 50.0 }
            },
            new double [][] {
                    new double [] { 220.0, 142.0 },
                    new double [] { 224.0, 145.0 },
                    new double [] { 215.0, 141.0 },
                    new double [] { 230.0, 148.0 },
                    new double [] { 221.0, 141.0 },
                    new double [] { 231.0, 142.0 },
                    new double [] { 231.0, 141.0 },
                    new double [] { 231.0, 143.0 },
                    new double [] { 231.0, 144.0 },
                    new double [] { 230.0, 140.0 }
            },
            new double [][] {
                    new double [] { 2146.0, 141.0 },
                    new double [] { 2143.0, 142.0 },
                    new double [] { 2141.0, 143.0 },
                    new double [] { 2140.0, 144.0 },
                    new double [] { 2143.0, 145.0 },
                    new double [] { 2146.0, 142.0 },
                    new double [] { 2141.0, 150.0 }
            },
            new double [][] {
                    new double [] { 1430.0, 421.0 },
                    new double [] { 200.0, 142.0 },
                    new double [] { 200.0, 145.0 },
                    new double [] { 2301.0, 140.0 },
                    new double [] { 2130.0, 140.0 },
                    new double [] { 230.0, 1140.0 }
            }
    };

    @Test
    public void run2d() {
        DBScanCluster[] clusters = DBScan.run2d(testData, testEpsilon, testMinPts);
        compareResult(clusters, testResult);
    }

    @Test
    public void run2dDistSq() {
        DBScanCluster[] clusters = DBScan.run2d(testData, testEpsilon * testEpsilon, testMinPts, DBScan::euclidDistSq);
        compareResult(clusters, testResult);
    }

    private static void compareResult(DBScanCluster[] clusters, double[][][] testResultData){
        Assert.assertEquals(testResultData.length, clusters.length);

        for (int i = 0; i < clusters.length; i++) {
            int matches = 0;
            Collection<double[]> cluster = clusters[i].getPoints2d();

            for (int j = 0; j < testResultData[i].length; j++){
                double[] testResultClusterPoint = testResultData[i][j];
                for (double[] clusterPoint : cluster) {
                    if(clusterPoint[0] == testResultClusterPoint[0] && clusterPoint[1] == testResultClusterPoint[1]){
                        matches++;
                        break;
                    }
                }
            }

            Assert.assertEquals(testResultData[i].length, matches);
        }
    }
}