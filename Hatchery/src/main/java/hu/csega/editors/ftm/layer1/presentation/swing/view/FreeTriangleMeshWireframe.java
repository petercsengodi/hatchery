package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.common.math.ScalarUtil;
import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.editors.anm.layer1.swing.views.AnimatorObject;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshLine;
import hu.csega.games.engine.GameEngineFacade;
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

public class FreeTriangleMeshWireframe extends FreeTriangleMeshCanvas {

	private static final Boolean DO_NOT_CHECK_FACING_DIRECTION = null;

	private final GameCanvas gameCanvas;
	private final FreeTriangleMeshHoverOverCalculations hoverOverCalculations;

	protected FreeTriangleMeshLine selectionLine = new FreeTriangleMeshLine();

	public FreeTriangleMeshWireframe(GameEngineFacade facade, GameCanvas gameCanvas) {
		super(facade);
		this.gameCanvas = gameCanvas;
		this.hoverOverCalculations = new FreeTriangleMeshHoverOverCalculations(this.lenses);
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
		Collection<AnimatorObject> selectedObjects = model.getSelectedObjects();

		List<FreeTriangleMeshVertex> vertices = model.getVertices();
		List<FreeTriangleMeshTriangle> triangles = model.getTriangles();

		hoverOverCalculations.doCalculations(model, Integer.MIN_VALUE, Integer.MIN_VALUE, getWidth(), getHeight(), DO_NOT_CHECK_FACING_DIRECTION);
		final double minZ = hoverOverCalculations.getMinZ();
		final double maxZ = hoverOverCalculations.getMaxZ();
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
				FreeTriangleMeshVertex v1 = vertices.get(triangle.getVertex1());
				FreeTriangleMeshVertex v2 = vertices.get(triangle.getVertex2());
				FreeTriangleMeshVertex v3 = vertices.get(triangle.getVertex3());

				EditorPoint p1 = transformToScreen(transformVertexToPoint(v1));
				EditorPoint p2 = transformToScreen(transformVertexToPoint(v2));
				EditorPoint p3 = transformToScreen(transformVertexToPoint(v3));

				boolean v1Selected = selectedObjects.contains(v1);
				boolean v2Selected = selectedObjects.contains(v2);
				boolean v3Selected = selectedObjects.contains(v3);

				if(v1Selected && v2Selected) { g.setColor(Color.red); } else { setColorForEditorPoints(g, p1, p2, minZ, diffZ); }
				drawLine(g, p1, p2);

				if(v2Selected && v3Selected) { g.setColor(Color.red); } else { setColorForEditorPoints(g, p2, p3, minZ, diffZ); }
				drawLine(g, p2, p3);

				if(v3Selected && v1Selected) { g.setColor(Color.red); } else { setColorForEditorPoints(g, p3, p1, minZ, diffZ); }
				drawLine(g, p3, p1);

			} // end enabled triangle
		} // end for triangle

		// Drawing the hover-over-triangle above everything with a thicker line.
		Object hoverOverObject = model.getHoverOverObject();
		if(hoverOverObject instanceof FreeTriangleMeshTriangle) {
			FreeTriangleMeshTriangle triangle = (FreeTriangleMeshTriangle) hoverOverObject;

			Stroke stroke = g.getStroke();
			g.setStroke(new BasicStroke(2));
			g.setColor(Color.magenta);

			EditorPoint p1 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex1())));
			EditorPoint p2 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex2())));
			EditorPoint p3 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex3())));
			drawLine(g, p1, p2);
			drawLine(g, p2, p3);
			drawLine(g, p3, p1);

			g.setStroke(stroke);
		} // end if hover-over-triangle is not null

		// Drawing the vertices.
		for(FreeTriangleMeshVertex vertex : vertices) {
			if(model.enabled(vertex)) {
				EditorPoint p = transformVertexToPoint(vertex);
				EditorPoint transformed = transformToScreen(p);
				if(selectedObjects.contains(vertex)) {
					g.setColor(Color.red);
				} else {
					setColorForEditorPoint(g, transformed, minZ, diffZ);
				}

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
		Collection<AnimatorObject> selectedObjects = model.getSelectedObjects();
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

			for(AnimatorObject obj : selectedObjects) {
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
	public void mouseClicked(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), getWidth(), getHeight(), DO_NOT_CHECK_FACING_DIRECTION);

		boolean repaintRequestedAlready = false;

		if(e.getButton() == MouseEvent.BUTTON1) {
			Object hoverOverObject = model.getHoverOverObject();
			if(hoverOverObject instanceof FreeTriangleMeshTriangle) {
				model.selectTriangle((FreeTriangleMeshTriangle) hoverOverObject);
				repaintRequestedAlready = true;
				facade.window().repaintEverything();
			}
		}

		if(!repaintRequestedAlready) {
			gameCanvas.repaint();
			repaint();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		int numberOfRotations = e.getWheelRotation();
		FreeTriangleMeshModel model = getModel();
		model.modifyOpenGLZoomIndex(numberOfRotations);

		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), getWidth(), getHeight(), DO_NOT_CHECK_FACING_DIRECTION);

		gameCanvas.repaint();
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		modifyAlfaAndBetaIfNeeded(e);

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), getWidth(), getHeight(), DO_NOT_CHECK_FACING_DIRECTION);
		gameCanvas.repaint();
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		modifyAlfaAndBetaIfNeeded(e);

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), getWidth(), getHeight(), DO_NOT_CHECK_FACING_DIRECTION);
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

	private static void setColorForEditorPoint(Graphics2D g, EditorPoint p, double minZ, double diffZ) {
		double colorRatio = (p.getZ() - minZ) / diffZ;
		int colorIndex = (int) Math.floor(colorRatio * 16);
		if(colorIndex < 0) {
			colorIndex = 0;
		} else if(colorIndex >= COLOR_TABLE.length) {
			colorIndex = COLOR_TABLE.length - 1;
		}

		g.setColor(COLOR_TABLE[colorIndex]);
	}

	private static void setColorForEditorPoints(Graphics2D g, EditorPoint p1, EditorPoint p2, double minZ, double diffZ) {
		double colorAvg = (p1.getZ() + p2.getZ()) /  2.0;
		double colorRatio = (colorAvg - minZ) / diffZ;
		int colorIndex = (int) Math.floor(colorRatio * 16);
		if(colorIndex < 0) {
			colorIndex = 0;
		} else if(colorIndex >= COLOR_TABLE.length) {
			colorIndex = COLOR_TABLE.length - 1;
		}

		g.setColor(COLOR_TABLE[colorIndex]);
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
