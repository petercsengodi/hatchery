package hu.csega.editors.anm.components;

import hu.csega.editors.anm.layer2Transformation.opengl.AnimatorOpenGLSet;

public interface ComponentOpenGLSetExtractor {

    AnimatorOpenGLSet extractAnimatorSet();

    void invalidate();

}
