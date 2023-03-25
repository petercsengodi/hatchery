package hu.csega.editors.anm.layer4.data.model.manipulators;

import hu.csega.editors.anm.layer4.data.model.AnimationPersistent;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.anm.layer4.data.model.AnimatorRefreshViews;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPartJoint;
import hu.csega.games.library.animation.v1.anm.AnimationTransformation;
import hu.csega.games.units.Dependency;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class AnimatorPartManipulator {

    private AnimatorModel model;
    private AnimatorRefreshViews refreshViews;

    @Dependency
    public void dependencies(AnimatorModel model, AnimatorRefreshViews refreshViews) {
        this.model = model;
        this.refreshViews = refreshViews;
    }

    public void addNewPart(String filename) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            if(persistent == null) {
                persistent = new AnimationPersistent();
                model.setPersistent(persistent);
            }

            Animation animation = persistent.getAnimation();
            if(animation == null) {
                animation = new Animation();
                persistent.setAnimation(animation);
            }

            int lastIndex = animation.getMaxPartIndex();
            int newIndex = lastIndex + 1;
            animation.setMaxPartIndex(newIndex);

            Map<Integer, AnimationPart> parts = animation.getParts();
            if(parts == null) {
                parts = new TreeMap<>();
                animation.setParts(parts);
            }

            AnimationPart part = new AnimationPart();
            part.setMesh(filename);
            part.setBasicTransformation(new AnimationTransformation());
            part.setJoints(new ArrayList<>());

            parts.put(newIndex, part);
            animation.cleanUpScenes();
        }

        refreshViews.refreshAll();
    }

}
