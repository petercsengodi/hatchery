package hu.csega.superstition.states.spells;

import hu.csega.games.engine.GameEngineCallback;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.intf.GameGraphics;
import hu.csega.superstition.states.SuperstitionModel;

public class SuperstitionGameSpellsRenderer implements GameEngineCallback {

	@Override
	public Object call(GameEngineFacade facade) {
		SuperstitionModel model = (SuperstitionModel) facade.model();
		if(model == null)
			return facade;

		SuperstitionGameSpellsModel mainMenu = (SuperstitionGameSpellsModel)model.currentModel();
		GameObjectHandler splash = mainMenu.getSplash();

		GameGraphics g = facade.graphics();

		g.placeCamera(mainMenu.camera());

		GameObjectPlacement placement = new GameObjectPlacement();
		g.drawModel(splash, placement);

		return facade;
	}

}
