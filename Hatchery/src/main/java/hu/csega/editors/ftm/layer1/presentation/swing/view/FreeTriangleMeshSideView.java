package hu.csega.editors.ftm.layer1.presentation.swing.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.csega.editors.FreeTriangleMeshToolStarter;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshLine;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.editors.ftm.util.FreeTriangleMeshSphereLineIntersection;
import hu.csega.games.engine.GameEngineFacade;

public abstract class FreeTriangleMeshSideView extends FreeTriangleMeshCanvas {

	protected FreeTriangleMeshLine selectionLine = new FreeTriangleMeshLine();
	protected FreeTriangleMeshSphereLineIntersection intersection = new FreeTriangleMeshSphereLineIntersection();

	public FreeTriangleMeshSideView(GameEngineFacade facade) {
		super(facade);
	}

	public abstract String label();

	@Override
	protected void paint2d(Graphics2D g) {
		drawGrid(g);

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		Collection<Object> selectedObjects = model.getSelectedObjects();

		List<FreeTriangleMeshVertex> vertices = model.getVertices();
		List<FreeTriangleMeshTriangle> triangles = model.getTriangles();

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

				g.setColor(v1Selected && v2Selected ? Color.red : Color.black);
				drawLine(g, p1, p2);

				g.setColor(v2Selected && v3Selected ? Color.red : Color.black);
				drawLine(g, p2, p3);

				g.setColor(v3Selected && v1Selected ? Color.red : Color.black);
				drawLine(g, p3, p1);
			}
		}

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

		Set<FreeTriangleMeshPictogram> pictograms = refreshPictograms(model);
		if(pictograms != null && !pictograms.isEmpty()) {
			for(FreeTriangleMeshPictogram p : pictograms) {
				BufferedImage img = FreeTriangleMeshToolStarter.SPRITES[p.action];
				g.drawImage(img, (int)p.x, (int)p.y, null);
			}
		}

		g.setColor(Color.BLACK);
		g.drawLine(0, 0, 300, 0);
		g.drawString(label(), 10, 20);

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

	protected abstract void drawGrid(Graphics2D g);

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

	private static final long serialVersionUID = 1L;
}
