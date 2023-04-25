package hu.csega.games.library.animation.v1.anm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Animation implements Serializable {

	private Map<String, AnimationPart> parts = new LinkedHashMap<>();
	private Map<String, String> connections = new HashMap<>();
	private List<AnimationScene> scenes = new ArrayList<>();
	private int maxPartIndex;

	public Map<String, AnimationPart> getParts() {
		return parts;
	}

	public void setParts(Map<String, AnimationPart> parts) {
		this.parts = parts;
	}

	public void connect(String partIdentifier, String jointIdentifier) {
		connections.put(partIdentifier, jointIdentifier);
	}

	public void disconnect(String partIdentifier) {
		connections.remove(partIdentifier);
	}

	public Map<String, String> getConnections() {
		return connections;
	}

	public List<AnimationScene> getScenes() {
		return scenes;
	}

	public void setScenes(List<AnimationScene> scenes) {
		this.scenes = scenes;
	}

	public AnimationScene createOrGetScene(int index) {
		if(index < 0) {
			return null;
		}

		if(index >= scenes.size()) {
			for(int i = scenes.size(); i <= index; i++) {
				scenes.set(i, null);
			}
		}

		AnimationScene scene = scenes.get(index);
		if(scene == null) {
			scene = new AnimationScene();
			scenes.set(index, scene);
		}

		return scene;
	}

	public AnimationScenePart createOrGetScenePart(int index, String partIdentifier) {
		return createOrGetScene(index).createOrGetScenePart(partIdentifier);
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
					value.setTransformation(new AnimationDetailedTransformation());
					value.setVisible(true);
					map.put(partIdentifier, value);
				}
			}

		}

	}

	private static final long serialVersionUID = 1L;

}
