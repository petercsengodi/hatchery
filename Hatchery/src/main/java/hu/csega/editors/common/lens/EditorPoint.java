package hu.csega.editors.common.lens;

public class EditorPoint {

	private double x;
	private double y;
	private double z;
	private double w;

	public EditorPoint() {
	}

	public EditorPoint(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public EditorPoint(EditorPoint other) {
		copyValuesFrom(other);
	}

	public void copyValuesFrom(EditorPoint other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.w = other.w;
	}

	public void addValuesFrom(EditorPoint other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
		this.w += other.w;
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

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public double valueOfIndex(int index) {
		switch (index) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
			default:
				return w;
		}
	}

	@Override
	public String toString() {
		return '(' + String.valueOf(x) + ';' + String.valueOf(y) + ';' + String.valueOf(z) + ';' + String.valueOf(w) + ')';
	}
}
