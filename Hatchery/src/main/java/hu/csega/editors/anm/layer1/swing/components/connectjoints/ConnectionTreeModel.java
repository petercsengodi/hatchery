package hu.csega.editors.anm.layer1.swing.connect;

import hu.csega.editors.anm.layer4.data.model.AnimatorModel;
import hu.csega.games.units.UnitStore;

import java.util.ArrayList;
import java.util.List;

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
        for(int i = 0; i < 10; i++) {
            ConnectionTreeMesh mesh = new ConnectionTreeMesh(root);
            List<ConnectionTreeJoint> joints = new ArrayList<>();

            for(int j = 0; j < 10; j++) {
                joints.add(new ConnectionTreeJoint(mesh));
            }

            mesh.setJoints(joints);
            meshes.add(mesh);
        }

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
}
