package hu.csega.editors.anm.layer1.swing.components.connectjoints;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

class ConnectionTreeJoint implements TreeNode {

    private ConnectionTreeMesh parent;

    ConnectionTreeJoint(ConnectionTreeMesh parent) {
        this.parent = parent;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration children() {
        return new ConnectionTreeEnumeration();
    }
}
