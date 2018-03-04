package onethreeseven.clustering.algorithm;

import onethreeseven.clustering.model.DBScanCluster;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collection;


public class DBScanTest {
    private static final double testEpsilon = 10.0;
    private static final int testMinPts = 4;

    private static final double[][] testData;
    private static final Collection<DBScanCluster> testResult;

    static {
        testResult = new ArrayList<>();

        DBScanCluster cluster1 = new DBScanCluster(false);
        DBScanCluster cluster2 = new DBScanCluster(false);
        DBScanCluster cluster3 = new DBScanCluster(false);
        //cluster #4 is noise
        DBScanCluster cluster4 = new DBScanCluster(true);
        testResult.add(cluster1);
        testResult.add(cluster2);
        testResult.add(cluster3);
        testResult.add(cluster4);

        //cluster 1
        cluster1.add(new double [] { 146.0, 41.0 });
        cluster1.add(new double [] { 143.0, 42.0 });
        cluster1.add(new double [] { 141.0, 43.0 });
        cluster1.add(new double [] { 140.0, 44.0 });
        cluster1.add(new double [] { 143.0, 45.0 });
        cluster1.add(new double [] { 146.0, 42.0 });
        cluster1.add(new double [] { 141.0, 50.0 });

        //cluster 2
        cluster2.add(new double [] { 220.0, 142.0 });
        cluster2.add(new double [] { 224.0, 145.0 });
        cluster2.add(new double [] { 215.0, 141.0 });
        cluster2.add(new double [] { 230.0, 148.0 });
        cluster2.add(new double [] { 221.0, 141.0 });
        cluster2.add(new double [] { 231.0, 142.0 });
        cluster2.add(new double [] { 231.0, 141.0 });
        cluster2.add(new double [] { 231.0, 143.0 });
        cluster2.add(new double [] { 231.0, 144.0 });
        cluster2.add(new double [] { 230.0, 140.0 });

        //cluster 3
        cluster3.add(new double [] { 2146.0, 141.0 });
        cluster3.add(new double [] { 2143.0, 142.0 });
        cluster3.add(new double [] { 2141.0, 143.0 });
        cluster3.add(new double [] { 2140.0, 144.0 });
        cluster3.add(new double [] { 2143.0, 145.0 });
        cluster3.add(new double [] { 2146.0, 142.0 });
        cluster3.add(new double [] { 2141.0, 150.0 });

        //cluster 4
        cluster4.add(new double [] { 1430.0, 421.0 });
        cluster4.add(new double [] { 200.0, 142.0 });
        cluster4.add(new double [] { 200.0, 145.0 });
        cluster4.add(new double [] { 2301.0, 140.0 });
        cluster4.add(new double [] { 2130.0, 140.0 });
        cluster4.add(new double [] { 230.0, 1140.0 });

        int totalPts = 0;
        for (DBScanCluster cluster : testResult) {
            totalPts += cluster.getPoints2d().size();
        }

        testData = new double[totalPts][2];
        int i = 0;
        for (DBScanCluster cluster : testResult) {
            for (double[] point2d : cluster.getPoints2d()) {
                testData[i] = point2d;
                i++;
            }
        }

    }

    @Test
    public void testDBSCAN2d() {
        Collection<DBScanCluster> clusters = DBScan.run2d(testData, testEpsilon, testMinPts);
        compareResult(clusters, testResult);
    }

    private static void compareResult(Collection<DBScanCluster> actualClusters, Collection<DBScanCluster> expectedClusters){
        Assert.assertEquals(expectedClusters.size(), actualClusters.size());

        int nMatches = 0;

        for (DBScanCluster actualCluster : actualClusters) {
            boolean foundMatch = false;
            for (DBScanCluster expectedCluster : expectedClusters) {
                if(actualCluster.equals(expectedCluster)){
                    foundMatch = true;
                    break;
                }
            }

            if(!foundMatch){
                Assert.fail("Could not find a match for cluster: " + actualCluster);
            }else{
                nMatches++;
            }

        }

        Assert.assertTrue(nMatches == actualClusters.size());

    }
}