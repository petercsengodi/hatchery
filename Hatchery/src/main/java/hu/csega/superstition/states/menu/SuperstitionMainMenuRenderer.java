package hu.csega.superstition.states.menu;

import hu.csega.games.engine.GameEngineCallback;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.intf.GameGraphics;
import hu.csega.superstition.game.SuperstitionGameElements;
import hu.csega.superstition.game.SuperstitionSerializableModel;
import hu.csega.superstition.states.SuperstitionModel;
import hu.csega.superstition.states.gameplay.SuperstitionGamePlayModel;

public class SuperstitionMainMenuRenderer implements GameEngineCallback {

    private static final String CURSOR = ">";
    private static final String NEW_GAME = "New game";
    private static final String RESUME = "Resume";
    private static final String EXIT = "Exit";

    private int cursor_row;

	@Override
	public Object call(GameEngineFacade facade) {
		SuperstitionModel model = (SuperstitionModel) facade.model();
		if(model == null)
			return facade;

        SuperstitionGameElements elements = SuperstitionGamePlayModel.elements;

        SuperstitionMainMenuModel mainMenu = (SuperstitionMainMenuModel)model.currentModel();
		GameObjectHandler splash = mainMenu.getSplash();

		GameGraphics g = facade.graphics();

		g.placeCamera(mainMenu.camera());

		GameObjectPlacement placement = new GameObjectPlacement();
		g.drawModel(splash, placement);


        drawString(g, elements, 10, cursor_row, CURSOR);

        return facade;
	}

    // TODO copied code
    public static void drawString(GameGraphics g, SuperstitionGameElements elements, int x, int y, String s) {
        int pos = x;
        int line = y;

        for(int i = 0; i < s.length(); i++) {
            GameObjectHandler character = elements.question;
            char c = s.charAt(i);
            if(c == '\n') {
                line ++;
                pos = x;
            }

            if(c != '\n' && c != '\r' && c <= ' ')
                pos ++;

            if(c <= ' ')
                continue;

            int maybe = (int)(c - 'a');
            if(maybe >= 0 && maybe < elements.numberOfLetters) {
                character = elements.alphabet[maybe];
            } else {
                maybe = (int)(c - 'A');
                if(maybe >= 0 && maybe < elements.numberOfLetters) {
                    character = elements.alphabet[maybe];
                } else {
                    maybe = (int)(c - '0');
                    if(maybe >= 0 && maybe < elements.numberOfNumbers) {
                        character = elements.numbers[maybe];
                    } else {
                        switch(c) {
                            case '.': character = elements.dot; break;
                            case ',': character = elements.comma; break;
                            case ':': character = elements.colon; break;
                            case '!': character = elements.exclamation; break;
                            case '?': character = elements.question; break;
                        }
                    }
                }
            }

            g.drawOnScreen(character, pos ++, line);
        }
    }

}
