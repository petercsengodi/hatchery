package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshCube;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.engine.GameEngineFacade;

public class FreeTriangleMeshXZSideView extends FreeTriangleMeshSideView {

	public FreeTriangleMeshXZSideView(GameEngineFacade facade) {
		super(facade);
		this.lenses.screenXZ();

		this.selectionLine.setY1(-1000.0);
		this.selectionLine.setY2(1000.0);
	}

	@Override
	public String label() {
		return "Top";
	}

	@Override
	protected void translate(double x, double y) {
		FreeTriangleMeshModel model = getModel();
		model.setCanvasXZTranslateX(x);
		model.setCanvasXZTranslateY(y);
		lenses.addTranslation(x, y, 0.0);
	}

	@Override
	protected void zoom(double delta) {
		FreeTriangleMeshModel model = getModel();
		model.setCanvasXZZoom(model.getCanvasXZZoom() + delta);
	}

	@Override
	protected void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add) {
		double vx1 = Math.min(topLeft.getX(), bottomRight.getX());
		double vx2 = Math.max(topLeft.getX(), bottomRight.getX());
		double vz1 = Math.min(topLeft.getZ(), bottomRight.getZ());
		double vz2 = Math.max(topLeft.getZ(), bottomRight.getZ());

		FreeTriangleMeshCube cube = new FreeTriangleMeshCube();
		cube.setX1(vx1);
		cube.setX2(vx2);
		cube.setY1(Double.NEGATIVE_INFINITY);
		cube.setY2(Double.POSITIVE_INFINITY);
		cube.setZ1(vz1);
		cube.setZ2(vz2);

		FreeTriangleMeshModel model = getModel();
		model.selectAll(cube, add);
	}

	@Override
	protected void selectFirst(EditorPoint p, double radius, boolean add) {
		selectionLine.setX1(p.getX());
		selectionLine.setX2(p.getX());
		selectionLine.setZ1(p.getZ());
		selectionLine.setZ2(p.getZ());

		FreeTriangleMeshModel model = getModel();
		model.selectFirst(intersection, selectionLine, radius, add);
	}

	private static final long serialVersionUID = 1L;
}