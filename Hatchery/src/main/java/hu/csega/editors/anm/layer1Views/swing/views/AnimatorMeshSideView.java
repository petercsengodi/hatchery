package hu.csega.editors.anm.layer1Views.swing.views;

import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrame;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrameLine;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFramePoint;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshPictogram;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshLine;
import hu.csega.editors.ftm.util.FreeTriangleMeshSphereLineIntersection;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.units.UnitStore;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class AnimatorMeshSideView extends AnimatorView {

	protected FreeTriangleMeshLine selectionLine = new FreeTriangleMeshLine();
	protected FreeTriangleMeshSphereLineIntersection intersection = new FreeTriangleMeshSphereLineIntersection();
	protected ComponentWireFrameConverter wireFrameConverter;
	protected AnimatorWireFrame wireFrame;

	public AnimatorMeshSideView(GameEngineFacade facade, AnimatorViewCanvas canvas) {
		super(facade, canvas);
	}

	@Override
	protected void paintView(Graphics2D g, int width, int height) {
		drawGrid(g);

		if(wireFrameConverter == null) {
			wireFrameConverter = UnitStore.instance(ComponentWireFrameConverter.class);
			wireFrameConverter.addDependent(this);
		}

		if(wireFrame == null && wireFrameConverter != null) {
			wireFrame = wireFrameConverter.getWireFrame();
		}

		if(wireFrame != null) {
			drawWireFrame(g, wireFrame, wireFrame.getCenterPartTransformation());
		}

		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		Collection<AnimatorObject> selectedObjects = model.getSelectedObjects();

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
	}

	private void drawWireFrame(Graphics2D g, AnimatorWireFrame wireFrame, GameTransformation transformation) {
		g.setColor(Color.blue);

		AnimatorWireFramePoint source = new AnimatorWireFramePoint();
		AnimatorWireFramePoint destination = new AnimatorWireFramePoint();

		Collection<AnimatorWireFrameLine> lines = wireFrame.getLines();
		if(lines != null) {
			for(AnimatorWireFrameLine line : lines) {
				source.copyFrom(line.getSource());
				source.transform(transformation);
				EditorPoint sourcePoint = transformToScreen(transformAnimatorWireFramePointToPoint(source));

				destination.copyFrom(line.getDestination());
				destination.transform(transformation);
				EditorPoint destinationPoint = transformToScreen(transformAnimatorWireFramePointToPoint(destination));

				drawLine(g, sourcePoint, destinationPoint);
			}
		}
	}

	protected void createVertexAt(EditorPoint p) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		model.createVertexAt(p.getX(), p.getY(), p.getZ());
	}

	@Override
	protected void moveSelected(EditorPoint p1, EditorPoint p2) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		double dz = p2.getZ() - p1.getZ();

		model.moveSelected(dx, dy, dz);
		canvas.somethingChanged();
	}

	protected EditorPoint transformVertexToPoint(FreeTriangleMeshVertex vertex) {
		return new EditorPoint(vertex.getPX(), vertex.getPY(), vertex.getPZ(), 1.0);
	}

	protected EditorPoint transformAnimatorWireFramePointToPoint(AnimatorWireFramePoint vertex) {
		return new EditorPoint(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0);
	}

	protected void drawLine(Graphics2D g, EditorPoint end1, EditorPoint end2) {
		g.drawLine((int)end1.getX(), (int)end1.getY(), (int)end2.getX(), (int)end2.getY());
	}

	protected void drawRectangle(Graphics2D g, EditorPoint end1, EditorPoint end2) {
		g.drawRect((int)end1.getX(), (int)end1.getY(), (int)(end2.getX() - end1.getX()), (int)(end2.getY() - end1.getY()));
	}

	protected abstract void drawGrid(Graphics2D g);

	@Override
	protected void generatePictograms(int numberOfSelectedItems, int selectionMinX, int selectionMinY, int selectionMaxX, int selectionMaxY, Set<FreeTriangleMeshPictogram> pictograms) {
		if(numberOfSelectedItems > 1) {
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_LEFT_ARROW, selectionMinX - 16, selectionMinY - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.UP_RIGHT_ARROW, selectionMaxX, selectionMinY - 16));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_LEFT_ARROW, selectionMinX - 16, selectionMaxY));
			pictograms.add(new FreeTriangleMeshPictogram(FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW, selectionMaxX, selectionMaxY));
		}
	}

	@Override
	protected void pictogramAction(int action, int dx, int dy, EditorPoint started, EditorPoint ended, Rectangle selection) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		switch(action) {
			case FreeTriangleMeshPictogram.DOWN_RIGHT_ARROW: {
				EditorPoint fixed = transformToModel(selection.getX(), selection.getY());
				if(fixed != null) {
					model.elasticMove(fixed, started, ended);
				}
			} break;
			case FreeTriangleMeshPictogram.UP_LEFT_ARROW: {
				EditorPoint fixed = transformToModel(selection.getX() + selection.getWidth(), selection.getY() + selection.getHeight());
				if(fixed != null) {
					model.elasticMove(fixed, started, ended);
				}
			} break;
			case FreeTriangleMeshPictogram.UP_RIGHT_ARROW: {
				EditorPoint fixed = transformToModel(selection.getX(), selection.getY() + selection.getHeight());
				if(fixed != null) {
					model.elasticMove(fixed, started, ended);
				}
			} break;
			case FreeTriangleMeshPictogram.DOWN_LEFT_ARROW: {
				EditorPoint fixed = transformToModel(selection.getX() + selection.getWidth(), selection.getY());
				if(fixed != null) {
					model.elasticMove(fixed, started, ended);
				}
			} break;
		}
	}

	@Override
	public void invalidate() {
		wireFrame = null;
		canvas.invalidate();
		super.invalidate();
	}
}
