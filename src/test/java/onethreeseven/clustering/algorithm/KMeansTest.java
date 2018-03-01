package onethreeseven.clustering.algorithm;

import onethreeseven.clustering.model.KMeansCluster;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class KMeansTest {
    private static final int testK = 3;

    private static final double[][] testData = {
            new double [] { 146.0, 541.0 },
            new double [] { 143.0, 542.0 },
            new double [] { 141.0, 543.0 },
            new double [] { 140.0, 544.0 },
            new double [] { 143.0, 545.0 },
            new double [] { 146.0, 542.0 },
            new double [] { 141.0, 550.0 },
            new double [] { 215.0, 141.0 },
            new double [] { 230.0, 148.0 },
            new double [] { 221.0, 141.0 },
            new double [] { 231.0, 142.0 },
            new double [] { 231.0, 141.0 },
            new double [] { 231.0, 143.0 },
            new double [] { 231.0, 144.0 },
            new double [] { 2146.0, 141.0 },
            new double [] { 2143.0, 142.0 },
            new double [] { 2141.0, 143.0 },
            new double [] { 2140.0, 144.0 },
            new double [] { 2143.0, 145.0 },
            new double [] { 2146.0, 142.0 },
            new double [] { 2141.0, 150.0 }
    };

    private static final double[][][] testResult = {
            new double [][] {
                    new double [] { 146.0, 541.0 },
                    new double [] { 143.0, 542.0 },
                    new double [] { 141.0, 543.0 },
                    new double [] { 140.0, 544.0 },
                    new double [] { 143.0, 545.0 },
                    new double [] { 146.0, 542.0 },
                    new double [] { 141.0, 550.0 }
            },
            new double [][] {
                    new double [] { 215.0, 141.0 },
                    new double [] { 230.0, 148.0 },
                    new double [] { 221.0, 141.0 },
                    new double [] { 231.0, 142.0 },
                    new double [] { 231.0, 141.0 },
                    new double [] { 231.0, 143.0 },
                    new double [] { 231.0, 144.0 }
            },
            new double [][] {
                    new double [] { 2146.0, 141.0 },
                    new double [] { 2143.0, 142.0 },
                    new double [] { 2141.0, 143.0 },
                    new double [] { 2140.0, 144.0 },
                    new double [] { 2143.0, 145.0 },
                    new double [] { 2146.0, 142.0 },
                    new double [] { 2141.0, 150.0 }
            }
    };

    private static final double[][] testCentroids = {
            new double [] { 141.0, 543.0 },
            new double [] { 231.0, 142.0 },
            new double [] { 2140.0, 144.0 }
    };

    @Test
    public void run2d() {
        KMeansCluster[] clusters = KMeans.run2d(testData, testK, testCentroids);
        compareResult(clusters, testResult);
    }

    private static void compareResult(KMeansCluster[] clusters, double[][][] testResultData){
        Assert.assertEquals(testResultData.length, clusters.length);

        for (int i = 0; i < clusters.length; i++) {
            int matches = 0;
            int resultLength = 0;
            Collection<double[]> cluster = clusters[i].getPoints2d();

            for (int k = 0; k < testResultData.length; k++) {
                matches = 0;
                resultLength = testResultData[k].length;
                for (int j = 0; j < resultLength; j++) {
                    double[] testResultClusterPoint = testResultData[k][j];
                    for (double[] clusterPoint : cluster) {
                        if (clusterPoint[0] == testResultClusterPoint[0] && clusterPoint[1] == testResultClusterPoint[1]) {
                            matches++;
                            break;
                        }
                    }
                }

                if(matches == resultLength){
                    break;
                }
            }

            Assert.assertEquals(resultLength, matches);
        }
    }
}