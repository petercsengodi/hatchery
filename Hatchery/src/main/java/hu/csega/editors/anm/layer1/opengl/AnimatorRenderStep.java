package hu.csega.editors.anm.layer1.opengl;

import java.util.List;

import hu.csega.editors.anm.layer1.view3d.AnimatorSet;
import hu.csega.editors.anm.layer1.view3d.AnimatorSetPart;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.engine.GameEngineCallback;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectDirection;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.g3d.GameObjectPosition;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.engine.intf.GameGraphics;
import hu.csega.games.library.animation.v1.anm.AnimationMisc;
import hu.csega.games.library.animation.v1.anm.AnimationPlacement;
import hu.csega.games.library.animation.v1.anm.AnimationVector;

public class AnimatorRenderStep implements GameEngineCallback {

	private AnimatorSet set;
	private AnimatorModel model;

	public AnimatorRenderStep() {
	}

	public void setAnimatorSet(AnimatorSet set) {
		this.set = set;
	}

	@Override
	public Object call(GameEngineFacade facade) {
		GameGraphics g = facade.graphics();

		if(model == null) {
			model = (AnimatorModel) facade.model();
			if(model == null) {
				return facade;
			}
		}

		AnimationPersistent persistent = model.getPersistent();
		if(persistent == null) {
			return facade;
		}

		AnimationMisc misc = persistent.getMisc();
		if(misc == null) {
			misc = new AnimationMisc();
			persistent.setMisc(misc);
		}

		AnimationPlacement camera = misc.getCamera();
		if(camera == null) {
			return facade;
		}

		if(set != null) {
			GameObjectPlacement cameraPlacement = new GameObjectPlacement();
			cameraPlacement.setPositionTargetUp(
					convertPosition(camera.getPosition()),
					convertPosition(camera.getTarget()),
					convertDirection(camera.getUp())
			);
			g.placeCamera(cameraPlacement);

			List<AnimatorSetPart> parts = set.getParts();
			if(parts != null && parts.size() > 0) {
				for(AnimatorSetPart part : parts) {
					GameObjectHandler modelObject = part.getModel();
					GameTransformation modelTransformation = part.getTransformation();
					g.drawModel(modelObject, modelTransformation);
				}
			}
		}

		return facade;
	}

	private GameObjectPosition convertPosition(AnimationVector vector) {
		float[] v = vector.getV();
		return new GameObjectPosition(v[0]/v[3], v[1]/v[3], v[2]/v[3]);
	}

	private GameObjectDirection convertDirection(AnimationVector vector) {
		float[] v = vector.getV();
		return new GameObjectDirection(v[0]/v[3], v[1]/v[3], v[2]/v[3]);
	}

}
