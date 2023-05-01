package hu.csega.editors.anm.layer4.data.model;

import hu.csega.editors.anm.components.ComponentExtractJointList;
import hu.csega.editors.anm.components.ComponentExtractPartList;
import hu.csega.editors.anm.components.ComponentOpenGLExtractor;
import hu.csega.editors.anm.components.ComponentRefreshViews;
import hu.csega.editors.anm.components.ComponentWireFrameConverter;
import hu.csega.editors.anm.layer1.swing.AnimatorUIComponents;
import hu.csega.editors.anm.layer1.view3d.AnimatorSetPart;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPartJoint;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.animation.v1.anm.AnimationScenePart;
import hu.csega.games.units.Dependency;
import hu.csega.games.units.UnitStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;

public class AnimatorRefreshViews implements ComponentRefreshViews {

	private AnimatorModel model;
	private ResourceAdapter resourceAdapter;

	private ComponentExtractPartList partListExtractor;
	private ComponentExtractJointList jointListExtractor;
	private ComponentWireFrameConverter wireFrameConverter;
	private ComponentOpenGLExtractor openGLExtractor;
	private AnimatorUIComponents components;

	private Matrix4f m1 = new Matrix4f();
	private Matrix4f m2 = new Matrix4f();
	private Matrix4f m3 = new Matrix4f();
	private Matrix4f m4 = new Matrix4f();
	private Matrix4f m5 = new Matrix4f();

	@Override
	public void refreshAll() {
		if(partListExtractor == null) {
			partListExtractor = UnitStore.instance(ComponentExtractPartList.class);
		}

		if(jointListExtractor == null) {
			jointListExtractor = UnitStore.instance(ComponentExtractJointList.class);
		}

		if(wireFrameConverter == null) {
			wireFrameConverter = UnitStore.instance(ComponentWireFrameConverter.class);
		}

		if(openGLExtractor == null) {
			openGLExtractor = UnitStore.instance(ComponentOpenGLExtractor.class);
		}

		if(components == null) {
			components = UnitStore.instance(AnimatorUIComponents.class);
		}

		synchronized (model) {
			List<AnimatorSetPart> parts = new ArrayList<>();
			AnimationPersistent persistent = model.getPersistent();
			Animation animation = persistent.getAnimation();
			int selectedScene = persistent.getSelectedScene();

			if(partListExtractor != null) {
				partListExtractor.accept(animation);
			}

			String selectedPart = persistent.getSelectedPart();
			if(selectedPart != null && !selectedPart.isEmpty()) {
				jointListExtractor.accept(animation.getParts().get(selectedPart));
			} else {
				jointListExtractor.accept(null);
			}

			Map<String, AnimationPart> map = animation.getParts();
			if(map != null && map.size() > 0) {
				int numberOfScenes = animation.getNumberOfScenes();
				if(selectedScene < 0 || selectedScene >= numberOfScenes) {
					selectedScene = 0;
				}

				generateParts(animation, selectedScene, parts);
			}

			if(openGLExtractor != null) {
				openGLExtractor.accept(parts);
			}

			if(wireFrameConverter != null) {
				wireFrameConverter.accept(parts);
			}

			components.jsonView.setJSON(animation);
		}

	} // end of refreshAll

	private void generateParts(Animation animation, int currentScene, List<AnimatorSetPart> parts) {
		Map<String, String> connections = animation.getConnections();
		for(Map.Entry<String, AnimationPart> entry : animation.getParts().entrySet()) {
			String partIdentifier = entry.getKey();
			if(connections.containsKey(partIdentifier)) {
				continue;
			}

			AnimationPart part = entry.getValue();
			transformPart(animation, currentScene, part, null, parts);
		} // end for entry
	}

	private void generateParts(Animation animation, int currentScene, String jointKey, Matrix4f m, List<AnimatorSetPart> parts) {
		Map<String, String> connections = animation.getConnections();
		for(Map.Entry<String, String> entry : connections.entrySet()) {
			if(jointKey.equals(entry.getValue())) {
				AnimationPart part = animation.getParts().get(entry.getKey());
				transformPart(animation, currentScene, part, m, parts);
			}
		} // end for entry
	}

	private void transformPart(Animation animation, int currentScene, AnimationPart part, Matrix4f m, List<AnimatorSetPart> parts) {
		if(part != null) {
			AnimationScenePart scenePart = animation.createOrGetScenePart(currentScene, part.getIdentifier());
			if(!scenePart.isVisible()) {
				return;
			}

			if(m == null) {
				m2.set(part.getBasicTransformation().getM());
			} else {
				m1.set(part.getBasicTransformation().getM());
				m.mul(m1, m2);
			}

			scenePart.getTransformation().createModelMatrix(m3);
			m2.mul(m3, m4);

			GameTransformation transformation = new GameTransformation();
			transformation.importFrom(m4);

			float[] flip = scenePart.getTransformation().getFlip().getV();
			boolean flipped = (flip[0] < 0f) ^ (flip[1] < 0f) ^ (flip[2] < 0f);

			AnimatorSetPart setPart = new AnimatorSetPart();
			setPart.setMesh(part.getMesh());
			setPart.setTransformation(transformation);
			setPart.setFlipped(flipped);
			parts.add(setPart);

			List<AnimationPartJoint> joints = part.getJoints();
			if(joints.size() > 0) {
				scenePart.getTransformation().createJointMatrix(m3);

				for (AnimationPartJoint joint : part.getJoints()) {
					Matrix4f _m = new Matrix4f();
					float[] v = joint.getRelativePosition().getV();
					m2.translateLocal(v[0], v[1], v[2], m4);
					m3.mul(m4, _m);
					// m5.invert(_m); // ???
					generateParts(animation, currentScene, joint.getIdentifier(), _m, parts);
				}
			} // end for each joint
		} // end if part not null
	}

	@Dependency
	public void setAnimatorModel(AnimatorModel animatorModel) {
		this.model = animatorModel;
	}

	@Dependency
	public void setResourceAdapter(ResourceAdapter resourceAdapter) {
		this.resourceAdapter = resourceAdapter;
	}

}
