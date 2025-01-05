package hu.csega.editors.ftm.layer1.presentation.swing.view;

import static org.junit.Assert.assertTrue;

import hu.csega.editors.common.lens.EditorLensPipeline;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

import org.junit.Test;

public class FreeTriangleMeshHoverOverCalculationsTest {

    @Test
    public void testCamera() {
        final FreeTriangleMeshModel model = new FreeTriangleMeshModel();
        model.createVertexAt(-2, 4, 2);
        model.createVertexAt(2, 4, 2);
        model.createVertexAt(0, 0, 2);
        model.selectAll();
        model.createTriangleStrip();
        model.setOpenGLZoomIndex(15);
        model.setOpenGLAlpha(Math.PI / 2.0);

        final EditorLensPipeline lenses = new EditorLensPipeline();
        final FreeTriangleMeshHoverOverCalculations calculations = new FreeTriangleMeshHoverOverCalculations(lenses);
        calculations.doCalculations(model, Integer.MIN_VALUE, Integer.MIN_VALUE, 640, 480, null);

        double minZ = calculations.getMinZ();
        double maxZ = calculations.getMaxZ();
        assertTrue(minZ > 0.98 && minZ < 0.99);
        assertTrue(maxZ > 0.98 && maxZ < 0.99);
    }

}
