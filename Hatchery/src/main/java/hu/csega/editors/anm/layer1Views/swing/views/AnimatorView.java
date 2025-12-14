package hu.csega.editors.anm.layer1Views.swing.views;

import hu.csega.editors.anm.common.CommonComponent;
import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.common.CommonInvalidatable;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.editors.common.lens.EditorLensPipeline;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshPictogram;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public abstract class AnimatorView implements CommonComponent {

	private final Set<CommonInvalidatable> dependents = new HashSet<>();

	protected final GameEngineFacade facade;
	protected final AnimatorViewCanvas canvas;

	protected EditorLensPipeline lenses = new EditorLensPipeline();
	protected int zoomIndex = FreeTriangleMeshModel.DEFAULT_ZOOM_INDEX;

	protected AnimatorViewContextMenu contextMenu;

	public AnimatorView(GameEngineFacade facade, AnimatorViewCanvas canvas) {
		this.facade = facade;
		this.canvas = canvas;
	}

	public void paint(Graphics2D g2d, int width, int height) {
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.black);

		paintView(g2d, width, height);
	}

	public void repaintEverything() {
		facade.window().repaintEverything();
	}

	public void finalizeMove() {
		CommonEditorModel model = getModel(CommonEditorModel.class);
		if(model == null)
			return;

		model.finalizeMove();
	}

	public void showContextMenu(AnimatorViewCanvas animatorViewCanvas, int x, int y) {
		if(contextMenu != null) {
			contextMenu.setPosition(x, y);
			contextMenu.show(animatorViewCanvas, x, y);
		}
	}

	public abstract String label();

	protected abstract EditorPoint transformToScreen(EditorPoint p);

	/**
	 * @return May be null, as not all views can transform to model.
	 */
	protected abstract EditorPoint transformToModel(double x, double y);

	protected abstract void translate(double x, double y);

	protected abstract void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add);

	protected abstract void selectFirst(EditorPoint p, double radius, boolean add);

	protected abstract void moveSelected(EditorPoint p1, EditorPoint p2);

	protected abstract void pictogramAction(int action, int dx, int dy, EditorPoint started, EditorPoint ended, Rectangle selection);

	protected <T> T getModel(Class<T> returnType) {
		AnimatorModel animatorModel = (AnimatorModel) facade.model();
		CommonEditorModel commonEditorModel = animatorModel.selectModel();
		if(commonEditorModel != null && returnType.isAssignableFrom(commonEditorModel.getClass()))
			return (T) commonEditorModel;

		return null;
	}

	public void controlPlusLeftClick(int x, int y) {
		/* EditorPoint p = transformToModel(x, y);
		if(p != null) {
			createVertexAt(p);
			repaintEverything();
		} */
	}

	protected abstract void paintView(Graphics2D g, int width, int height);

	protected abstract void generatePictograms(int numberOfSelectedItems, int selectionMinX, int selectionMinY, int selectionMaxX, int selectionMaxY, Set<FreeTriangleMeshPictogram> pictograms);

	@Override
	public synchronized void invalidate() {
		for(CommonInvalidatable dependent : dependents) {
			dependent.invalidate();
		}
	}

	@Override
	public void addDependent(CommonInvalidatable dependent) {
		this.dependents.add(dependent);
	}
}
