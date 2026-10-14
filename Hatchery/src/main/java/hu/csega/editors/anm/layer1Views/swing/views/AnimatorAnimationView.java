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
import hu.csega.games.units.UnitStore;

import java.awt.*;
import java.util.Collection;
import java.util.Set;

public class AnimatorAnimationView extends AnimatorView implements ComponentAnimationXYSideView, ComponentAnimationXZSideView, ComponentAnimationZYSideView {

	private final int indexOfX, indexOfY;
	private ComponentWireFrameConverter wireFrameConverter;
	private AnimatorWireFrame wireFrame;

	private double translationX, translationY;

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
					EditorPoint p1 = transformToScreen(new EditorPoint(source.getX(), source.getY(), source.getZ(), 1.0));
					int x1 = (int) p1.getX();
					int y1 = (int) p1.getY();

					AnimatorWireFramePoint destination = line.getDestination();
					EditorPoint p2 = transformToScreen(new EditorPoint(destination.getX(), destination.getY(), destination.getZ(), 1.0));
					int x2 = (int) p2.getX();
					int y2 = (int) p2.getY();

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
					EditorPoint p = transformToScreen(new EditorPoint(point.getX(), point.getY(), point.getZ(), 1.0));
					int x = (int) p.getX();
					int y = (int) p.getY();

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
		int x = (int) (p.valueOfIndex(indexOfX) + translationX);
		int y = (int) (p.valueOfIndex(indexOfY) + translationY);
		return new EditorPoint(x, y, 0, 1);
	}

	@Override
	protected EditorPoint transformToModel(double x, double y) {
		EditorPoint result = new EditorPoint(0, 0, 0, 1);
		result.valueOfIndex(indexOfX, x - translationX);
		result.valueOfIndex(indexOfY, y - translationY);
		return result;
	}

	@Override
	protected void translate(double x, double y) {
		translationX += x;
		translationY += y;
	}

	@Override
	protected void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add) {
	}

	@Override
	protected void selectFirst(EditorPoint p, double radius, boolean add) {
	}

	@Override
	protected void selectFirstOnScreen(int x, int y, double radius, boolean add) {
	}

	@Override
	protected void selectAllOnScreen(int x1, int y1, int x2, int y2, boolean add) {
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
		canvas.repaint();
		super.invalidate();
	}
}
