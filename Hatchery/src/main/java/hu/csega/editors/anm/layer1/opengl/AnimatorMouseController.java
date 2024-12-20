package hu.csega.editors.anm.layer1.opengl;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameCanvas;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class AnimatorMouseController implements MouseListener, MouseMotionListener, MouseWheelListener{

	public static final double[] ZOOM_VALUES = { 0.0001, 0.0002, 0.0005, 0.001, 0.002, 0.005, 0.01, 0.02, 0.05,
			0.1, 0.2, 0.3, 0.5, 0.75, 1.0, 1.25, 1.50, 2.0, 3.0, 4.0, 5.0,
			10.0, 15.0, 20.0, 30.0, 50.0, 80.0, 100.0 };
	public static final int DEFAULT_ZOOM_INDEX = 8;

	private int zoomIndex = DEFAULT_ZOOM_INDEX;

	private double alfa;
	private double beta;

	private boolean mouseRightPressed = false;
	private Point mouseRightAt = new Point(0, 0);

	private GameCanvas canvas;
	private GameEngineFacade facade;
	private AnimatorModel model;

	public double getAlfa() {
		return alfa;
	}

	public double getBeta() {
		return beta;
	}

	public AnimatorMouseController(GameCanvas canvas, GameEngineFacade facade) {
		this.canvas = canvas;
		this.facade = facade;
	}

	public double getScaling() {
		return ZOOM_VALUES[zoomIndex];
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int numberOfRotations = e.getWheelRotation();
		zoomIndex += numberOfRotations;
		if(zoomIndex < 0)
			zoomIndex = 0;
		else if(zoomIndex >= ZOOM_VALUES.length)
			zoomIndex = ZOOM_VALUES.length - 1;

		refresh();
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
			mouseRightAt = new Point(e.getX(), e.getY());
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

			alfa += dx / 100.0;
			if(alfa < -PI2)
				alfa += PI2;
			else if(alfa > PI2)
				alfa -= PI2;

			beta += dy / 100.0;
			if(beta < -BETA_LIMIT)
				beta = -BETA_LIMIT;
			else if(beta > BETA_LIMIT)
				beta = BETA_LIMIT;

			refresh();
			mouseRightAt.x = e.getX();
			mouseRightAt.y = e.getY();
		}
	}

	private void refresh() {
		if(model == null) {
			model = (AnimatorModel) facade.model();
			if(model == null) {
				return;
			}
		}

		model.refreshCamera(this);
		canvas.repaint();
	}

	private static final double PI2 = 2*Math.PI;
	private static final double BETA_LIMIT = Math.PI / 2;
}
