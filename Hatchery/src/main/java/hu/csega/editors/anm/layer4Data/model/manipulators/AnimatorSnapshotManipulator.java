package hu.csega.editors.anm.layer4Data.model.manipulators;

import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.editors.anm.layer4Data.model.AnimationSnapshot;
import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.editors.anm.layer4Data.model.AnimatorRefreshViews;
import hu.csega.editors.common.SerializationUtil;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.units.Dependency;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AnimatorSnapshotManipulator {

    private AnimatorModel model;
    private AnimatorRefreshViews refreshViews;

    private List<AnimationSnapshot> previousStates;
    private List<AnimationSnapshot> nextStates;

    public AnimatorSnapshotManipulator() {
        this.previousStates = new ArrayList<>();
        this.nextStates = new ArrayList<>();
    }

    @Dependency
    public void dependencies(AnimatorModel model, AnimatorRefreshViews refreshViews) {
        this.model = model;
        this.refreshViews = refreshViews;
    }

    public void clear() {
        previousStates.clear();
        nextStates.clear();
    }

    public void createNewSnapshot() {
        AnimationPersistent persistent = model.getPersistent();
        Animation animation = persistent.getAnimation();
        AnimationSnapshot snapshot = new AnimationSnapshot(SerializationUtil.serialize(animation));
        previousStates.add(snapshot);
        nextStates.clear();
    }

    public void undo() {
        if(previousStates.isEmpty())
            return;

        AnimationPersistent persistent = model.getPersistent();
        AnimationSnapshot snapshot = previousStates.remove(previousStates.size() - 1);
        nextStates.add(snapshot);
        persistent.setAnimation(SerializationUtil.deserialize(snapshot.getBytes(), Animation.class));

        refreshViews.refreshAll();
    }

    public void redo() {
        if(nextStates.isEmpty())
            return;

        AnimationPersistent persistent = model.getPersistent();
        AnimationSnapshot snapshot = nextStates.remove(nextStates.size() - 1);
        previousStates.add(snapshot);
        persistent.setAnimation(SerializationUtil.deserialize(snapshot.getBytes(), Animation.class));

        refreshViews.refreshAll();
    }

    public void changeJSON(String text) {
        // FIXME create new objects from JSON representation
        // Animation animation = new Animation(text);
        logger.error("Implementation is not done, yet!");

        AnimationPersistent persistent = model.getPersistent();
        Animation animation = persistent.getAnimation();
        AnimationSnapshot snapshot = new AnimationSnapshot(SerializationUtil.serialize(animation));
        previousStates.add(snapshot);
        nextStates.clear();

        refreshViews.refreshAll();
    }

    private static final Logger logger = LoggerFactory.createLogger(AnimatorSnapshotManipulator.class);

}
