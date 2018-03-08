package onethreeseven.clustering.algorithm;

import onethreeseven.clustering.model.KMeansCluster;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;

public class KMeansTest {

    private static final double[][] testCentroids = new double[][]{
            new double [] { 141.0, 543.0 },
            new double [] { 231.0, 142.0 },
            new double [] { 2140.0, 144.0 }
    };

    private static final int testK;
    private static final double[][] testData;
    private static final KMeansCluster[] testResult;

    static {
        testK = testCentroids.length;
        testResult = new KMeansCluster[testK];
        //cluster 1
        KMeansCluster cluster1 = new KMeansCluster(testCentroids[0]);
        cluster1.add(new double [] { 146.0, 541.0 });
        cluster1.add(new double [] { 143.0, 542.0 });
        cluster1.add(new double [] { 141.0, 543.0 });
        cluster1.add(new double [] { 140.0, 544.0 });
        cluster1.add(new double [] { 143.0, 545.0 });
        cluster1.add(new double [] { 146.0, 542.0 });
        cluster1.add(new double [] { 141.0, 550.0 });
        cluster1.recomputeCentroid();
        testResult[0] = cluster1;
        //cluster 2
        KMeansCluster cluster2 = new KMeansCluster(testCentroids[1]);
        cluster2.add(new double [] { 215.0, 141.0 });
        cluster2.add(new double [] { 230.0, 148.0 });
        cluster2.add(new double [] { 221.0, 141.0 });
        cluster2.add(new double [] { 231.0, 142.0 });
        cluster2.add(new double [] { 231.0, 141.0 });
        cluster2.add(new double [] { 231.0, 143.0 });
        cluster2.add(new double [] { 231.0, 144.0 });
        cluster2.recomputeCentroid();
        testResult[1] = cluster2;
        //cluster 3
        KMeansCluster cluster3 = new KMeansCluster(testCentroids[2]);
        cluster3.add(new double [] { 2146.0, 141.0 });
        cluster3.add(new double [] { 2143.0, 142.0 });
        cluster3.add(new double [] { 2141.0, 143.0 });
        cluster3.add(new double [] { 2140.0, 144.0 });
        cluster3.add(new double [] { 2143.0, 145.0 });
        cluster3.add(new double [] { 2146.0, 142.0 });
        cluster3.add(new double [] { 2141.0, 150.0 });
        cluster3.recomputeCentroid();
        testResult[2] = cluster3;

        ArrayList<double[]> pts = new ArrayList<>();
        for (KMeansCluster cluster : testResult) {
            pts.addAll(cluster.getPoints2d());
        }
        testData = new double[pts.size()][2];
        for (int i = 0; i < pts.size(); i++) {
            testData[i] = pts.get(i);
        }


    }

    @Test
    public void testFind3Clusters() {
        KMeansCluster[] clusters = KMeans.run2d(testData, testK, testCentroids);
        compareResult(clusters, testResult);
    }

    private static void compareResult(KMeansCluster[] actualClusters, KMeansCluster[] expectedClusters){
        Assert.assertEquals(expectedClusters.length, actualClusters.length);

        int matches = 0;

        for (KMeansCluster actualCluster : actualClusters) {
            boolean foundMatch = false;
            for (KMeansCluster expectedCluster : expectedClusters) {
                if(actualCluster.equals(expectedCluster)){
                    matches++;
                    foundMatch = true;
                    break;
                }
            }
            if(!foundMatch){

                for (KMeansCluster expectedCluster : expectedClusters) {
                    if(actualCluster.equals(expectedCluster)){
                        matches++;
                        foundMatch = true;
                        break;
                    }
                }


                Assert.fail("Could not find a match for cluster: " + actualCluster);



            }
        }

        Assert.assertEquals(testK, matches);

    }
}