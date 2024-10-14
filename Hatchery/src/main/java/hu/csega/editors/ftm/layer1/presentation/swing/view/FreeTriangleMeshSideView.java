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
		int widthDiv2 = lastSize.width / 2;
		int heightDiv2 = lastSize.height / 2;
		g.translate(widthDiv2, heightDiv2);

		//		SwingGraphics graphics = new SwingGraphics(g, lastSize.width, heightDiv2);
		//		gameEngine.runStep(GameEngineStep.RENDER, graphics);

		{
			g.setColor(Color.WHITE);

			for(int x = -400; x <= 400; x += 20) {
				EditorPoint p1 = transformToScreen(new EditorPoint(x, -400, 0.0, 1.0));
				EditorPoint p2 = transformToScreen(new EditorPoint(x, 400, 0.0, 1.0));
				drawLine(g, p1, p2);
			}

			for(int y = -400; y <= 400; y += 20) {
				EditorPoint p1 = transformToScreen(new EditorPoint(-400, y, 0.0, 1.0));
				EditorPoint p2 = transformToScreen(new EditorPoint(400, y, 0.0, 1.0));
				drawLine(g, p1, p2);
			}

			EditorPoint p1 = transformToScreen(new EditorPoint(-10, -10, 0.0, 1.0));
			EditorPoint p2 = transformToScreen(new EditorPoint(10, 10, 0.0, 1.0));
			drawRectangle(g, p1, p2);
		}

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		Collection<Object> selectedObjects = model.getSelectedObjects();

		List<FreeTriangleMeshVertex> vertices = model.getVertices();
		List<FreeTriangleMeshTriangle> triangles = model.getTriangles();

		g.setColor(Color.darkGray);
		for(FreeTriangleMeshTriangle triangle : triangles) {
			if(model.enabled(triangle)) {
				EditorPoint p1 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex1())));
				EditorPoint p2 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex2())));
				EditorPoint p3 = transformToScreen(transformVertexToPoint(vertices.get(triangle.getVertex3())));
				drawLine(g, p1, p2);
				drawLine(g, p2, p3);
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

		g.translate(-widthDiv2, -heightDiv2);

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

	private EditorPoint transformToScreen(EditorPoint p) {
		return lenses.fromModelToScreen(p.getX(), p.getY(), 0.0);
	}

	private void drawLine(Graphics2D g, EditorPoint end1, EditorPoint end2) {
		g.drawLine((int)end1.getX(), (int)end1.getY(), (int)end2.getX(), (int)end2.getY());
	}

	private void drawRectangle(Graphics2D g, EditorPoint end1, EditorPoint end2) {
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
			int widthDiv2 = lastSize.width / 2;
			int heightDiv2 = lastSize.height / 2;

			selectionMinX = Double.POSITIVE_INFINITY;
			selectionMinY = Double.POSITIVE_INFINITY;
			selectionMaxX = Double.NEGATIVE_INFINITY;
			selectionMaxY = Double.NEGATIVE_INFINITY;

			for(Object obj : selectedObjects) {
				if(obj instanceof FreeTriangleMeshVertex) {
					FreeTriangleMeshVertex v = ((FreeTriangleMeshVertex)obj);
					EditorPoint p = transformToScreen(transformVertexToPoint(v));

					double x = p.getX();
					double y = p.getY();
					if(x < selectionMinX) { selectionMinX = x; }
					if(y < selectionMinY) { selectionMinY = y; }
					if(x > selectionMaxX) { selectionMaxX = x; }
					if(y > selectionMaxY) { selectionMaxY = y; }
				}
			}

			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_LEFT_ARROW, selectionMinX + widthDiv2 - 16, selectionMinY + heightDiv2 - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_RIGHT_ARROW, selectionMaxX + widthDiv2, selectionMinY + heightDiv2 - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_LEFT_ARROW, selectionMinX + widthDiv2 - 16, selectionMaxY + heightDiv2));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW, selectionMaxX + widthDiv2, selectionMaxY + heightDiv2));
		}

		return pictograms;
	}

	@Override
	protected void pictogramAction(int action, int dx, int dy, EditorPoint started, EditorPoint ended) {
		System.out.println("ACTION: " + action + " dx: " + dx + " dy: " + dy + " Start: " +started + " End: " + ended);

		FreeTriangleMeshModel model = getModel();
		switch(action) {
			case FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW: {
				EditorPoint fixed = lenses.fromScreenToModel(new EditorPoint(selectionMinX, selectionMinY, 0.0, 1.0));
				model.elasticMove(fixed, started, ended);
			} break;
			case FreeTriangleMeshPictogram.UP_LEFT_ARROW: {
				EditorPoint fixed = lenses.fromScreenToModel(new EditorPoint(selectionMaxX, selectionMaxY, 0.0, 1.0));
				model.elasticMove(fixed, started, ended);
			} break;
			case FreeTriangleMeshPictogram.UP_RIGHT_ARROW: {
				EditorPoint fixed = lenses.fromScreenToModel(new EditorPoint(selectionMinX, selectionMaxY, 0.0, 1.0));
				model.elasticMove(fixed, started, ended);
			} break;
			case FreeTriangleMeshPictogram.DOWN_LEFT_ARROW: {
				EditorPoint fixed = lenses.fromScreenToModel(new EditorPoint(selectionMaxX, selectionMinY, 0.0, 1.0));
				model.elasticMove(fixed, started, ended);
			} break;
		}
	}

	private static final long serialVersionUID = 1L;
}
