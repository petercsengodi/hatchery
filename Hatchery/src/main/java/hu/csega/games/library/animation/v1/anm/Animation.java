package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Animation implements Serializable {

	private Map<String, AnimationPart> parts;
	private List<AnimationScene> scenes;
	private int maxPartIndex;

	public Map<String, AnimationPart> getParts() {
		return parts;
	}

	public void setParts(Map<String, AnimationPart> parts) {
		this.parts = parts;
	}

	public List<AnimationScene> getScenes() {
		return scenes;
	}

	public void setScenes(List<AnimationScene> scenes) {
		this.scenes = scenes;
	}

	public int getMaxPartIndex() {
		return maxPartIndex;
	}

	public void setMaxPartIndex(int maxPartIndex) {
		this.maxPartIndex = maxPartIndex;
	}

	public void cleanUpScenes() {
		if(scenes == null) {
			scenes = new ArrayList<>();
		}

		if(scenes.size() == 0) {
			scenes.add(new AnimationScene());
		}

		for(AnimationScene scene : scenes) {
			Map<String, AnimationScenePart> map = scene.getSceneParts();
			if(map == null) {
				map = new TreeMap<>();
				scene.setSceneParts(map);
			}

			// Clear info for indexes that don't exist anymore.
			Iterator<Entry<String, AnimationScenePart>> entries = map.entrySet().iterator();
			while(entries.hasNext()) {
				Entry<String, AnimationScenePart> entry = entries.next();
				if(!parts.containsKey(entry.getKey())) {
					entries.remove();
				}
			}

			// Add info object for indexes that now exist, but not contained by the scenes.
			Iterator<String> indexes = parts.keySet().iterator();
			while(indexes.hasNext()) {
				String partIdentifier = indexes.next();
				if(!map.containsKey(partIdentifier)) {
					AnimationScenePart value = new AnimationScenePart();
					value.setModelTransformation(new AnimationTransformation());
					value.setPartTransformation(new AnimationTransformation());
					value.setVisible(true);
					map.put(partIdentifier, value);
				}
			}

		}

	}

	private static final long serialVersionUID = 1L;

}
