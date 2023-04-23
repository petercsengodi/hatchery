package hu.csega.editors.anm.layer1.swing.components.rotator;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.animation.v1.anm.AnimationScene;
import hu.csega.games.library.animation.v1.anm.AnimationScenePart;
import hu.csega.games.units.UnitStore;

public class AnimatorModelBinding implements AnimatorRotatorBinding {

    private AnimatorModel model;

    public AnimatorModelBinding() {
        this.model = UnitStore.instance(AnimatorModel.class);
    }

    @Override
    public void changeXRotation(double change) {
        model.rotateSelectedPart(change, 0, 0);
    }

    @Override
    public void changeYRotation(double change) {
        model.rotateSelectedPart(0, change, 0);
    }

    @Override
    public void changeZRotation(double change) {
        model.rotateSelectedPart(0, 0, change);
    }

    @Override
    public double currentXRotation() {
        AnimationPersistent persistent = model.getPersistent();
        if(persistent == null) {
            return 0;
        }

        String selectedPart = persistent.getSelectedPart();
        if(selectedPart == null) {
            return 0;
        }

        int sceneIndex = persistent.getSelectedScene();
        AnimationScene scene = persistent.getAnimation().getScenes().get(sceneIndex);
        AnimationScenePart part = scene.getSceneParts().get(selectedPart);
        return part.getTransformation().getRotation().getV()[0];
    }

    @Override
    public double currentYRotation() {
        AnimationPersistent persistent = model.getPersistent();
        if(persistent == null) {
            return 0;
        }

        String selectedPart = persistent.getSelectedPart();
        if(selectedPart == null) {
            return 0;
        }

        int sceneIndex = persistent.getSelectedScene();
        AnimationScene scene = persistent.getAnimation().getScenes().get(sceneIndex);
        AnimationScenePart part = scene.getSceneParts().get(selectedPart);
        return part.getTransformation().getRotation().getV()[1];
    }

    @Override
    public double currentZRotation() {
        AnimationPersistent persistent = model.getPersistent();
        if(persistent == null) {
            return 0;
        }

        String selectedPart = persistent.getSelectedPart();
        if(selectedPart == null) {
            return 0;
        }

        int sceneIndex = persistent.getSelectedScene();
        AnimationScene scene = persistent.getAnimation().getScenes().get(sceneIndex);
        AnimationScenePart part = scene.getSceneParts().get(selectedPart);
        return part.getTransformation().getRotation().getV()[2];
    }
}
