package hu.csega.editors.common.lens;

import org.joml.Matrix4d;
import org.joml.Vector4d;

// Not thread-safe.
public class EditorTransformation implements EditorLens {

	public Matrix4d transformation;
	public Matrix4d inverse;

	private Vector4d v = new Vector4d();

	public EditorTransformation() {
		setTransformation(new Matrix4d(
				1.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0,
				0.0, 0.0, 0.0, 1.0));
	}

	public EditorTransformation(Matrix4d transformationMatrix) {
		setTransformation(transformationMatrix);
	}

	public void setTransformation(Matrix4d transformationMatrix) {
		this.transformation = new Matrix4d(transformationMatrix);
		this.inverse = new Matrix4d(this.transformation);
		this.inverse.invert();
	}

	@Override
	public void fromModelToScreen(EditorPoint original) {
		v.x = original.getX();
		v.y = original.getY();
		v.z = original.getZ();
		v.w = original.getW();

		v = transformation.transform(v);

		original.setX(v.x);
		original.setY(v.y);
		original.setZ(v.z);
		original.setW(v.w);
	}

	@Override
	public void fromScreenToModel(EditorPoint original) {
		v.x = original.getX();
		v.y = original.getY();
		v.z = original.getZ();
		v.w = original.getW();

		v = inverse.transform(v);

		original.setX(v.x);
		original.setY(v.y);
		original.setZ(v.z);
		original.setW(v.w);
	}

}
