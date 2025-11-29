package hu.csega.editors.anm.layer4Data.model.manipulators;

import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.editors.anm.layer4Data.model.AnimatorRefreshViews;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationMisc;
import hu.csega.games.units.Dependency;

public class AnimatorFileManipulator {

    private AnimatorModel model;
    private AnimatorRefreshViews refreshViews;

    @Dependency
    public void dependencies(AnimatorModel model, AnimatorRefreshViews refreshViews) {
        this.model = model;
        this.refreshViews = refreshViews;
    }

    public void loadAnimation(String filename, Animation animation) {
        AnimationMisc misc = new AnimationMisc();
        misc.setFilename(filename);

        AnimationPersistent persistent = new AnimationPersistent();
        persistent.setAnimation(animation);
        persistent.setName(filename);
        persistent.setMisc(misc);
        model.setPersistent(persistent);
    }

}
