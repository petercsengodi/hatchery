package hu.csega.editors.anm.layer1.swing.components.rotator;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class AnimatorRotatorCanvas extends JPanel {

	static final int CANVAS_MIN_SIZE = 80;

	static final int MARKER_RADIUS = 3;
	static final int MARKER_DIAMETER = 2 * MARKER_RADIUS + 1;

	private double width;
	private double height;

	private int centerX;
	private int centerY;
	private double radius;

	@Override
	public void paint(Graphics g) {
		width = this.getWidth();
		height = this.getHeight();

		// Background
		g.setColor(Color.RED);
		g.fillRect(0, 0, (int)width, (int)height);

		// Graphic based on rotation values
		radius = Math.min(width, height) / 2.0 - MARKER_RADIUS - 2.0;
		centerX = (int)(width / 2.0);
		centerY = (int)(height / 2.0);

		g.translate(centerX, centerY);
		g.setColor(Color.BLACK);
		g.drawOval(-MARKER_RADIUS, -MARKER_RADIUS, MARKER_DIAMETER, MARKER_DIAMETER);
	}

	private static final long serialVersionUID = 1L;

}
