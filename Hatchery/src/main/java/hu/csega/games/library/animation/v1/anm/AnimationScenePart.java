package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;

public class AnimationScenePart implements Serializable {

	private AnimationDetailedTransformation transformation;
	private boolean visible;

	public AnimationDetailedTransformation getTransformation() {
		return transformation;
	}

	public void setTransformation(AnimationDetailedTransformation transformation) {
		this.transformation = transformation;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	private static final long serialVersionUID = 1L;

}
