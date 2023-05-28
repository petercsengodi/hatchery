package hu.csega.editors.anm.layer1.view3d;

import hu.csega.editors.anm.components.ComponentOpenGLExtractor;
import hu.csega.editors.anm.components.ComponentOpenGLTransformer;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameModelStore;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.library.animation.v1.anm.AnimationMisc;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.animation.v1.anm.AnimationPlacement;
import hu.csega.games.library.animation.v1.anm.AnimationVector;
import hu.csega.games.units.UnitStore;

import java.util.List;

public class AnimatorOpenGLExtractor implements ComponentOpenGLExtractor {

	private AnimatorModel animatorModel;
	private ResourceAdapter resourceAdapter;

	private AnimatorSet set;
	private ComponentOpenGLTransformer transformer;
	private GameEngineFacade facade;
	private GameModelStore store;

	@Override
	public void accept(List<AnimatorSetPart> parts) {
		if(animatorModel == null) {
			animatorModel = UnitStore.instance(AnimatorModel.class);
		}

		if(resourceAdapter == null) {
			resourceAdapter = UnitStore.instance(ResourceAdapter.class);
		}

		if(transformer == null) {
			transformer = UnitStore.instance(ComponentOpenGLTransformer.class);
		}

		if(facade == null || store == null) {
			facade = UnitStore.instance(GameEngineFacade.class);
			store = facade.store();
		}

		generateSet(animatorModel.getPersistent(), parts);

		transformer.accept(set);
	}

	private void generateSet(AnimationPersistent persistent, List<AnimatorSetPart> parts) {
		if(this.set == null) {
			this.set = new AnimatorSet();
		}

		GameObjectPlacement camera = new GameObjectPlacement();

		AnimationMisc misc = persistent.getMisc();
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

		for(AnimatorSetPart part : parts) {

			String filename = part.getMesh();
			if(filename == null || filename.length() == 0) {
				return;
			}

			if(filename.charAt(0) != '/') {
				filename = resourceAdapter.resourcesRoot() + filename;
			}

			GameObjectHandler handler = store.loadModel(filename);
			if(handler == null) {
				throw new RuntimeException("Couldn't load game model: " + filename);
			}

			part.setHandler(handler);
		}

		set.setCamera(camera);
		set.setParts(parts);
	}

}
