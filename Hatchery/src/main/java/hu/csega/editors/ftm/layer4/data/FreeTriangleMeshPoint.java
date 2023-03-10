package hu.csega.editors.ftm.layer4.data;

import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;

public class FreeTriangleMeshPoint {

	private double x;
	private double y;
	private double z;

	public FreeTriangleMeshPoint() {
	}

	public FreeTriangleMeshPoint(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public FreeTriangleMeshVertex toIncompleteVertex() {
		FreeTriangleMeshVertex ret = new FreeTriangleMeshVertex(x, y, z);
		return ret;
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

}
