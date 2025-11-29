package hu.csega.editors.anm.layer1Views.swing.components.rotator;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class AnimatorRotatorCanvas extends JPanel {

	private double width;
	private double height;

	private int centerX;
	private int centerY;
	private double radius;

	private final AnimatorRotatorComponent parent;

	AnimatorRotatorCanvas(AnimatorRotatorComponent parent) {
		this.parent = parent;
	}

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

		AnimatorRotatorBinding binding = parent.getBinding();
		if(binding != null) {
			double x = binding.currentXRotation() * RAD;
			double y = binding.currentYRotation() * RAD;
			double z = binding.currentZRotation() * RAD;

			double x0 = 0;
			double y0 = 0;
			double z0 = -radius;

			double x1 = x0;
			double y1 = y0 * Math.cos(x) + z0 * Math.sin(x);
			double z1 = -y0 * Math.sin(x) + z0 * Math.cos(x);

			double x2 = x1 * Math.cos(y) + z1 * Math.sin(y);
			double y2 = y1;
			double z2 = -x1 * Math.sin(y) + z1 * Math.cos(y);

			double x3 = x2 * Math.cos(z) + y2 * Math.sin(z);
			double y3 = -x2 * Math.sin(z) + y2 * Math.cos(z);
			double z3 = z2;

			g.setColor(Color.BLUE);
			g.drawLine((int)x3, (int)y3, 0, 0);

			g.setColor(Color.GREEN);
			g.drawOval((int) (x3 - MARKER_RADIUS), ((int) (y3 - MARKER_RADIUS)), MARKER_DIAMETER, MARKER_DIAMETER);
		}

		g.translate(-centerX, -centerY);
	}

	private static final double RAD = Math.PI / 180.0;

	static final int CANVAS_MIN_SIZE = 80;

	static final int MARKER_RADIUS = 3;
	static final int MARKER_DIAMETER = 2 * MARKER_RADIUS + 1;

	private static final long serialVersionUID = 1L;

}
