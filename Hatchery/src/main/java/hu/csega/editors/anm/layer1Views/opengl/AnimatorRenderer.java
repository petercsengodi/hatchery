package hu.csega.editors.anm.layer1Views.opengl;

import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameGraphics;

public abstract class AnimatorRenderer {

	abstract void paint(GameEngineFacade facade, GameGraphics g, CommonEditorModel model);

}
