package hu.csega.editors.anm.components;

import hu.csega.editors.anm.layer1Views.swing.wireframe.AnimatorWireFrame;
import hu.csega.editors.anm.layer1Views.view3d.AnimatorSetPart;
import hu.csega.games.common.CommonDataTransformer;
import hu.csega.games.common.CommonDrain;
import hu.csega.games.common.CommonSource;

import java.util.List;

public interface ComponentWireFrameConverter extends CommonDataTransformer<List<AnimatorSetPart>, AnimatorWireFrame>,
CommonDrain<List<AnimatorSetPart>>, CommonSource<AnimatorWireFrame> {

    AnimatorWireFrame getWireFrame();

}
