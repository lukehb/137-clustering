package onethreeseven.clustering.graphic;

import onethreeseven.clustering.model.Cluster;
import onethreeseven.common.util.ColorUtil;
import onethreeseven.trajsuitePlugin.graphics.GraphicsPayload;
import onethreeseven.trajsuitePlugin.graphics.PackedVertexData;
import onethreeseven.trajsuitePlugin.graphics.RenderingModes;
import onethreeseven.trajsuitePlugin.model.BoundingCoordinates;
import onethreeseven.trajsuitePlugin.settings.PluginSettings;

import java.nio.DoubleBuffer;
import java.util.Collection;

/**
 * Graphics for rendering {@link Cluster}
 * @author Luke Bermingham
 */
public class ClusterGraphic extends GraphicsPayload {

    public ClusterGraphic(){
        this.doScalePointsOrLines.setValue(true);
        this.pointOrLineSize.setValue(10);
        this.smoothPoints.setValue(false);
    }

    @Override
    protected RenderingModes defaultRenderingMode() {
        return RenderingModes.POINTS;
    }

    @Override
    public PackedVertexData createVertexData(BoundingCoordinates model) {

        Collection<double[]> points2d = ((Cluster) model).getPoints2d();

        final double ele = PluginSettings.smallElevation.getSetting();

        DoubleBuffer buf = DoubleBuffer.allocate(points2d.size() * 3);

        for (double[] point2d : points2d) {
            buf.put(point2d);
            buf.put(ele);
        }

        PackedVertexData.Types[] vertTypes = new PackedVertexData.Types[]{
                PackedVertexData.Types.VERTEX
        };

        return new PackedVertexData(buf, vertTypes);
    }
}
