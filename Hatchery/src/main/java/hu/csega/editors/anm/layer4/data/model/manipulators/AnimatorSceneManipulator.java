package hu.csega.editors.anm.layer4.data.model.manipulators;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.anm.layer4.data.model.AnimatorRefreshViews;
import hu.csega.editors.common.SerializationUtil;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.animation.v1.anm.AnimationScene;
import hu.csega.games.units.Dependency;

import java.io.Serializable;

public class AnimatorSceneManipulator {

    private AnimatorModel model;
    private AnimatorRefreshViews refreshViews;

    @Dependency
    public void dependencies(AnimatorModel model, AnimatorRefreshViews refreshViews) {
        this.model = model;
        this.refreshViews = refreshViews;
    }

    public void changeNumberOfScenes(int numberOfScenes) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            Animation animation = persistent.getAnimation();
            animation.setNumberOfScenes(numberOfScenes);
            int updated = animation.getNumberOfScenes();

            int selectedScene = persistent.getSelectedScene();
            if(selectedScene >= updated) {
                persistent.setSelectedScene(updated - 1);
            }
        }

        refreshViews.refreshAll();
    }

    public void selectScene(int sceneIndex) {
        synchronized (model) {
            if(sceneIndex < 0) {
                sceneIndex = 0;
            }

            AnimationPersistent persistent = model.getPersistent();
            int numberOfScenes = persistent.getAnimation().getNumberOfScenes();
            if(sceneIndex >= numberOfScenes) {
                sceneIndex = numberOfScenes - 1;
            }

            persistent.setSelectedScene(sceneIndex);
        }

        refreshViews.refreshAll();
    }

    public void previousScene() {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            int currentScene = persistent.getSelectedScene();
            if(currentScene > 0) {
                persistent.setSelectedScene(currentScene-1);
            }
        }

        refreshViews.refreshAll();
    }

    public void nextScene() {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            int numberOfScenes = persistent.getAnimation().getNumberOfScenes();
            int currentScene = persistent.getSelectedScene();
            if (currentScene < numberOfScenes - 1) {
                persistent.setSelectedScene(currentScene + 1);
            }
        }

        refreshViews.refreshAll();
    }

    public void copySelectedSceneFrom(int otherSceneIndex) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            Animation animation = persistent.getAnimation();
            int numberOfScenes = animation.getNumberOfScenes();
            if(otherSceneIndex < 0 || otherSceneIndex >= numberOfScenes) {
                return;
            }

            int selectedScene = persistent.getSelectedScene();
            if(selectedScene == otherSceneIndex) {
                return;
            }

            AnimationScene sceneToCopy = animation.createOrGetScene(otherSceneIndex);
            AnimationScene copiedScene = deepCopyHack(sceneToCopy);
            animation.putScene(selectedScene, copiedScene);
        }

        refreshViews.refreshAll();
    }

    public void copyFromThePrevious() {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            int selectedScene = persistent.getSelectedScene();
            if(selectedScene < 1) {
                return;
            }

            Animation animation = persistent.getAnimation();
            AnimationScene sceneToCopy = animation.createOrGetScene(selectedScene - 1);
            AnimationScene copiedScene = deepCopyHack(sceneToCopy);
            animation.putScene(selectedScene, copiedScene);
        }

        refreshViews.refreshAll();
    }

    private static <T extends Serializable> T deepCopyHack(T original) {
        return (T) (SerializationUtil.deserialize(SerializationUtil.serialize(original), original.getClass()));
    }
}
