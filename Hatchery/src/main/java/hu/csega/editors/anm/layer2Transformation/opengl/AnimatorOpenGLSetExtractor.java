package hu.csega.editors.anm.layer2Transformation.opengl;

import hu.csega.editors.anm.components.ComponentOpenGLSetExtractor;
import hu.csega.editors.anm.layer2Transformation.parts.AnimatorSetExtractor;
import hu.csega.editors.anm.layer2Transformation.parts.AnimatorSetPart;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.editors.common.resources.ResourceAdapter;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameModelStore;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.library.animation.v1.anm.AnimationMisc;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.animation.v1.anm.AnimationPlacement;
import hu.csega.games.library.animation.v1.anm.AnimationVector;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.units.Dependency;
import hu.csega.games.units.UnitStore;

import java.util.List;

public class AnimatorOpenGLSetExtractor implements ComponentOpenGLSetExtractor {

	private AnimatorOpenGLSet set;

	///////////////////////////////////////////////////////////////////////
	// Dependencies
	private AnimatorModel animatorModel;
	private ResourceAdapter resourceAdapter;
	private GameEngineFacade facade;
	private GameModelStore store;
	private AnimatorSetExtractor setExtractor;

	@Override
	public synchronized void invalidate() {
		this.set = null;

		// FIXME : Invalidate OpenGL, repaint.
	}

	@Override
	public synchronized AnimatorOpenGLSet extractAnimatorSet() {
		if(this.set != null) {
			return this.set;
		}

		this.set = new AnimatorOpenGLSet();

		AnimationPersistent persistent = this.animatorModel.getPersistent();
		AnimationMisc misc = persistent.getMisc();
		GameObjectPlacement camera = new GameObjectPlacement();
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

		set.setCamera(camera);

		List<AnimatorSetPart> parts = setExtractor.extractSetParts();
		for(AnimatorSetPart part : parts) {
			AnimatorOpenGLSetPart openGLpart = new AnimatorOpenGLSetPart();
			openGLpart.setOriginalPart(part);

			FreeTriangleMeshModel meshModel = part.getMeshModel();
			if(meshModel != null) {
				GameObjectHandler gameObjectHandler = meshModel.ensureConvertedModelIsBuilt(facade);
				openGLpart.setHandler(gameObjectHandler);
			} else {
				String filename = part.getMesh();
				if (filename == null || filename.length() == 0) {
					continue;
				}

				if (filename.charAt(0) != '/') {
					filename = resourceAdapter.resourcesRoot() + filename;
				}

				GameObjectHandler handler = loadMesh(filename);
				openGLpart.setHandler(handler);
			}

			set.addPart(openGLpart);
		}

		return set;
	}

	private GameObjectHandler loadMesh(String filename) {
		if(store == null) {
			store = getFacade().store();
		}
		GameObjectHandler handler = store.loadMesh(filename);
		if (handler == null) {
			throw new RuntimeException("Couldn't load game model: " + filename);
		}

		return handler;
	}

	private GameEngineFacade getFacade() {
		if(facade == null) {
			facade = UnitStore.instance(GameEngineFacade.class);
		}

		return facade;
	}

	@Dependency
	public void setAnimatorModel(AnimatorModel animatorModel) {
		this.animatorModel = animatorModel;
	}

	@Dependency
	public void setResourceAdapter(ResourceAdapter resourceAdapter) {
		this.resourceAdapter = resourceAdapter;
	}

	@Dependency
	public void setSetExtractor(AnimatorSetExtractor setExtractor) {
		this.setExtractor = setExtractor;
	}
}
