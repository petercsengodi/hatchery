package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AnimationScene implements Serializable {

	private Map<String, AnimationScenePart> sceneParts = new HashMap<>();

	public Map<String, AnimationScenePart> getSceneParts() {
		return sceneParts;
	}

	public void setSceneParts(Map<String, AnimationScenePart> sceneParts) {
		this.sceneParts = sceneParts;
	}

	public AnimationScenePart createOrGetScenePart(String partIdentifier) {
		return sceneParts.computeIfAbsent(partIdentifier, id -> new AnimationScenePart());
	}

	private static final long serialVersionUID = 1L;

}
