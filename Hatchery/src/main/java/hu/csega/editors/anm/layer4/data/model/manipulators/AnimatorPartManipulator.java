package hu.csega.editors.anm.layer4.data.model.manipulators;

import hu.csega.games.library.animation.v1.anm.AnimationDetailedTransformation;
import hu.csega.games.library.animation.v1.anm.AnimationPartJoint;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.anm.layer4.data.model.AnimatorRefreshViews;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationScene;
import hu.csega.games.library.animation.v1.anm.AnimationScenePart;
import hu.csega.games.library.animation.v1.anm.AnimationTransformation;
import hu.csega.games.library.animation.v1.anm.AnimationVector;
import hu.csega.games.units.Dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

            Map<String, AnimationPart> parts = animation.getParts();
            if(parts == null) {
                parts = new TreeMap<>();
                animation.setParts(parts);
            }

            String partIdentifier = "part:" + newIndex;
            AnimationPart part = new AnimationPart(partIdentifier);
            part.setMesh(filename);
            part.setBasicTransformation(new AnimationTransformation());
            part.setJoints(new ArrayList<>());

            parts.put(partIdentifier, part);
            animation.cleanUpScenes();
        }

        refreshViews.refreshAll();
    }

    public void selectPart(String identifier) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            if(persistent == null) {
                return;
            }

            persistent.setSelectedPart(identifier);
        }

        refreshViews.refreshAll();
    }

    public void flipSelectedPart(double x, double y, double z) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            if(persistent == null) {
                return;
            }

            String partIdentifier = persistent.getSelectedPart();
            if(partIdentifier == null) {
                return;
            }

            Animation animation = persistent.getAnimation();
            if(animation == null) {
                animation = new Animation();
                persistent.setAnimation(animation);
            }

            int selectedScene = persistent.getSelectedScene();
            List<AnimationScene> scenes = animation.getScenes();
            if(scenes == null || scenes.size() <= selectedScene) {
                return;
            }

            AnimationScene scene = scenes.get(selectedScene);
            Map<String, AnimationScenePart> sceneParts = scene.getSceneParts();
            if(sceneParts == null) {
                sceneParts = new HashMap<>();
                scene.setSceneParts(sceneParts);
            }

            AnimationScenePart scenePart = sceneParts.get(partIdentifier);
            if(scenePart == null) {
                scenePart = new AnimationScenePart();
                sceneParts.put(partIdentifier, scenePart);
            }

            AnimationDetailedTransformation transformation = scenePart.getTransformation();
            float[] flip = transformation.getFlip().getV();

            if(x < 0) {
                flip[0] = (flip[0] > 0f ? -1f : 1f);
            }

            if(y < 0) {
                flip[1] = (flip[1] > 0f ? -1f : 1f);
            }

            if(z < 0) {
                flip[2] = (flip[2] > 0f ? -1f : 1f);
            }
        }

        refreshViews.refreshAll();
    }

    public void addNewJoint(String name, double x, double y, double z) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            if(persistent == null) {
                return;
            }

            String partIdentifier = persistent.getSelectedPart();
            if(partIdentifier == null) {
                return;
            }

            Animation animation = persistent.getAnimation();
            if(animation == null) {
                animation = new Animation();
                persistent.setAnimation(animation);
            }

            AnimationPart animationPart = animation.getParts().get(partIdentifier);
            if(animationPart == null) {
                throw new RuntimeException("Missing part: " + partIdentifier);
            }

            int lastIndex = animation.getMaxPartIndex();
            int newIndex = lastIndex + 1;
            animation.setMaxPartIndex(newIndex);
            String jointIdentifier = partIdentifier + "-joint:" + newIndex;

            List<AnimationPartJoint> joints = animationPart.getJoints();
            if(joints == null) {
                joints = new ArrayList<>();
                animationPart.setJoints(joints);
            }

            AnimationPartJoint joint = new AnimationPartJoint(partIdentifier, jointIdentifier);
            joint.setDisplayName(name);
            joint.setRelativePosition(new AnimationVector((float)x, (float)y, (float)z));
            joint.setRelativeTransformation(new AnimationTransformation());
            joints.add(joint);
        }

        refreshViews.refreshAll();
    }
}
