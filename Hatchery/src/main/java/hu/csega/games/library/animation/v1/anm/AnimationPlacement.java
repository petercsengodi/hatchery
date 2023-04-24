package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;

public class AnimationPlacement implements Serializable {

	private AnimationVector position;
	private AnimationVector target;
	private AnimationVector up;

	public AnimationPlacement() {
		this.position = new AnimationVector(0f, 0f, 30f);
		this.target = new AnimationVector(0f, 0f, 0f);
		this.up = new AnimationVector(0f, 1f, 0f);
	}

	public AnimationVector getPosition() {
		return position;
	}

	public void setPosition(AnimationVector position) {
		this.position = position;
	}

	public AnimationVector getTarget() {
		return target;
	}

	public void setTarget(AnimationVector target) {
		this.target = target;
	}

	public AnimationVector getUp() {
		return up;
	}

	public void setUp(AnimationVector up) {
		this.up = up;
	}

	private static final long serialVersionUID = 1L;

}
