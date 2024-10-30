package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshCube;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.engine.GameEngineFacade;

public class FreeTriangleMeshXYSideView extends FreeTriangleMeshSideView {

	public FreeTriangleMeshXYSideView(GameEngineFacade facade) {
		super(facade);

		this.selectionLine.setZ1(-1000.0);
		this.selectionLine.setZ2(1000.0);
	}

	@Override
	public String label() {
		return "Front";
	}

	@Override
	protected void translate(double x, double y) {
		FreeTriangleMeshModel model = getModel();
		model.setCanvasXYTranslateX(x);
		model.setCanvasXYTranslateY(y);
		lenses.addTranslation(x, y, 0.0);
		somethingChanged();
	}

	@Override
	protected void zoom(double delta) {
		FreeTriangleMeshModel model = getModel();
		model.setCanvasXYZoom(model.getCanvasXYZoom() + delta);
		somethingChanged();
	}

	@Override
	protected void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add) {
		double vx1 = Math.min(topLeft.getX(), bottomRight.getX());
		double vx2 = Math.max(topLeft.getX(), bottomRight.getX());
		double vy1 = Math.min(topLeft.getY(), bottomRight.getY());
		double vy2 = Math.max(topLeft.getY(), bottomRight.getY());

		FreeTriangleMeshCube cube = new FreeTriangleMeshCube();
		cube.setX1(vx1);
		cube.setX2(vx2);
		cube.setY1(vy1);
		cube.setY2(vy2);
		cube.setZ1(Double.NEGATIVE_INFINITY);
		cube.setZ2(Double.POSITIVE_INFINITY);

		FreeTriangleMeshModel model = getModel();
		model.selectAll(cube, add);
		somethingChanged();
	}

	@Override
	protected void selectFirst(EditorPoint p, double radius, boolean add) {
		selectionLine.setX1(p.getX());
		selectionLine.setX2(p.getX());
		selectionLine.setY1(p.getY());
		selectionLine.setY2(p.getY());

		FreeTriangleMeshModel model = getModel();
		model.selectFirst(intersection, selectionLine, radius, add);
		somethingChanged();
	}

	private static final long serialVersionUID = 1L;
}