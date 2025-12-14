package hu.csega.editors.anm.layer1Views.opengl.renderers;

import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.common.CommonInvalidatable;
import hu.csega.editors.anm.components.ComponentOpenGLSetExtractor;
import hu.csega.editors.anm.layer2Transformation.opengl.AnimatorOpenGLSet;
import hu.csega.editors.anm.layer2Transformation.opengl.AnimatorOpenGLSetPart;
import hu.csega.editors.anm.layer2Transformation.parts.AnimatorSetPart;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameTransformation;
import hu.csega.games.engine.intf.GameGraphics;

import java.util.List;

public class AnimatorAnimationRenderer extends AnimatorRenderer {

    private ComponentOpenGLSetExtractor openGLExtractor;

    public AnimatorAnimationRenderer(ComponentOpenGLSetExtractor openGLExtractor) {
        this.openGLExtractor = openGLExtractor;
    }

    @Override
    public void renderModel(GameEngineFacade facade, GameGraphics g, CommonEditorModel commonEditorModel) {
        AnimatorOpenGLSet set = openGLExtractor.extractAnimatorSet();

        List<AnimatorOpenGLSetPart> parts = set.getParts();
        if(parts != null && !parts.isEmpty()) {
            for(AnimatorOpenGLSetPart part : parts) {
                GameObjectHandler modelObject = part.getHandler();
                AnimatorSetPart originalPart = part.getOriginalPart();
                GameTransformation modelTransformation = originalPart.getTransformation();
                boolean flipped = originalPart.isFlipped();
                g.drawModel(modelObject, modelTransformation, flipped);
            }
        }
    }
}
