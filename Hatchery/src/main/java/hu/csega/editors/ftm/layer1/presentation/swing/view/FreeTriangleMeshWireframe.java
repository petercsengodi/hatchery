package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.common.math.ScalarUtil;
import hu.csega.common.math.TriangleUtil;
import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.editors.common.lens.EditorLensClippingCoordinatesTransformationImpl;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.common.lens.EditorTransformation;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshLine;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectDirection;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameObjectPosition;
import hu.csega.games.engine.intf.GameCanvas;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class FreeTriangleMeshWireframe extends FreeTriangleMeshCanvas {

	private final GameCanvas gameCanvas;
	private final EditorTransformation editorTransformation = new EditorTransformation();
	private final EditorLensClippingCoordinatesTransformationImpl clippingCoordinatesTransformation = new EditorLensClippingCoordinatesTransformationImpl();

	protected FreeTriangleMeshLine selectionLine = new FreeTriangleMeshLine();

	// Needed for model transformation, which for now is an identity transformation.
	private GameObjectPlacement modelPlacement = new GameObjectPlacement();
	private Vector4f outEye = new Vector4f();
	private Vector4f outCenter = new Vector4f();
	private Vector4f outUp = new Vector4f();
	private Matrix4f outBasicLookAt = new Matrix4f();
	private Matrix4f outInverseLookAt = new Matrix4f();
	private Matrix4f outBasicScale = new Matrix4f();
	private final Matrix4d modelMatrix = new Matrix4d();

	// Needed for camera transformation.
	private GameObjectPosition cameraPosition = new GameObjectPosition(0f, 0f, 0f);
	private GameObjectPosition cameraTarget = new GameObjectPosition(0f, 0f, 0f);
	private GameObjectDirection cameraUp = new GameObjectDirection(0f, 1f, 0f);
	private GameObjectPlacement cameraPlacement = new GameObjectPlacement();
	private final Matrix4d cameraMatrix = new Matrix4d();

	// Needed for perspective transformation.
	private final double viewAngle = (float) Math.toRadians(45);
	private final double zNear = 0.1f;
	private final double zFar = 10000.0f;
	private final Matrix4d perspectiveMatrix = new Matrix4d();

	// Needed for merging transformations together.
	private final Matrix4d calculatedMatrix = new Matrix4d();

	public FreeTriangleMeshWireframe(GameEngineFacade facade, GameCanvas gameCanvas) {
		super(facade);
		this.gameCanvas = gameCanvas;

		this.modelPlacement.target.set(0f, 0f, 1f); // TODO: Why not -1f ????
		this.modelPlacement.up.set(0f, 1f, 0f);
		this.lenses.setCustomTransformation(editorTransformation);
		this.lenses.setScreenTransformation(clippingCoordinatesTransformation);
	}

	@Override
	protected EditorPoint transformToScreen(EditorPoint p) {
		return lenses.fromModelToScreen(p.getX(), p.getY(), p.getZ());
	}

	@Override
	protected EditorPoint transformToModel(int x, int y) {
		return null;
	}

	@Override
	protected void translate(double x, double y) {
	}

	@Override
	protected void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add) {
	}

	@Override
	protected void selectFirst(EditorPoint p, double radius, boolean add) {
	}

	public String label() {
		return "Wireframe";
	}

	@Override
	protected void paint2d(Graphics2D g) {
		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		Collection<Object> selectedObjects = model.getSelectedObjects();

		List<FreeTriangleMeshVertex> vertices = model.getVertices();
		List<FreeTriangleMeshTriangle> triangles = model.getTriangles();

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
		int windowWidth = this.getWidth();
		int windowHeight = this.getHeight();
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

		// -------------

		double maxZ = Double.NEGATIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;

		// We need to check which triangle is the mouse hovering over.
		double lastZPosition = Double.POSITIVE_INFINITY;
		FreeTriangleMeshTriangle hoverOverTriangle = null;
		double tx = trackedMousePosition.x;
		double ty = trackedMousePosition.y;

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

				double zPosition = TriangleUtil.zIfContainedOrInfinity(
						p1.getX(), p1.getY(), p1.getZ(),
						p2.getX(), p2.getY(), p2.getZ(),
						p3.getX(), p3.getY(), p3.getZ(),
						tx, ty
				);

				if(zPosition < lastZPosition) {
					lastZPosition = zPosition;
					hoverOverTriangle = triangle;
				}
			}
		}

		final double diffZ;
		if(maxZ != Double.NEGATIVE_INFINITY && minZ != Double.POSITIVE_INFINITY &&
				maxZ - minZ > ScalarUtil.EPSILON) { // Intentionally left out <abs>, as negative is also not okay.
			diffZ = (maxZ - minZ);
		} else {
			diffZ = 100.0;
		}

		// Drawing the actual triangles.
		for(FreeTriangleMeshTriangle triangle : triangles) {
			if(model.enabled(triangle)) {
				EditorPoint p1 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex1())));
				EditorPoint p2 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex2())));
				EditorPoint p3 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex3())));

				double colorAvg = (p1.getZ() + p2.getZ() + p3.getZ()) /  3.0;
				double colorRatio = (colorAvg - minZ) / diffZ;
				int colorIndex = (int) Math.floor(colorRatio * 16);
				if(colorIndex < 0) {
					colorIndex = 0;
				} else if(colorIndex >= COLOR_TABLE.length) {
					colorIndex = COLOR_TABLE.length - 1;
				}

				g.setColor(COLOR_TABLE[colorIndex]);

				drawLine(g, p1, p2);
				drawLine(g, p2, p3);
				drawLine(g, p3, p1);
			} // end enabled triangle
		} // end for triangle

		// Drawing the hover-over-triangle above everything with a thicker line.
		if(hoverOverTriangle != null) {
			Stroke stroke = g.getStroke();
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.red);

			EditorPoint p1 = transformToScreen(transformVertexToPoint(vertices.get(hoverOverTriangle.getVertex1())));
			EditorPoint p2 = transformToScreen(transformVertexToPoint(vertices.get(hoverOverTriangle.getVertex2())));
			EditorPoint p3 = transformToScreen(transformVertexToPoint(vertices.get(hoverOverTriangle.getVertex3())));
			drawLine(g, p1, p2);
			drawLine(g, p2, p3);
			drawLine(g, p3, p1);

			g.setStroke(stroke);
		} // end if hover-over-triangle is not null

		// Drawing the vertices.
		for(FreeTriangleMeshVertex vertex : vertices) {
			if(model.enabled(vertex)) {
				if(selectedObjects.contains(vertex)) {
					g.setColor(Color.red);
				} else {
					g.setColor(Color.black);
				}

				EditorPoint p = transformVertexToPoint(vertex);
				EditorPoint transformed = transformToScreen(p);
				g.drawRect((int)transformed.getX() - 2, (int)transformed.getY() - 2, 5, 5);
			}
		}

		// Marking the center.
		Stroke stroke = g.getStroke();
		g.setStroke(new BasicStroke(3));
		g.setColor(Color.PINK);
		EditorPoint centerEP = new EditorPoint(0.0, 0.0, 0.0, 1.0);
		centerEP = transformToScreen(centerEP);
		int centerX = (int) centerEP.getX();
		int centerY = (int) centerEP.getY();
		g.drawLine(centerX - 10, centerY - 10, centerX + 10, centerY + 10);
		g.drawLine(centerX - 10, centerY + 10, centerX + 10, centerY - 10);
		g.setStroke(stroke);

		// Drawing the draggable pictograms.
		Set<FreeTriangleMeshPictogram> pictograms = refreshPictograms(model);
		if(pictograms != null && !pictograms.isEmpty()) {
			for(FreeTriangleMeshPictogram p : pictograms) {
				BufferedImage img = FreeTriangleMeshToolStarter.SPRITES[p.action];
				g.drawImage(img, (int)p.x, (int)p.y, null);
			}
		}

		// Drawing the label.
		g.setColor(Color.BLACK);
		g.drawLine(0, 0, 300, 0);
		g.drawString(label(), 10, 20);

		// Drawing the selection box.
		Rectangle selectionBox = calculateSelectionBox();
		if(selectionBox != null) {
			g.setColor(Color.red);
			calculateSelectionBox();
			g.drawRect(selectionBox.x, selectionBox.y, selectionBox.width, selectionBox.height);
		}
	}

	@Override
	protected void createVertexAt(EditorPoint p) {
		FreeTriangleMeshModel model = getModel();
		model.createVertexAt(p.getX(), p.getY(), p.getZ());
	}

	@Override
	protected void moveSelected(EditorPoint p1, EditorPoint p2) {
		FreeTriangleMeshModel model = getModel();

		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		double dz = p2.getZ() - p1.getZ();

		model.moveSelected(dx, dy, dz);
		somethingChanged();
	}

	@Override
	protected EditorPoint transformVertexToPoint(FreeTriangleMeshVertex vertex) {
		return new EditorPoint(vertex.getPX(), vertex.getPY(), vertex.getPZ(), 1.0);
	}

	protected void drawLine(Graphics2D g, EditorPoint end1, EditorPoint end2) {
		g.drawLine((int)end1.getX(), (int)end1.getY(), (int)end2.getX(), (int)end2.getY());
	}

	protected void drawRectangle(Graphics2D g, EditorPoint end1, EditorPoint end2) {
		g.drawRect((int)end1.getX(), (int)end1.getY(), (int)(end2.getX() - end1.getX()), (int)(end2.getY() - end1.getY()));
	}

	@Override
	protected Set<FreeTriangleMeshPictogram> refreshPictograms(FreeTriangleMeshModel model) {
		Collection<Object> selectedObjects = model.getSelectedObjects();
		if(selectedObjects == null || selectedObjects.size() < 2) {
			return null;
		}

		if(pictograms == null || selectionLastChanged < model.getSelectionLastChanged()) {
			selectionLastChanged = model.getSelectionLastChanged();
			pictograms = new HashSet<>();

			selectionMinX = Integer.MAX_VALUE;
			selectionMinY = Integer.MAX_VALUE;
			selectionMaxX = Integer.MIN_VALUE;
			selectionMaxY = Integer.MIN_VALUE;

			for(Object obj : selectedObjects) {
				if(obj instanceof FreeTriangleMeshVertex) {
					FreeTriangleMeshVertex v = ((FreeTriangleMeshVertex)obj);
					EditorPoint p = transformToScreen(transformVertexToPoint(v));

					int x = (int) p.getX();
					int y = (int) p.getY();
					if(x < selectionMinX) { selectionMinX = x; }
					if(y < selectionMinY) { selectionMinY = y; }
					if(x > selectionMaxX) { selectionMaxX = x; }
					if(y > selectionMaxY) { selectionMaxY = y; }
				}
			}

			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_LEFT_ARROW, selectionMinX - 16, selectionMinY - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_RIGHT_ARROW, selectionMaxX, selectionMinY - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_LEFT_ARROW, selectionMinX - 16, selectionMaxY));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW, selectionMaxX, selectionMaxY));
		}

		return pictograms;
	}

	@Override
	protected void pictogramAction(int action, int dx, int dy, EditorPoint started, EditorPoint ended) {
		FreeTriangleMeshModel model = getModel();
		switch(action) {
			case FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW: {
				EditorPoint fixed = transformToModel(selectionMinX, selectionMinY);
				if(fixed != null) {
					model.elasticMove(fixed, started, ended);
				}
			} break;
			case FreeTriangleMeshPictogram.UP_LEFT_ARROW: {
				EditorPoint fixed = transformToModel(selectionMaxX, selectionMaxY);
				if(fixed != null) {
					model.elasticMove(fixed, started, ended);
				}
			} break;
			case FreeTriangleMeshPictogram.UP_RIGHT_ARROW: {
				EditorPoint fixed = transformToModel(selectionMinX, selectionMaxY);
				if(fixed != null) {
					model.elasticMove(fixed, started, ended);
				}
			} break;
			case FreeTriangleMeshPictogram.DOWN_LEFT_ARROW: {
				EditorPoint fixed = transformToModel(selectionMaxX, selectionMinY);
				if(fixed != null) {
					model.elasticMove(fixed, started, ended);
				}
			} break;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		int numberOfRotations = e.getWheelRotation();
		FreeTriangleMeshModel model = getModel();
		model.modifyOpenGLZoomIndex(numberOfRotations);

		// FIXME: Scaling in lenses.

		gameCanvas.repaint();
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();
		modifyAlfaAndBetaIfNeeded(e);
		gameCanvas.repaint();
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();
		modifyAlfaAndBetaIfNeeded(e);
		gameCanvas.repaint();
		repaint();
	}

	private void modifyAlfaAndBetaIfNeeded(MouseEvent e) {
		if(mouseRightPressed) {
			int dx = mouseRightAt.x - e.getX();
			int dy = mouseRightAt.y - e.getY();

			FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
			model.modifyOpenGLAlpha(dx / 100.0);
			model.modifyOpenGLBeta(dy / 100.0);
			mouseRightAt.x = e.getX();
			mouseRightAt.y = e.getY();
		}
	}

	private static final Color[] COLOR_TABLE = new Color[16];

	static {
		// 0 - black, 15 - lightest grey
		int colorStep = 10;
		for(int i = 0; i < 16; i++) {
			COLOR_TABLE[i] = new Color(colorStep * i, colorStep * i, colorStep * i);
		}
	}

	private static final long serialVersionUID = 1L;
}
