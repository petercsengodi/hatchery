package hu.csega.editors.anm.layer1Views.swing.wireframe;

import hu.csega.games.engine.g3d.GameTransformation;

import java.awt.*;

public class AnimatorWireFramePoint {

	private double x;
	private double y;
	private double z;
	private Color color;
	private boolean cross;

	public AnimatorWireFramePoint() {
	}

	public AnimatorWireFramePoint(double x, double y, double z, Color color, boolean cross) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
		this.cross = cross;
	}

	public void copyFrom(AnimatorWireFramePoint other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.color = other.color;
		this.cross = other.cross;
	}

	public double valueOfIndex(int index) {
		switch (index) {
			case 0:
				return x;
			case 1:
				return -y;
			case 2:
				return z;
			case 3:
				return 1.0;
			default:
				return 0;
		}
	}

	public void transform(GameTransformation transformation) {
		float[] m = transformation.getFloats();

		double newx = x * m[0] + y * m[4] + z * m[8] + m[12];
		double newy = x * m[1] + y * m[5] + z * m[9] + m[13];
		double newz = x * m[2] + y * m[6] + z * m[10] + m[14];
		double neww = x * m[3] + y * m[7] + z * m[11] + m[15];

		this.x = newx / neww;
		this.y = newy / neww;
		this.z = newz / neww;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isCross() {
		return cross;
	}

	public void setCross(boolean cross) {
		this.cross = cross;
	}

}
