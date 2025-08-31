package hu.csega.editors.anm.common;

import hu.csega.editors.anm.layer1.opengl.AnimatorMouseController;
import hu.csega.editors.anm.layer1.swing.views.AnimatorObject;
import hu.csega.games.engine.g3d.GameObjectPlacement;

import java.util.Collection;

public interface CommonEditorModel {

    GameObjectPlacement cameraPlacement();

    long getSelectionLastChanged();

    Collection<AnimatorObject> getSelectedObjects();

    void finalizeMove();

    void setCameraPosition(float px, float py, float pz, float pw);
}
