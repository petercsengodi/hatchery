package hu.csega.editors.anm.layer4.data.model.manipulators;

import hu.csega.editors.anm.layer1.opengl.AnimatorMouseController;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.anm.layer4.data.model.AnimatorRefreshViews;
import hu.csega.games.library.animation.v1.anm.AnimationMisc;
import hu.csega.games.library.animation.v1.anm.AnimationPlacement;
import hu.csega.games.library.animation.v1.anm.AnimationVector;
import hu.csega.games.units.Dependency;

public class AnimatorCameraManipulator {

    private AnimatorModel model;
    private AnimatorRefreshViews refreshViews;

    @Dependency
    public void dependencies(AnimatorModel model, AnimatorRefreshViews refreshViews) {
        this.model = model;
        this.refreshViews = refreshViews;
    }

    public void refreshCamera(AnimatorMouseController mouseController) {
        AnimationPersistent persistent = model.getPersistent();
        AnimationMisc misc = persistent.getMisc();
        AnimationPlacement camera = misc.getCamera();
        AnimationVector position = camera.getPosition();

        double alfa = 0.0;
        double beta = 0.0;
        double distance = 100.0;

        if(mouseController != null) {
            double scaling = mouseController.getScaling();
            distance *= scaling;
            alfa = mouseController.getAlfa();
            beta = mouseController.getBeta();
        }

        double y = distance * Math.sin(beta);
        double distanceReduced = distance * Math.cos(beta);

        float[] p = position.getV();

        p[0] = (float)(Math.cos(alfa) * distanceReduced);
        p[1] = (float) y;
        p[2] = (float)(Math.sin(alfa) * distanceReduced);
        p[3] = 1f;
    }

}
