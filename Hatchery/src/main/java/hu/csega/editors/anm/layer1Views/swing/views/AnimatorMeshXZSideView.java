package hu.csega.editors.anm.layer1Views.swing.views;

import hu.csega.editors.anm.components.ComponentMeshXZSideView;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer4.data.FreeTriangleMeshCube;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

import java.awt.*;

public class AnimatorMeshXZSideView extends AnimatorMeshSideView implements ComponentMeshXZSideView {

	public AnimatorMeshXZSideView(GameEngineFacade facade, AnimatorViewCanvas canvas) {
		super(facade, canvas);
		this.lenses.screenXZ();

		this.selectionLine.setY1(-1000.0);
		this.selectionLine.setY2(1000.0);
	}

	@Override
	public String label() {
		return "Top";
	}

	@Override
	protected EditorPoint transformToScreen(EditorPoint p) {
		EditorPoint result = lenses.fromModelToScreen(p.getX(), p.getY(), p.getZ());
		double x = canvas.lastSize.width / 2.0 + result.getX();
		double y = canvas.lastSize.height / 2.0 + result.getY();
		double z = result.getZ();

		result.setX(x);
		result.setY(y);
		result.setZ(z);
		return result;
	}

	@Override
	protected EditorPoint transformToModel(double x, double y) {
		EditorPoint result = new EditorPoint(0, 0, 0, 1);
		result.setX(x - canvas.lastSize.width / 2.0);
		result.setY(y - canvas.lastSize.height / 2.0);
		return lenses.fromScreenToModel(result);
	}

	@Override
	protected void drawGrid(Graphics2D g) {
		g.setColor(Color.WHITE);

		for (int x = -400; x <= 400; x += 20) {
			EditorPoint p1 = transformToScreen(new EditorPoint(x, 0.0, -400.0, 1.0));
			EditorPoint p2 = transformToScreen(new EditorPoint(x, 0.0, 400.0, 1.0));
			drawLine(g, p1, p2);
		}

		for (int z = -400; z <= 400; z += 20) {
			EditorPoint p1 = transformToScreen(new EditorPoint(-400.0, 0.0, z, 1.0));
			EditorPoint p2 = transformToScreen(new EditorPoint(400.0, 0.0, z, 1.0));
			drawLine(g, p1, p2);
		}
	}

	@Override
	protected void translate(double x, double y) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if (model == null)
			return;

		model.setCanvasXZTranslateX(x);
		model.setCanvasXZTranslateY(y);
		lenses.addTranslation(x, y, 0.0);
		canvas.somethingChanged();
	}

	@Override
	protected void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if (model == null)
			return;

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

		model.selectAll(cube, add);
		canvas.somethingChanged();
	}

	@Override
	protected void selectFirst(EditorPoint p, double radius, boolean add) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if (model == null)
			return;

		selectionLine.setX1(p.getX());
		selectionLine.setX2(p.getX());
		selectionLine.setZ1(p.getZ());
		selectionLine.setZ2(p.getZ());

		model.selectFirst(intersection, selectionLine, radius, add);
		canvas.somethingChanged();
	}

}