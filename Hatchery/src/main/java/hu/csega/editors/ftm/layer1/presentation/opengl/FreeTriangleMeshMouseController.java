package hu.csega.editors.ftm.layer1.presentation.opengl;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import hu.csega.editors.common.lens.EditorLensPipeline;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshHoverOverCalculations;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshWireframe;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameCanvas;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshTriangle;

public class FreeTriangleMeshMouseController implements MouseListener, MouseMotionListener, MouseWheelListener{

	private static final Boolean COUNTER_CLOCKWISE = true;

	private boolean mouseRightPressed = false;
	private final Point mouseRightAt = new Point(0, 0);
	private final Point trackedMousePosition = new Point(0, 0);

	private final GameEngineFacade facade;
	private final GameCanvas gameCanvas;
	private final FreeTriangleMeshWireframe wireframeCanvas;
	private final EditorLensPipeline lenses = new EditorLensPipeline();
	private final FreeTriangleMeshHoverOverCalculations hoverOverCalculations = new FreeTriangleMeshHoverOverCalculations(lenses);

	public FreeTriangleMeshMouseController(GameEngineFacade facade, GameCanvas gameCanvas, FreeTriangleMeshWireframe wireframeCanvas) {
		this.facade = facade;
		this.gameCanvas = gameCanvas;
		this.wireframeCanvas = wireframeCanvas;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		int numberOfRotations = e.getWheelRotation();
		model.modifyOpenGLZoomIndex(numberOfRotations);

		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), gameCanvas.getWidth(), gameCanvas.getHeight(), COUNTER_CLOCKWISE);

		repaintBothCanvases();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		modifyAlfaAndBetaIfNeeded(e);

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), gameCanvas.getWidth(), gameCanvas.getHeight(), COUNTER_CLOCKWISE);

		repaintBothCanvases();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		modifyAlfaAndBetaIfNeeded(e);

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), gameCanvas.getWidth(), gameCanvas.getHeight(), COUNTER_CLOCKWISE);

		repaintBothCanvases();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), gameCanvas.getWidth(), gameCanvas.getHeight(), COUNTER_CLOCKWISE);

		boolean repaintRequestedAlready = false;

		if(e.getButton() == MouseEvent.BUTTON1) {
			Object hoverOverObject = model.getHoverOverObject();
			if(hoverOverObject instanceof FreeTriangleMeshTriangle) {
				model.selectTriangle((FreeTriangleMeshTriangle) hoverOverObject);
				repaintRequestedAlready = true;
				facade.window().repaintEverything();
			}
		}

		if(!repaintRequestedAlready)
			repaintBothCanvases();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		if(e.getButton() == MouseEvent.BUTTON3) {
			mouseRightPressed = true;
			mouseRightAt.x = e.getX();
			mouseRightAt.y = e.getY();
		}

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), gameCanvas.getWidth(), gameCanvas.getHeight(), COUNTER_CLOCKWISE);

		repaintBothCanvases();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		if(e.getButton() == 3) {
			mouseRightPressed = false;
		}

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), gameCanvas.getWidth(), gameCanvas.getHeight(), COUNTER_CLOCKWISE);

		repaintBothCanvases();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), gameCanvas.getWidth(), gameCanvas.getHeight(), COUNTER_CLOCKWISE);

		repaintBothCanvases();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		trackedMousePosition.x = e.getX();
		trackedMousePosition.y = e.getY();

		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		hoverOverCalculations.doCalculations(model, e.getX(), e.getY(), gameCanvas.getWidth(), gameCanvas.getHeight(), COUNTER_CLOCKWISE);

		repaintBothCanvases();
	}

	private void modifyAlfaAndBetaIfNeeded(MouseEvent e) {
		if(mouseRightPressed) {
			int dx = mouseRightAt.x - e.getX();
			int dy = mouseRightAt.y - e.getY();

			FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
			model.modifyOpenGLAlpha(dx / 100.0);
			model.modifyOpenGLBeta(dy / 100.0);
			mouseRightAt.x = e.getX();
			mouseRightAt.y = e.getY();
		}
	}

	private void repaintBothCanvases() {
		gameCanvas.repaint();
		wireframeCanvas.invalidate();
		wireframeCanvas.repaint();
	}
}
