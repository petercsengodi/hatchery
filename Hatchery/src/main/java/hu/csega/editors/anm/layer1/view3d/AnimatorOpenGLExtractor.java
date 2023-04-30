package hu.csega.editors.anm.layer1.view3d;

import hu.csega.editors.anm.components.ComponentOpenGLExtractor;
import hu.csega.editors.anm.components.ComponentOpenGLTransformer;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameModelStore;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationMisc;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPartJoint;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.animation.v1.anm.AnimationPlacement;
import hu.csega.games.library.animation.v1.anm.AnimationScene;
import hu.csega.games.library.animation.v1.anm.AnimationScenePart;
import hu.csega.games.library.animation.v1.anm.AnimationVector;
import hu.csega.games.units.UnitStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;

public class AnimatorOpenGLExtractor implements ComponentOpenGLExtractor {

	private AnimatorSet set;
	private ComponentOpenGLTransformer transformer;
	private GameEngineFacade facade;
	private GameModelStore store;
	private ResourceAdapter resourceAdapter;

	private Matrix4f m1 = new Matrix4f();
	private Matrix4f m2 = new Matrix4f();
	private Matrix4f m3 = new Matrix4f();
	private Matrix4f m4 = new Matrix4f();
	private Matrix4f m5 = new Matrix4f();

	@Override
	public void accept(AnimatorModel model) {
		if(model == null) {
			return;
		}

		if(transformer == null) {
			transformer = UnitStore.instance(ComponentOpenGLTransformer.class);
		}

		if(facade == null || store == null) {
			facade = UnitStore.instance(GameEngineFacade.class);
			store = facade.store();
		}

		if(resourceAdapter == null) {
			resourceAdapter = UnitStore.instance(ResourceAdapter.class);
		}

		synchronized (model) {
			generateSet(model.getPersistent());
		}

		transformer.accept(set);
	}

	private void generateSet(AnimationPersistent persistent) {
		if(this.set == null) {
			this.set = new AnimatorSet();
		}

		int currentScene = 0;
		GameObjectPlacement camera = new GameObjectPlacement();
		List<AnimatorSetPart> parts = new ArrayList<>();

		if(persistent != null) {
			AnimationMisc misc = persistent.getMisc();
			if(misc != null) {
				currentScene = misc.getCurrentScene();

				AnimationPlacement cam = misc.getCamera();
				if(cam != null) {
					AnimationVector pos = cam.getPosition();
					if(pos != null && pos.getV() != null) {
						float[] v = pos.getV();
						camera.position.set(v[0], v[1], v[2]);
					} else {
						camera.position.set(0f, 0f, -100f);
					}

					AnimationVector tar = cam.getTarget();
					if(tar != null && tar.getV() != null) {
						float[] v = tar.getV();
						camera.target.set(v[0], v[1], v[2]);
					} else {
						camera.target.set(0f, 0f, 0f);
					}

					AnimationVector up = cam.getTarget();
					if(up != null && up.getV() != null) {
						float[] v = up.getV();
						camera.up.set(v[0], v[1], v[2]);
					} else {
						camera.up.set(0f, 1f, 0f);
					}
				} else {
					camera.position.set(0f, 400f, -400f);
					camera.target.set(0f, 0f, 0f);
					camera.up.set(0f, 1f, 0f);
				}
			} else {
				camera.position.set(0f, 400f, -400f);
				camera.target.set(0f, 0f, 0f);
				camera.up.set(0f, 1f, 0f);
			}

			Animation animation = persistent.getAnimation();
			if(animation != null) {

				Map<String, AnimationPart> map = animation.getParts();
				if(map != null && map.size() > 0) {

					Map<String, String> connections = animation.getConnections();
					List<AnimationScene> scenes = animation.getScenes();
					if(scenes != null && scenes.size() > 0) {
						if(currentScene < 0 || currentScene >= scenes.size()) {
							currentScene = 0;
						}

						AnimationScene scene = scenes.get(currentScene);
						Map<String, AnimationScenePart> sceneParts = scene.getSceneParts();
						if(sceneParts != null && sceneParts.size() > 0) {
							generateParts(parts, connections, sceneParts, map);
						}
					}

				} // end map check
			}

		}

		set.setCamera(camera);
		set.setParts(parts);
	}

	private void generateParts(List<AnimatorSetPart> parts, Map<String, String> connections,
			Map<String, AnimationScenePart> sceneParts, Map<String, AnimationPart> map) {

		for(Map.Entry<String, AnimationScenePart> entry : sceneParts.entrySet()) {
			String partKey = entry.getKey();
			if(connections.containsKey(partKey)) {
				continue;
			}

			AnimationScenePart scenePart = entry.getValue();
			transformPart(parts, null, partKey, scenePart, connections, map, sceneParts);
		}

	}

	private void generateParts(List<AnimatorSetPart> parts, Matrix4f m, String jointKey, Map<String, String> connections,
							   Map<String, AnimationScenePart> sceneParts, Map<String, AnimationPart> map) {

		for(Map.Entry<String, AnimationScenePart> entry : sceneParts.entrySet()) {
			String partKey = entry.getKey();
			if(jointKey.equals(connections.get(partKey))) {
				AnimationScenePart scenePart = entry.getValue();
				transformPart(parts, m, partKey, scenePart, connections, map, sceneParts);
			}
		}

	}

	private void transformPart(List<AnimatorSetPart> parts, Matrix4f m, String partKey, AnimationScenePart scenePart,
			Map<String, String> connections, Map<String, AnimationPart> map, Map<String, AnimationScenePart> sceneParts) {
		if(!scenePart.isVisible()) {
			return;
		}

		AnimationPart animationPart = map.get(partKey);
		if(animationPart != null) {

			String filename = animationPart.getMesh();
			if(filename == null || filename.length() == 0) {
				return;
			}

			if(filename.charAt(0) != '/') {
				filename = resourceAdapter.projectRoot() + filename;
			}

			GameObjectHandler model = store.loadModel(filename);
			if(model == null) {
				throw new RuntimeException("Couldn't load game model: " + filename);
			}

			if(m == null) {
				m2.set(animationPart.getBasicTransformation().getM());
			} else {
				m1.set(animationPart.getBasicTransformation().getM());
				m.mul(m1, m2);
			}

			scenePart.getTransformation().createModelMatrix(m3);
			m2.mul(m3, m4);

			GameTransformation transformation = new GameTransformation();
			transformation.importFrom(m4);

			float[] flip = scenePart.getTransformation().getFlip().getV();
			boolean flipped = (flip[0] < 0f) ^ (flip[1] < 0f) ^ (flip[2] < 0f);

			AnimatorSetPart setPart = new AnimatorSetPart();
			setPart.setTransformation(transformation);
			setPart.setModel(model);
			setPart.setFlipped(flipped);
			parts.add(setPart);

			List<AnimationPartJoint> joints = animationPart.getJoints();
			if(joints.size() > 0) {
				scenePart.getTransformation().createJointMatrix(m3);
				m2.mul(m3, m4);

				for (AnimationPartJoint joint : animationPart.getJoints()) {
					Matrix4f _m = new Matrix4f();
					float[] v = joint.getRelativePosition().getV();
					m4.translateLocal(v[0], v[1], v[2], m5);
					m5.invert(_m); // ???
					generateParts(parts, _m, joint.getIdentifier(), connections, sceneParts, map);
				}
			} // end for each joint
		}
	}

}
