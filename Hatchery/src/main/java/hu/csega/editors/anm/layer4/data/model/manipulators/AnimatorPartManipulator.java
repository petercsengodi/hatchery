package hu.csega.editors.anm.layer4.data.model.manipulators;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.editors.anm.layer4.data.model.AnimatorRefreshViews;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationDetailedTransformation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPartJoint;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.library.animation.v1.anm.AnimationScene;
import hu.csega.games.library.animation.v1.anm.AnimationScenePart;
import hu.csega.games.library.animation.v1.anm.AnimationTransformation;
import hu.csega.games.library.animation.v1.anm.AnimationVector;
import hu.csega.games.units.Dependency;

import java.util.ArrayList;
import java.util.List;

public class AnimatorPartManipulator {

    private AnimatorModel model;
    private AnimatorRefreshViews refreshViews;

    @Dependency
    public void dependencies(AnimatorModel model, AnimatorRefreshViews refreshViews) {
        this.model = model;
        this.refreshViews = refreshViews;
    }

    public String currentPartIdentifier() {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            return persistent.getSelectedPart();
        }
    }

    public void addNewPart(String filename) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            Animation animation = persistent.getAnimation();

            int lastIndex = animation.getMaxPartIndex();
            int newIndex = lastIndex + 1;
            animation.setMaxPartIndex(newIndex);

            String partIdentifier = "part:" + newIndex;
            AnimationPart part = new AnimationPart(partIdentifier);
            part.setMesh(filename);
            part.setBasicTransformation(new AnimationTransformation());
            part.setJoints(new ArrayList<>());

            animation.getParts().put(partIdentifier, part);
            animation.cleanUpScenes();
        }

        refreshViews.refreshAll();
    }

    public void changePart(String filename) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            String partIdentifier = persistent.getSelectedPart();
            if(partIdentifier == null) {
                return;
            }

            Animation animation = persistent.getAnimation();
            AnimationPart part = animation.getParts().get(partIdentifier);
            part.setMesh(filename);
        }

        refreshViews.refreshAll();
    }

    public void selectPart(String identifier) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            persistent.setSelectedPart(identifier);
        }

        refreshViews.refreshAll();
    }

    public void flipSelectedPart(double x, double y, double z) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            String partIdentifier = persistent.getSelectedPart();
            if(partIdentifier == null) {
                return;
            }

            Animation animation = persistent.getAnimation();
            int selectedScene = persistent.getSelectedScene();
            AnimationScene scene = animation.createOrGetScene(selectedScene);
            if(scene == null) {
                return;
            }

            AnimationScenePart scenePart = scene.createOrGetScenePart(partIdentifier);
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

    public void rotateSelectedPart(double x, double y, double z) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            String partIdentifier = persistent.getSelectedPart();
            if(partIdentifier == null) {
                return;
            }

            Animation animation = persistent.getAnimation();
            int selectedScene = persistent.getSelectedScene();
            AnimationScene scene = animation.createOrGetScene(selectedScene);
            AnimationScenePart scenePart = scene.createOrGetScenePart(partIdentifier);
            AnimationDetailedTransformation transformation = scenePart.getTransformation();
            float[] rotation = transformation.getRotation().getV();
            rotation[0] += x;
            rotation[1] += y;
            rotation[2] += z;
        }

        refreshViews.refreshAll();
    }

    public void addNewJoint(String name, double x, double y, double z) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            String partIdentifier = persistent.getSelectedPart();
            if(partIdentifier == null) {
                return;
            }

            Animation animation = persistent.getAnimation();
            AnimationPart animationPart = animation.getParts().get(partIdentifier);
            if(animationPart == null) {
                throw new RuntimeException("Missing part: " + partIdentifier);
            }

            int lastIndex = animation.getMaxPartIndex();
            int newIndex = lastIndex + 1;
            animation.setMaxPartIndex(newIndex);
            String jointIdentifier = partIdentifier + "-joint:" + newIndex;

            List<AnimationPartJoint> joints = animationPart.getJoints();
            AnimationPartJoint joint = new AnimationPartJoint(partIdentifier, jointIdentifier);
            joint.setDisplayName(name);
            joint.setRelativePosition(new AnimationVector((float)x, (float)y, (float)z));
            joint.setRelativeTransformation(new AnimationTransformation());
            joints.add(joint);
        }

        refreshViews.refreshAll();
    }

    public void modifySelectedJoint(String name, double x, double y, double z) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            String partIdentifier = persistent.getSelectedPart();
            if(partIdentifier == null) {
                return;
            }

            Animation animation = persistent.getAnimation();
            AnimationPart animationPart = animation.getParts().get(partIdentifier);
            if(animationPart == null) {
                throw new RuntimeException("Missing part: " + partIdentifier);
            }

            String selectedJointIdentifier = persistent.getSelectedJoint();
            if(selectedJointIdentifier == null) {
                return;
            }

            List<AnimationPartJoint> joints = animationPart.getJoints();
            if(joints != null) {
                for(AnimationPartJoint joint : joints) {
                    if(selectedJointIdentifier.equals(joint.getIdentifier())) {
                        joint.setDisplayName(name);
                        joint.setRelativePosition(new AnimationVector((float)x, (float)y, (float)z));
                    }
                }
            }
        }

        refreshViews.refreshAll();
    }

    public void deleteSelectedJoint() {
    }

    public void connectSelectedPart(String jointIdentifier) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            String partIdentifier = persistent.getSelectedPart();
            Animation animation = persistent.getAnimation();
            animation.getConnections().put(partIdentifier, jointIdentifier);
        }

        refreshViews.refreshAll();
    }

    public void changeSelectedPartName(String name) {
        synchronized (model) {
            AnimationPersistent persistent = model.getPersistent();
            String partIdentifier = persistent.getSelectedPart();
            if(partIdentifier == null) {
                return;
            }

            Animation animation = persistent.getAnimation();
            AnimationPart animationPart = animation.getParts().get(partIdentifier);
            if(animationPart == null) {
                throw new RuntimeException("Missing part: " + partIdentifier);
            }

            animationPart.setDisplayName(name);
        }

        refreshViews.refreshAll();
    }

}
