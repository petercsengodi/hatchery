package hu.csega.editors.anm.layer1Views.opengl;

import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.layer1Views.view3d.AnimatorSet;
import hu.csega.editors.anm.layer1Views.view3d.AnimatorSetPart;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.engine.intf.GameGraphics;

import java.util.List;

public class AnimatorAnimationRenderer extends AnimatorRenderer {

    private AnimatorSet set;

    public void accept(AnimatorSet set) {
        this.set = set;
    }

    @Override
    void paint(GameEngineFacade facade, GameGraphics g, CommonEditorModel commonEditorModel) {
        if(set != null) {
            List<AnimatorSetPart> parts = set.getParts();
            if(parts != null && !parts.isEmpty()) {
                for(AnimatorSetPart part : parts) {
                    GameObjectHandler modelObject = part.getHandler();
                    GameTransformation modelTransformation = part.getTransformation();
                    boolean flipped = part.isFlipped();
                    g.drawModel(modelObject, modelTransformation, flipped);
                }
            }
        }
    }
}
