package hu.csega.editors.anm.layer1.swing.components.connectjoints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

class ConnectionTreeRoot implements TreeNode {

    private List<ConnectionTreeMesh> meshes;

    void setMeshes(Collection<ConnectionTreeMesh> meshes) {
        this.meshes = new ArrayList<>();
        if(meshes != null && !meshes.isEmpty()) {
            this.meshes.addAll(meshes);
        }
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return meshes.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return meshes.size();
    }

    @Override
    public TreeNode getParent() {
        return null;
    }

    @Override
    public int getIndex(TreeNode node) {
        return meshes.indexOf(node);
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
        return new ConnectionTreeEnumeration(meshes);
    }

    public void clearChildren() {
        this.meshes = null;
    }

    @Override
    public String toString() {
        return "All Meshes";
    }

}
