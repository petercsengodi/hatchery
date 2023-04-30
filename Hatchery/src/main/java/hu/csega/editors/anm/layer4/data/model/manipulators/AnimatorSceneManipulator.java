package hu.csega.editors.anm.layer4.data.model.manipulators;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.anm.layer4.data.model.AnimatorRefreshViews;
import hu.csega.editors.common.SerializationUtil;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationDetailedTransformation;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.animation.v1.anm.AnimationScene;
import hu.csega.games.library.animation.v1.anm.AnimationScenePart;
import hu.csega.games.library.animation.v1.anm.AnimationVector;
import hu.csega.games.units.Dependency;

import java.io.Serializable;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Vector4f;

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

    public void lerp(int start, int end, int writeStart, int writeEnd) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            Animation animation = persistent.getAnimation();
            int numberOfScenes = animation.getNumberOfScenes();

            if(start < 0 || start >= numberOfScenes) {
                return;
            }

            if(end< 0 || end >= numberOfScenes) {
                return;
            }

            if(writeStart < 0 || writeStart >= writeEnd) {
                return;
            }

            if(writeEnd >= numberOfScenes) {
                return;
            }

            int length = writeEnd - writeStart;
            double delta = 1.0 / length;
            Set<String> partIdentifiers = animation.getParts().keySet();
            AnimationScene startScene = animation.createOrGetScene(start);
            AnimationScene endScene = animation.createOrGetScene(end);
            for(int i = 0; i <= length; i++) {
                float t = (float) (delta * i);
                AnimationScene scene = calculateScene(partIdentifiers, startScene, endScene, t);
                int sceneIndex = writeStart + i;
                animation.putScene(sceneIndex, scene);
            }
        }

        refreshViews.refreshAll();
    }

    private AnimationScene calculateScene(Set<String> partIdentifiers, AnimationScene startScene, AnimationScene endScene, float t) {
        AnimationScene resultScene = new AnimationScene();
        for(String partIdentifier : partIdentifiers) {
            AnimationScenePart startScenePart = startScene.createOrGetScenePart(partIdentifier);
            AnimationScenePart endScenePart = endScene.createOrGetScenePart(partIdentifier);
            AnimationScenePart resultScenePart = resultScene.createOrGetScenePart(partIdentifier);
            calculateScenePart(startScenePart, endScenePart, t, resultScenePart);
        }

        return resultScene;
    }

    private void calculateScenePart(AnimationScenePart startScenePart, AnimationScenePart endScenePart, float t, AnimationScenePart resultScenePart) {
        resultScenePart.setVisible(lerpBoolean(startScenePart.isVisible(), endScenePart.isVisible(), t));
        AnimationDetailedTransformation startTransformation = startScenePart.getTransformation();
        AnimationDetailedTransformation endTransformation = endScenePart.getTransformation();
        AnimationDetailedTransformation resultTransformation = resultScenePart.getTransformation();
        lerpTransformation(startTransformation, endTransformation, t, resultTransformation);
    }

    private void lerpTransformation(AnimationDetailedTransformation start, AnimationDetailedTransformation end, float t, AnimationDetailedTransformation result) {
        lerpVector(start.getRotation(), end.getRotation(), t, result.getRotation());
        lerpVector(start.getFlip(), end.getFlip(), t, result.getFlip());
        lerpVector(start.getScaling(), end.getScaling(), t, result.getScaling());
        lerpVector(start.getTranslation(), end.getTranslation(), t, result.getTranslation());
    }

    private void lerpVector(AnimationVector start, AnimationVector end, float t, AnimationVector result) {
        float[] v1 = start.getV();
        float[] v2 = end.getV();
        float[] v3 = result.getV();

        for(int i = 0; i < 4; i++) {
            v3[i] = lerpFloat(v1[i], v2[i], t);
        }
    }

    private boolean lerpBoolean(boolean start, boolean end, float t) {
        return (t < 0.5f ? start : end);
    }

    private float lerpFloat(float start, float end, float t) {
        float length = end - start;
        float delta = length * t;
        return start + delta;
    }

    private void lerpVector(Vector4f start, Vector4f end, float t, Vector4f result) {
        for(int i = 0; i < 4; i++) {
            float s = start.get(i);
            float e = end.get(i);
            float r = lerpFloat(s, e, t);
            result.setComponent(i, r);
        }
    }

    private void lerpMatrix(Matrix4f start, Matrix4f end, float t, Matrix4f result) {
        start.lerp(end, t, result);
    }

    private static <T extends Serializable> T deepCopyHack(T original) {
        return (T) (SerializationUtil.deserialize(SerializationUtil.serialize(original), original.getClass()));
    }

    private static final float E = 0.000001f;

}
