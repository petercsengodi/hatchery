package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;
import java.util.Map;

public class AnimationScene implements Serializable {

	private Map<String, AnimationScenePart> sceneParts;

	public Map<String, AnimationScenePart> getSceneParts() {
		return sceneParts;
	}

	public void setSceneParts(Map<String, AnimationScenePart> sceneParts) {
		this.sceneParts = sceneParts;
	}

	private static final long serialVersionUID = 1L;

}
