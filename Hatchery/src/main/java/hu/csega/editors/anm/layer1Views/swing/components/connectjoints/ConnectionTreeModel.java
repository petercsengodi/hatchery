package hu.csega.editors.anm.layer1Views.swing.components.connectjoints;

import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.library.animation.v1.anm.AnimationPart;
import hu.csega.games.library.animation.v1.anm.AnimationPartJoint;
import hu.csega.games.library.animation.v1.anm.AnimationPersistent;
import hu.csega.games.units.UnitStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class ConnectionTreeModel implements TreeModel {

    private AnimatorModel animatorModel;
    private ConnectionTreeRoot root;

    public void update() {
        if(animatorModel == null) {
            animatorModel = UnitStore.instance(AnimatorModel.class);
        }

        if(root == null) {
            root = new ConnectionTreeRoot();
        }

        clear();

        List<ConnectionTreeMesh> meshes = new ArrayList<>();

        AnimationPersistent persistent = animatorModel.getPersistent();
        Animation animation = persistent.getAnimation();
        collectMeshes(animation, meshes);

        root.setMeshes(meshes);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((TreeNode)parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((TreeNode)parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((TreeNode)node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((TreeNode)parent).getIndex((TreeNode) child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }

    public void clear() {
        if(root != null) {
            root.clearChildren();
        }
    }

    private void collectMeshes(Animation animation, List<ConnectionTreeMesh> meshes) {
        Map<String, AnimationPart> parts = animation.getParts();
        if(parts == null || parts.isEmpty()) {
            return;
        }

        for(Map.Entry<String, AnimationPart> entry : parts.entrySet()) {
            String identifier = String.valueOf(entry.getKey());
            AnimationPart part = entry.getValue();
            String label = part.getDisplayName();
            ConnectionTreeMesh mesh = new ConnectionTreeMesh(root, identifier, label);

            List<ConnectionTreeJoint> joints = new ArrayList<>();
            List<AnimationPartJoint> partJoints = part.getJoints();
            for(AnimationPartJoint joint : partJoints) {
                String id = String.valueOf(joint.getIdentifier());
                String dn = joint.getDisplayName();
                joints.add(new ConnectionTreeJoint(mesh, id, dn));
            }

            mesh.setJoints(joints);
            meshes.add(mesh);
        }
    }

}
