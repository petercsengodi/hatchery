package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;

import org.json.JSONString;

public class AnimationTransformation implements Serializable, JSONString {

	/** Matrix, float, 4x4. */
	private float[] m;

	public AnimationTransformation() {
		this.m = new float[16];
		this.m[0] = 1f;
		this.m[5] = 1f;
		this.m[10] = 1f;
		this.m[15] = 1f;
	}

	public float[] getM() {
		return m;
	}

	public void setM(float[] m) {
		this.m = m;
	}

	@Override
	public String toJSONString() {
		return "[\"Matrix\"]"; // FIXME
	}

	private static final long serialVersionUID = 1L;
}
