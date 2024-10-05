package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshCube;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.engine.GameEngineFacade;

public class FreeTriangleMeshZYSideView extends FreeTriangleMeshSideView {

	public FreeTriangleMeshZYSideView(GameEngineFacade facade) {
		super(facade);

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
	protected void selectAll(double x1, double y1, double x2, double y2, boolean add) {
		double vz1 = Math.min(x1, x2);
		double vz2 = Math.max(x1, x2);
		double vy1 = Math.min(y1, y2);
		double vy2 = Math.max(y1, y2);

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
	protected void selectFirst(double x, double y, double radius, boolean add) {
		selectionLine.setZ1(x);
		selectionLine.setZ2(x);
		selectionLine.setY1(y);
		selectionLine.setY2(y);

		FreeTriangleMeshModel model = getModel();
		model.selectFirst(intersection, selectionLine, radius, add);
	}

	@Override
	protected void createVertexAt(double x, double y) {
		FreeTriangleMeshModel model = getModel();
		model.createVertexAt(0, y, x);
	}

	@Override
	protected void moveSelected(double x1, double y1, double x2, double y2) {
		FreeTriangleMeshModel model = getModel();

		EditorPoint p1 = lenses.fromScreenToModel(new EditorPoint(0, y1, x1, 1));
		EditorPoint p2 = lenses.fromScreenToModel(new EditorPoint(0, y2, x2, 1));

		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		double dz = p2.getZ() - p1.getZ();

		model.moveSelected(dx, dy, dz);
	}

	@Override
	protected EditorPoint transformVertexToPoint(FreeTriangleMeshVertex vertex) {
		EditorPoint ret = new EditorPoint();

		ret.setX(vertex.getPZ());
		ret.setY(vertex.getPY());

		return ret;
	}

	@Override
	protected void pictogramAction(int action, int dx, int dy) {
		System.out.println("ACTION: " + action);
	}

	private static final long serialVersionUID = 1L;
}