package hu.csega.editors.anm.layer1.swing.components.connectjoints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

class ConnectionTreeEnumeration implements Enumeration<TreeNode> {

    private List<TreeNode> nodes;
    private int currentIndex;

    ConnectionTreeEnumeration() {
        this.nodes = new ArrayList<>();
    }

    ConnectionTreeEnumeration(Collection<? extends TreeNode> nodes) {
        if(nodes == null || nodes.isEmpty()) {
            this.nodes = new ArrayList<>();
        } else {
            int size = nodes.size();
            this.nodes = new ArrayList<>(size);
            this.nodes.addAll(nodes);
        }
    }

    @Override
    public boolean hasMoreElements() {
        return currentIndex < nodes.size();
    }

    @Override
    public TreeNode nextElement() {
        return nodes.get(currentIndex++);
    }
}
