package hu.csega.editors.anm.layer1Views.swing.views;

import hu.csega.editors.anm.components.ComponentAnimationXYSideView;
import hu.csega.editors.anm.components.ComponentAnimationXZSideView;
import hu.csega.editors.anm.components.ComponentAnimationZYSideView;
import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrame;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrameLine;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFramePoint;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshPictogram;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshLine;
import hu.csega.editors.ftm.util.FreeTriangleMeshSphereLineIntersection;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.units.Dependency;
import hu.csega.games.units.UnitStore;

import java.awt.*;
import java.util.Collection;
import java.util.Set;

public class AnimatorAnimationView extends AnimatorView implements ComponentAnimationXYSideView, ComponentAnimationXZSideView, ComponentAnimationZYSideView {

	private final int indexOfX, indexOfY;
	private ComponentWireFrameConverter wireFrameConverter;
	private AnimatorWireFrame wireFrame;

	public AnimatorAnimationView(GameEngineFacade facade, AnimatorViewCanvas canvas, int indexOfX, int indexOfY) {
		super(facade, canvas);
		this.indexOfX = indexOfX;
		this.indexOfY = indexOfY;
	}

	protected FreeTriangleMeshLine selectionLine = new FreeTriangleMeshLine();
	protected FreeTriangleMeshSphereLineIntersection intersection = new FreeTriangleMeshSphereLineIntersection();

	@Override
	protected void paintView(Graphics2D g, int width, int height) {
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, width, height);

		if(wireFrameConverter == null) {
			wireFrameConverter = UnitStore.instance(ComponentWireFrameConverter.class);
			wireFrameConverter.addDependent(this);
		}

		if(wireFrame == null && wireFrameConverter != null) {
			wireFrame = wireFrameConverter.getWireFrame();
		}

		if(wireFrame != null) {
			g.translate(width / 2, height / 2);

			Collection<AnimatorWireFrameLine> lines = wireFrame.getLines();
			if(lines != null) {
				for(AnimatorWireFrameLine line : lines) {
					AnimatorWireFramePoint source = line.getSource();
					int x1 = (int)source.valueOfIndex(indexOfX);
					int y1 = (int)source.valueOfIndex(indexOfY);

					AnimatorWireFramePoint destination = line.getDestination();
					int x2 = (int)destination.valueOfIndex(indexOfX);
					int y2 = (int)destination.valueOfIndex(indexOfY);

					g.setColor(line.getColor());
					g.drawLine(x1, y1, x2, y2);

					g.setColor(source.getColor());
					g.drawRect(x1 - 2, y1 - 2, 5, 5);

					g.setColor(destination.getColor());
					g.drawRect(x2 - 2, y2 - 2, 5, 5);
				}
			}

			Collection<AnimatorWireFramePoint> points = wireFrame.getPoints();
			if(points != null) {
				for(AnimatorWireFramePoint point : points) {
					int x = (int) point.valueOfIndex(indexOfX);
					int y = (int) point.valueOfIndex(indexOfY);

					g.setColor(point.getColor());
					if(point.isCross()) {
						g.drawLine(x-10, y-10, x+10, y+10);
						g.drawLine(x-10, y+10, x+10, y-10);
					} else {
						g.drawOval(x-5, y-5, 11, 11);
					}
				}
			}

			g.translate(-width / 2, -height / 2);
		}
	}

	@Override
	public String label() {
		return "Wireframe";
	}

	@Override
	protected EditorPoint transformToScreen(EditorPoint p) {
		int x = (int) p.valueOfIndex(indexOfX);
		int y = (int) p.valueOfIndex(indexOfY);
		return new EditorPoint(x, y, 0, 1);
	}

	@Override
	protected EditorPoint transformToModel(double x, double y) {
		EditorPoint result = new EditorPoint(0, 0, 0, 1);
		result.valueOfIndex(indexOfX, x);
		result.valueOfIndex(indexOfY, y);
		return result;
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

	@Override
	protected void moveSelected(EditorPoint p1, EditorPoint p2) {
	}

	@Override
	protected void generatePictograms(int numberOfSelectedItems, int selectionMinX, int selectionMinY, int selectionMaxX, int selectionMaxY, Set<FreeTriangleMeshPictogram> pictograms) {
	}

	@Override
	protected void pictogramAction(int action, int dx, int dy, EditorPoint started, EditorPoint ended, Rectangle selection) {
	}

	@Override
	public synchronized void invalidate() {
		wireFrame = null;
		canvas.invalidate();
		super.invalidate();
	}
}
