package hu.csega.editors.anm.components;

import hu.csega.editors.anm.common.CommonComponent;
import hu.csega.editors.anm.layer2Transformation.opengl.AnimatorOpenGLSet;

public interface ComponentOpenGLSetExtractor extends CommonComponent {

    AnimatorOpenGLSet extractAnimatorSet();

}
