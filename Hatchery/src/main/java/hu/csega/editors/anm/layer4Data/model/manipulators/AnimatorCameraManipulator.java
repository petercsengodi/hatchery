package hu.csega.editors.anm.layer4Data.model.manipulators;

import hu.csega.editors.anm.common.CommonEditorModel;
import hu.csega.editors.anm.layer1Views.opengl.AnimatorMouseController;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.editors.anm.layer4Data.model.AnimatorRefreshViews;
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
        CommonEditorModel commonEditorModel = model.selectModel();

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

        float px = (float)(Math.cos(alfa) * distanceReduced);
        float py = (float) y;
        float pz = (float)(Math.sin(alfa) * distanceReduced);
        float pw = 1f;

        commonEditorModel.setCameraPosition(px, py, pz, pw);
    }

}
