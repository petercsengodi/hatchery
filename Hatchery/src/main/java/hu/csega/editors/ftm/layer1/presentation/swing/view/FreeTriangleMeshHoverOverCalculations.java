package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.common.math.TriangleUtil;
import hu.csega.editors.common.lens.EditorLensClippingCoordinatesTransformationImpl;
import hu.csega.editors.common.lens.EditorLensPipeline;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.common.lens.EditorTransformation;
import hu.csega.games.engine.g3d.GameObjectDirection;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameObjectPosition;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;

import java.util.List;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class FreeTriangleMeshHoverOverCalculations {

    private double maxZ = Double.NEGATIVE_INFINITY;
    private double minZ = Double.POSITIVE_INFINITY;

    private final EditorLensPipeline lenses;

    private final EditorTransformation editorTransformation = new EditorTransformation();
    private final EditorLensClippingCoordinatesTransformationImpl clippingCoordinatesTransformation = new EditorLensClippingCoordinatesTransformationImpl();

    // Needed for model transformation, which for now is an identity transformation.
    private final GameObjectPlacement modelPlacement = new GameObjectPlacement();
    private final Vector4f outEye = new Vector4f();
    private final Vector4f outCenter = new Vector4f();
    private final Vector4f outUp = new Vector4f();
    private final Matrix4f outBasicLookAt = new Matrix4f();
    private final Matrix4f outInverseLookAt = new Matrix4f();
    private final Matrix4f outBasicScale = new Matrix4f();

    // Needed for camera transformation.
    private final GameObjectPosition cameraPosition = new GameObjectPosition(0f, 0f, 0f);
    private final GameObjectPosition cameraTarget = new GameObjectPosition(0f, 0f, 0f);
    private final GameObjectDirection cameraUp = new GameObjectDirection(0f, 1f, 0f);
    private final GameObjectPlacement cameraPlacement = new GameObjectPlacement();
    private final Matrix4d cameraMatrix = new Matrix4d();

    // Needed for perspective transformation.
    private final double viewAngle = (float) Math.toRadians(45);
    private static final double zNear = 0.1f;
    private static final double zFar = 10000.0f;
    private final Matrix4d perspectiveMatrix = new Matrix4d();

    // Needed for merging transformations together.
    private final Matrix4d calculatedMatrix = new Matrix4d();

    public FreeTriangleMeshHoverOverCalculations(EditorLensPipeline lenses) {
        this.modelPlacement.target.set(0f, 0f, 1f); // TODO: Why not -1f ????
        this.modelPlacement.up.set(0f, 1f, 0f);

        this.lenses = lenses;
        this.lenses.setCustomTransformation(editorTransformation);
        this.lenses.setScreenTransformation(clippingCoordinatesTransformation);
    }

    /**
     * @param mouseX If Integer.MIN_VALUE or Integer.MAX_VALUE, hover-over will not be updated, only the screen transformations will be re-calculated.
     * @param mouseY If Integer.MIN_VALUE or Integer.MAX_VALUE, hover-over will not be updated, only the screen transformations will be re-calculated.
     */
    public void doCalculations(FreeTriangleMeshModel model, int mouseX, int mouseY, int windowWidth, int windowHeight, Boolean counterClockwise) {
        if(windowWidth < 10 || windowHeight < 10) {
            model.setHoverOverObject(null);
            return;
        }

        boolean updateHoverOver = true;
        if(mouseX == Integer.MIN_VALUE || mouseX == Integer.MAX_VALUE || mouseY == Integer.MIN_VALUE || mouseY == Integer.MAX_VALUE) {
            updateHoverOver = false;
        }

        // Create transformations same as in OpenGL.

        // -> model
        this.modelPlacement.calculateBasicLookAt(outBasicLookAt);
        this.modelPlacement.calculateInverseLookAt(outBasicLookAt, outEye, outCenter, outUp, outInverseLookAt);
        this.modelPlacement.calculateBasicScaleMatrix(outBasicScale);

        // -> camera
        double alfa = model.getOpenGLAlpha();
        double beta = model.getOpenGLBeta();
        double distance = model.getOpenGLZoom();
        double y = distance * Math.sin(beta);
        double distanceReduced = distance * Math.cos(beta);
        this.cameraPosition.x = (float)(Math.cos(alfa) * distanceReduced);
        this.cameraPosition.y = (float) y;
        this.cameraPosition.z = (float)(Math.sin(alfa) * distanceReduced);
        this.cameraPlacement.setPositionTargetUp(this.cameraPosition, this.cameraTarget, this.cameraUp);
        this.cameraPlacement.calculateBasicLookAt(this.cameraMatrix);

        // -> perspective
        double aspect = (float) windowWidth / windowHeight;
        this.perspectiveMatrix.identity().setPerspective(viewAngle, aspect, zNear, zFar);

        // -> apply all
        calculatedMatrix.set(perspectiveMatrix);
        calculatedMatrix.mul(cameraMatrix);
        calculatedMatrix.mul(outInverseLookAt);
        calculatedMatrix.mul(outBasicScale);
        this.editorTransformation.setTransformation(this.calculatedMatrix);
        this.clippingCoordinatesTransformation.setWindowWidth(windowWidth);
        this.clippingCoordinatesTransformation.setWindowHeight(windowHeight);
        List<FreeTriangleMeshVertex> vertices = model.getVertices();
        List<FreeTriangleMeshTriangle> triangles = model.getTriangles();

        maxZ = Double.NEGATIVE_INFINITY;
        minZ = Double.POSITIVE_INFINITY;

        // We need to check which triangle is the mouse hovering over.
        double lastZPosition = Double.POSITIVE_INFINITY;
        FreeTriangleMeshTriangle hoverOverTriangle = null;

        for(FreeTriangleMeshTriangle triangle : triangles) {
            if(model.enabled(triangle)) {
                EditorPoint p1 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex1())));
                EditorPoint p2 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex2())));
                EditorPoint p3 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex3())));

                if(maxZ < p1.getZ()) { maxZ = p1.getZ(); }
                if(maxZ < p2.getZ()) { maxZ = p2.getZ(); }
                if(maxZ < p3.getZ()) { maxZ = p3.getZ(); }
                if(minZ > p1.getZ()) { minZ = p1.getZ(); }
                if(minZ > p2.getZ()) { minZ = p2.getZ(); }
                if(minZ > p3.getZ()) { minZ = p3.getZ(); }

                if(updateHoverOver) {
                    double zPosition = TriangleUtil.zIfContainedOrInfinity(
                            p1.getX(), p1.getY(), p1.getZ(),
                            p2.getX(), p2.getY(), p2.getZ(),
                            p3.getX(), p3.getY(), p3.getZ(),
                            mouseX, mouseY, counterClockwise
                    );

                    if (lastZPosition == Double.POSITIVE_INFINITY || zPosition < lastZPosition) {
                        lastZPosition = zPosition;
                        hoverOverTriangle = triangle;
                    }
                }
            }
        }

        if(updateHoverOver)
            model.setHoverOverObject(hoverOverTriangle);
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxZ() {
        return maxZ;
    }

    private EditorPoint transformVertexToPoint(FreeTriangleMeshVertex vertex) {
        return new EditorPoint(vertex.getPX(), vertex.getPY(), vertex.getPZ(), 1.0);
    }

    private EditorPoint transformToScreen(EditorPoint p) {
        return lenses.fromModelToScreen(p.getX(), p.getY(), p.getZ());
    }
}
