package hu.csega.editors.anm.layer1Views.opengl;

import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.intf.GameGraphics;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;

public class AnimatorMeshRenderer extends AnimatorRenderer {

    private GameObjectPlacement modelPlacement = new GameObjectPlacement();

    @Override
    void paint(GameEngineFacade facade, GameGraphics g, CommonEditorModel commonEditorModel) {
        FreeTriangleMeshModel model = (FreeTriangleMeshModel) commonEditorModel;
        if(model == null)
            return;

        GameObjectHandler convertedModel = model.ensureConvertedModelIsBuilt(facade);
        if(convertedModel != null) {
            g.drawModel(convertedModel, modelPlacement);
        }
    }

}
