package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;
import java.util.Map;

public class AnimationScene implements Serializable {

	private Map<Integer, AnimationScenePart> sceneParts;

	public Map<Integer, AnimationScenePart> getSceneParts() {
		return sceneParts;
	}

	public void setSceneParts(Map<Integer, AnimationScenePart> sceneParts) {
		this.sceneParts = sceneParts;
	}

	private static final long serialVersionUID = 1L;

}
