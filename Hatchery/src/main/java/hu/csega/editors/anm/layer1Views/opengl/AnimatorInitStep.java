package hu.csega.editors.anm.layer1Views.opengl;

import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.engine.GameEngineCallback;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.units.UnitStore;

public class AnimatorInitStep implements GameEngineCallback {

	@Override
	public Object call(GameEngineFacade facade) {
		AnimatorModel model = UnitStore.instance(AnimatorModel.class);
		facade.setModel(model);
		return facade;
	}

}
