package hu.csega.editors.ftm.layer1.presentation.opengl;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshWireframe;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameCanvas;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

public class FreeTriangleMeshMouseController implements MouseListener, MouseMotionListener, MouseWheelListener{

	private boolean mouseRightPressed = false;
	private final Point mouseRightAt = new Point(0, 0);

	private GameEngineFacade facade;
	private GameCanvas gameCanvas;
	private FreeTriangleMeshWireframe wireframeCanvas;

	public FreeTriangleMeshMouseController(GameEngineFacade facade, GameCanvas gameCanvas, FreeTriangleMeshWireframe wireframeCanvas) {
		this.facade = facade;
		this.gameCanvas = gameCanvas;
		this.wireframeCanvas = wireframeCanvas;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
		int numberOfRotations = e.getWheelRotation();
		model.modifyOpenGLZoomIndex(numberOfRotations);
		gameCanvas.repaint();
		wireframeCanvas.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		modifyAlfaAndBetaIfNeeded(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		modifyAlfaAndBetaIfNeeded(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 3) {
			mouseRightPressed = true;
			mouseRightAt.x = e.getX();
			mouseRightAt.y = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 3) {
			mouseRightPressed = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private void modifyAlfaAndBetaIfNeeded(MouseEvent e) {
		if(mouseRightPressed) {
			int dx = mouseRightAt.x - e.getX();
			int dy = mouseRightAt.y - e.getY();

			FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();
			model.modifyOpenGLAlpha(dx / 100.0);
			model.modifyOpenGLBeta(dy / 100.0);

			gameCanvas.repaint();
			wireframeCanvas.repaint();
			mouseRightAt.x = e.getX();
			mouseRightAt.y = e.getY();
		}
	}
}
