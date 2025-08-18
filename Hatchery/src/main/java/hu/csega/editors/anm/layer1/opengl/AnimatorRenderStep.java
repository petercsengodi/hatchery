package hu.csega.editors.anm.layer1.opengl;

import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.layer1.view3d.AnimatorSet;
import hu.csega.editors.anm.layer1.view3d.AnimatorSetPart;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.engine.GameEngineCallback;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.engine.intf.GameGraphics;

import java.util.List;

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

		CommonEditorModel common = model.selectModel();


		if(set != null) {

			g.placeCamera(common.cameraPlacement());

			List<AnimatorSetPart> parts = set.getParts();
			if(parts != null && parts.size() > 0) {
				for(AnimatorSetPart part : parts) {
					GameObjectHandler modelObject = part.getHandler();
					GameTransformation modelTransformation = part.getTransformation();
					boolean flipped = part.isFlipped();
					g.drawModel(modelObject, modelTransformation, flipped);
				}
			}
		}

		return facade;
	}

}
