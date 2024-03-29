package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshCube;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.engine.GameEngineFacade;

public class FreeTriangleMeshXZSideView extends FreeTriangleMeshSideView {

	public FreeTriangleMeshXZSideView(GameEngineFacade facade) {
		super(facade);

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
	protected void selectAll(double x1, double y1, double x2, double y2, boolean add) {
		double vx1 = Math.min(x1, x2);
		double vx2 = Math.max(x1, x2);
		double vz1 = Math.min(y1, y2);
		double vz2 = Math.max(y1, y2);

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
	protected void selectFirst(double x, double y, double radius, boolean add) {
		selectionLine.setX1(x);
		selectionLine.setX2(x);
		selectionLine.setZ1(y);
		selectionLine.setZ2(y);

		FreeTriangleMeshModel model = getModel();
		model.selectFirst(intersection, selectionLine, radius, add);
	}

	@Override
	protected void createVertexAt(double x, double y) {
		FreeTriangleMeshModel model = getModel();
		model.createVertexAt(x, 0, y);
	}

	@Override
	protected void moveSelected(double x1, double y1, double x2, double y2) {
		FreeTriangleMeshModel model = getModel();

		EditorPoint p1 = lenses.fromScreenToModel(new EditorPoint(x1, 0, y1, 1));
		EditorPoint p2 = lenses.fromScreenToModel(new EditorPoint(x2, 0, y2, 1));

		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		double dz = p1.getZ() - p2.getZ();

		model.moveSelected(dx, dy, dz);
	}

	@Override
	protected EditorPoint transformVertexToPoint(FreeTriangleMeshVertex vertex) {
		EditorPoint ret = new EditorPoint();

		ret.setX(vertex.getPX());
		ret.setY(vertex.getPZ());

		return ret;
	}

	private static final long serialVersionUID = 1L;
}