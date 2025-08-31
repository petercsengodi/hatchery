package hu.csega.editors.anm.layer1.opengl;

import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.layer1.swing.views.AnimatorViewCanvas;
import hu.csega.editors.anm.layer1.swing.views.AnimatorViewContextMenu;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.common.lens.EditorLensPipeline;
import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshPictogram;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.intf.GameGraphics;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

import java.awt.*;
import java.util.Set;

public abstract class AnimatorRenderer {

	abstract void paint(GameEngineFacade facade, GameGraphics g, CommonEditorModel model);

}
