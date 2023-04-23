package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;

import org.joml.Matrix4f;

public class AnimationDetailedTransformation implements Serializable {

	private AnimationVector flip = new AnimationVector(1f, 1f, 1f);
	private AnimationVector scaling = new AnimationVector(1f, 1f, 1f);
	private AnimationVector rotation = new AnimationVector(1f, 1f, 1f);
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

	private static final long serialVersionUID = 1L;

	public Matrix4f createMatrix() {
		Matrix4f result = new Matrix4f().identity();

		float[] f = flip.getV();
		float[] s = scaling.getV();
		result.m00(f[0] * s[0]);
		result.m11(f[1] * s[1]);
		result.m22(f[2] * s[2]);
		result.m33(f[3] * s[3]);

		Matrix4f rotated = new Matrix4f().identity();

		float[] r = rotation.getV();
		result = result.mul(rotated.rotateZ(r[2]).rotateY(r[1]).rotateX(r[0]));

		float[] t = translation.getV();
		Matrix4f translated = rotated.m30(t[0]).m31(t[1]).m32(t[2]);
		return result.mul(rotated).mul(translated);
	}
}
