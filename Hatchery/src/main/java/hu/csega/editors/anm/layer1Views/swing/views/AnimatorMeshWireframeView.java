package hu.csega.editors.anm.layer1Views.swing.views;

import hu.csega.editors.anm.components.ComponentMeshZYSideView;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimatorMeshWireframeView extends AnimatorMeshSideView implements ComponentMeshZYSideView {

	public AnimatorMeshWireframeView(GameEngineFacade facade, AnimatorViewCanvas canvas) {
		super(facade, canvas);
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
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		model.setCanvasZYTranslateX(x);
		model.setCanvasZYTranslateY(y);
		lenses.addTranslation(x, y, 0.0);
		canvas.somethingChanged();
	}

	@Override
	protected void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add) {
	}

	@Override
	protected void selectFirst(EditorPoint p, double radius, boolean add) {
	}

	@Override
	protected void selectFirstOnScreen(int x, int y, double radius, boolean add) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		double x1 = x - radius;
		double x2 = x + radius;
		double y1 = y - radius;
		double y2 = y + radius;

		// FIXME: There is no checking of depth (Z coordinates)
		List<FreeTriangleMeshVertex> vertices = model.getVertices();
		if(vertices != null) {
			for(FreeTriangleMeshVertex vertex : vertices) {
				if(model.enabled(vertex)) {
					EditorPoint p = transformVertexToPoint(vertex);
					EditorPoint transformed = transformToScreen(p);
					if(transformed.getX() >= x1 && transformed.getX() <= x2) {
						if(transformed.getY() >= y1 && transformed.getY() <= y2) {
							model.select(vertex, add);
							canvas.somethingChanged();
							return;
						}
					}
				}
			}
		}
	}

	@Override
	protected void selectAllOnScreen(int x1, int y1, int x2, int y2, boolean add) {
		FreeTriangleMeshModel model = getModel(FreeTriangleMeshModel.class);
		if(model == null)
			return;

		List<FreeTriangleMeshVertex> vertices = model.getVertices();
		if(vertices != null) {
			List<FreeTriangleMeshVertex> selected = new ArrayList<>();
			for(FreeTriangleMeshVertex vertex : vertices) {
				if(model.enabled(vertex)) {
					EditorPoint p = transformVertexToPoint(vertex);
					EditorPoint transformed = transformToScreen(p);
					if(transformed.getX() >= x1 && transformed.getX() <= x2) {
						if(transformed.getY() >= y1 && transformed.getY() <= y2) {
							selected.add(vertex);
						}
					}
				}
			}

			if(!selected.isEmpty()) {
				model.select(selected, add);
				canvas.somethingChanged();
			}
		}
	}

}