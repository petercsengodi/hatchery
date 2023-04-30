package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;

import org.joml.Matrix4f;

public class AnimationDetailedTransformation implements Serializable {

	private AnimationVector flip = new AnimationVector(1f, 1f, 1f);
	private AnimationVector scaling = new AnimationVector(1f, 1f, 1f);
	private AnimationVector rotation = new AnimationVector(0f, 0f, 0f);
	private AnimationVector translation = new AnimationVector(0f, 0f, 0f);

	public AnimationVector getFlip() {
		return flip;
	}

	public void setFlip(AnimationVector flip) {
		this.flip = flip;
	}

	public AnimationVector getScaling() {
		return scaling;
	}

	public void setScaling(AnimationVector scaling) {
		this.scaling = scaling;
	}

	public AnimationVector getRotation() {
		return rotation;
	}

	public void setRotation(AnimationVector rotation) {
		this.rotation = rotation;
	}

	public AnimationVector getTranslation() {
		return translation;
	}

	public void setTranslation(AnimationVector translation) {
		this.translation = translation;
	}

	public Matrix4f createModelMatrix(Matrix4f destination) {
		destination.identity();

		float[] f = flip.getV();
		float[] s = scaling.getV();
		destination.m00(f[0] * s[0]);
		destination.m11(f[1] * s[1]);
		destination.m22(f[2] * s[2]);
		destination.m33(f[3] * s[3]);

		Matrix4f rotated = new Matrix4f().identity(); // TODO remove somehow

		float[] r = rotation.getV();
		float rz = (float)(r[2] * Math.PI / 180.0);
		float ry = (float)(r[1] * Math.PI / 180.0);
		float rx = (float)(r[0] * Math.PI / 180.0);
		destination.mul(rotated.rotateZ(rz).rotateY(ry).rotateX(rx));

		float[] t = translation.getV();
		Matrix4f translated = rotated.m30(t[0]).m31(t[1]).m32(t[2]);
		return destination.mul(rotated).mul(translated);
	}

	public Matrix4f createJointMatrix(Matrix4f destination) {
		destination.identity();

		float[] s = scaling.getV();
		destination.m00(s[0]);
		destination.m11(s[1]);
		destination.m22(s[2]);
		destination.m33(s[3]);

		Matrix4f rotated = new Matrix4f().identity(); // TODO remove somehow

		float[] r = rotation.getV();
		float rz = (float)(r[2] * Math.PI / 180.0);
		float ry = (float)(r[1] * Math.PI / 180.0);
		float rx = (float)(r[0] * Math.PI / 180.0);
		destination.mul(rotated.rotateZ(rz).rotateY(ry).rotateX(rx));

		float[] t = translation.getV();
		Matrix4f translated = rotated.m30(t[0]).m31(t[1]).m32(t[2]);
		return destination.mul(rotated).mul(translated);
	}

	private static final long serialVersionUID = 1L;
}
