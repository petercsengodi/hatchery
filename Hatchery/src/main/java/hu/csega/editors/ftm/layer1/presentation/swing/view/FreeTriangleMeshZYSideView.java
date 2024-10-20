package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshCube;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.engine.GameEngineFacade;

public class FreeTriangleMeshZYSideView extends FreeTriangleMeshSideView {

	public FreeTriangleMeshZYSideView(GameEngineFacade facade) {
		super(facade);
		this.lenses.screenZY();

		this.selectionLine.setX1(-1000.0);
		this.selectionLine.setX2(1000.0);
	}

	@Override
	public String label() {
		return "Side";
	}

	@Override
	protected void translate(double x, double y) {
		FreeTriangleMeshModel model = getModel();
		model.setCanvasZYTranslateX(x);
		model.setCanvasZYTranslateY(y);
		lenses.addTranslation(x, y, 0.0);
	}

	@Override
	protected void zoom(double delta) {
		FreeTriangleMeshModel model = getModel();
		model.setCanvasZYZoom(model.getCanvasZYZoom() + delta);
	}

	@Override
	protected void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add) {
		double vz1 = Math.min(topLeft.getZ(), bottomRight.getZ());
		double vz2 = Math.max(topLeft.getZ(), bottomRight.getZ());
		double vy1 = Math.min(topLeft.getY(), bottomRight.getY());
		double vy2 = Math.max(topLeft.getY(), bottomRight.getY());

		FreeTriangleMeshCube cube = new FreeTriangleMeshCube();
		cube.setX1(Double.NEGATIVE_INFINITY);
		cube.setX2(Double.POSITIVE_INFINITY);
		cube.setY1(vy1);
		cube.setY2(vy2);
		cube.setZ1(vz1);
		cube.setZ2(vz2);

		FreeTriangleMeshModel model = getModel();
		model.selectAll(cube, add);
	}

	@Override
	protected void selectFirst(EditorPoint p, double radius, boolean add) {
		selectionLine.setZ1(p.getZ());
		selectionLine.setZ2(p.getZ());
		selectionLine.setY1(p.getY());
		selectionLine.setY2(p.getY());

		FreeTriangleMeshModel model = getModel();
		model.selectFirst(intersection, selectionLine, radius, add);
	}

	private static final long serialVersionUID = 1L;
}