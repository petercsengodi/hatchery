package hu.csega.editors.ftm.layer1.presentation.swing.view;

import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshCube;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import hu.csega.games.engine.GameEngineFacade;

import java.awt.*;

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
	protected EditorPoint transformToScreen(EditorPoint p) {
		EditorPoint result = lenses.fromModelToScreen(p.getX(), p.getY(), p.getZ());
		double x = lastSize.width / 2.0 + result.getX();
		double y = lastSize.height / 2.0 + result.getY();
		double z = result.getZ();

		result.setX(x);
		result.setY(y);
		result.setZ(z);
		return result;
	}

	@Override
	protected EditorPoint transformToModel(int x, int y) {
		if(x == Integer.MIN_VALUE || x == Integer.MAX_VALUE || y == Integer.MIN_VALUE || y == Integer.MAX_VALUE) {
			return null;
		}

		EditorPoint result = new EditorPoint(0, 0, 0, 1);
		result.setX(x - lastSize.width / 2.0);
		result.setY(y - lastSize.height / 2.0);
		return lenses.fromScreenToModel(result);
	}

	@Override
	protected void drawGrid(Graphics2D g) {
		g.setColor(Color.WHITE);

		for(int y = -400; y <= 400; y += 20) {
			EditorPoint p1 = transformToScreen(new EditorPoint(0.0, y, -400.0, 1.0));
			EditorPoint p2 = transformToScreen(new EditorPoint(0.0, y, 400.0, 1.0));
			drawLine(g, p1, p2);
		}

		for(int z = -400; z <= 400; z += 20) {
			EditorPoint p1 = transformToScreen(new EditorPoint(0.0, -400.0, z, 1.0));
			EditorPoint p2 = transformToScreen(new EditorPoint(0.0, 400.0, z, 1.0));
			drawLine(g, p1, p2);
		}
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