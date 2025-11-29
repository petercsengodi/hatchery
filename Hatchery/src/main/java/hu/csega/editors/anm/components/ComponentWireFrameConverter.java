package hu.csega.editors.anm.components;

import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrame;

public interface ComponentWireFrameConverter {

    void invalidate();

    AnimatorWireFrame getWireFrame();

}
