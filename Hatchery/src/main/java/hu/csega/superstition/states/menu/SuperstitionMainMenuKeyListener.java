package hu.csega.superstition.states.menu;

import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameKeyListener;
import hu.csega.superstition.states.SuperstitionModel;
import hu.csega.superstition.states.gameplay.SuperstitionGamePlayModel;
import java.awt.event.KeyEvent;

public class SuperstitionMainMenuKeyListener implements GameKeyListener {

	@Override
	public void hit(GameEngineFacade facade, char key) {
        if(key == KeyEvent.VK_DOWN || key == 's' || key == 'S') {
            SuperstitionModel model = (SuperstitionModel) facade.model();
            if(model != null) {
                SuperstitionMainMenuModel mainMenu = (SuperstitionMainMenuModel) model.currentModel();
                mainMenu.cursorDown();
            }
        }

        if(key == KeyEvent.VK_UP || key == 'w' || key == 'W') {
            SuperstitionModel model = (SuperstitionModel) facade.model();
            if(model != null) {
                SuperstitionMainMenuModel mainMenu = (SuperstitionMainMenuModel) model.currentModel();
                mainMenu.cursorUp();
            }
        }

        if(key == KeyEvent.VK_ENTER) {
            SuperstitionModel model = (SuperstitionModel) facade.model();
            if(model != null) {
                SuperstitionMainMenuModel mainMenu = (SuperstitionMainMenuModel) model.currentModel();
                switch (mainMenu.getCursorRow()) {

                    case 0:
                        model.toggleMainManu();
                        if(model.currentModel() instanceof SuperstitionGamePlayModel) {
                            SuperstitionGamePlayModel playModel = (SuperstitionGamePlayModel) model.currentModel();
                            playModel.initializeModel(facade);
                            // FIXME: Disposing old stuff??????
                        } else {
                            throw new RuntimeException("Current model should be a SuperstitionGamePlayModel (after the toggle operation).");
                        }
                        break;

                    case 1:
                        model.toggleMainManu();
                        break;

                    default:
                        facade.window().closeApplication();
                }
            }
        }
	}

}
