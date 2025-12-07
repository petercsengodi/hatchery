package hu.csega.editors.anm.components;

import hu.csega.editors.anm.common.CommonComponent;
import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrame;

public interface ComponentWireFrameConverter extends CommonComponent {

    AnimatorWireFrame getWireFrame();

}
