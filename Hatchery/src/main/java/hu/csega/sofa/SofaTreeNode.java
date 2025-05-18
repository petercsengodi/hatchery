package hu.csega.sofa;

import java.util.Set;
import java.util.TreeSet;

public class SofaTreeNode implements Comparable<SofaTreeNode> {

    public SofaTreeNode() {
        this.code = SofaWordTree.STOP;
        this.children = null;
    }

    public SofaTreeNode(int code) {
        this.code = code;
        this.children = new TreeSet<>();
    }

    public final int code;
    public Set<SofaTreeNode> children;

    @Override
    public int compareTo(SofaTreeNode o) {
        if(this.code == SofaWordTree.STOP) {
            if(o.code == SofaWordTree.STOP)
                return 0;
            else
                return -1;
        } else if(o.code == SofaWordTree.STOP) {
            return 1;
        } else {
            return Integer.compare(this.code, o.code);
        }
    }

    @Override
    public int hashCode() {
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SofaTreeNode)
            return this.code == ((SofaTreeNode)obj).code;
        else
            return false;
    }
}
