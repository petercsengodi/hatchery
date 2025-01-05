package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.common.math.TriangleUtil;
import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.common.lens.EditorTransformation;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshLine;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FreeTriangleMeshWireframe extends FreeTriangleMeshCanvas {

	private double alfa;
	private double beta;

	private boolean mouseRightPressed = false;
	private final Point mouseRightAt = new Point(0, 0);

	private final EditorTransformation editorTransformation = new EditorTransformation();

	protected FreeTriangleMeshLine selectionLine = new FreeTriangleMeshLine();

	public FreeTriangleMeshWireframe(GameEngineFacade facade) {
		super(facade);
		this.lenses.setCustomTransformation(editorTransformation);
	}

	@Override
	protected EditorPoint transformToScreen(EditorPoint p) {
		EditorPoint result = lenses.fromModelToScreen(p.getX(), p.getY(), p.getZ());
		return result; // FIXME
	}

	@Override
	protected EditorPoint transformToModel(int x, int y) {
		return null;
	}

	@Override
	protected void translate(double x, double y) {
	}

	@Override
	protected void zoom(double delta) {
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

		// We need to check which triangle is the mouse hovering over.
		double lastZPosition = Double.POSITIVE_INFINITY;
		FreeTriangleMeshTriangle hoverOverTriangle = null;

		for(FreeTriangleMeshTriangle triangle : triangles) {
			if(model.enabled(triangle)) {
				EditorPoint p1 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex1())));
				EditorPoint p2 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex2())));
				EditorPoint p3 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex3())));

				double zPosition = TriangleUtil.zIfContainedOrInfinity(
						p1.getX(), p1.getY(), p1.getZ(),
						p2.getX(), p2.getY(), p2.getZ(),
						p3.getX(), p3.getY(), p3.getZ(),
						trackedMousePosition.x, trackedMousePosition.y
				);

				if(zPosition < lastZPosition) {
					lastZPosition = zPosition;
					hoverOverTriangle = triangle;
				}
			}
		}

		// Drawing the actual triangles.
		g.setColor(Color.darkGray);
		for(FreeTriangleMeshTriangle triangle : triangles) {
			if(model.enabled(triangle)) {
				EditorPoint p1 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex1())));
				EditorPoint p2 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex2())));
				EditorPoint p3 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex3())));
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

			g.setColor(Color.darkGray);
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
	public void mouseDragged(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();
		modifyAlfaAndBetaIfNeeded(e);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();
		modifyAlfaAndBetaIfNeeded(e);
		repaint();
	}

	private void modifyAlfaAndBetaIfNeeded(MouseEvent e) {
		if(mouseRightPressed) {
			int dx = mouseRightAt.x - e.getX();
			int dy = mouseRightAt.y - e.getY();

			alfa += dx / 100.0;
			if(alfa < -PI2)
				alfa += PI2;
			else if(alfa > PI2)
				alfa -= PI2;

			beta += dy / 100.0;
			if(beta < -BETA_LIMIT)
				beta = -BETA_LIMIT;
			else if(beta > BETA_LIMIT)
				beta = BETA_LIMIT;

			mouseRightAt.x = e.getX();
			mouseRightAt.y = e.getY();
		}
	}

	private static final double PI2 = 2*Math.PI;
	private static final double BETA_LIMIT = Math.PI / 2;

	private static final long serialVersionUID = 1L;
}
