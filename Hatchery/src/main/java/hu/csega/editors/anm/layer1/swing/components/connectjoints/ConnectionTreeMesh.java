package hu.csega.editors.anm.layer1.swing.components.connectjoints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

class ConnectionTreeMesh implements TreeNode {

    private TreeNode parent;
    private List<ConnectionTreeJoint> joints;

    ConnectionTreeMesh(TreeNode parent) {
        this.parent = parent;
    }

    void setJoints(Collection<ConnectionTreeJoint> joints) {
        this.joints = new ArrayList<>();
        if(joints != null) {
            this.joints.addAll(joints);
        }
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return joints.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return joints.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return joints.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Enumeration children() {
        return new ConnectionTreeEnumeration(joints);
    }
}
