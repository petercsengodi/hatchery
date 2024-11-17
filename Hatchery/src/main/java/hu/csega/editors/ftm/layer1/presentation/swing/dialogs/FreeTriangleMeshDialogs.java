package hu.csega.editors.ftm.layer1.presentation.swing.dialogs;

import javax.swing.*;

public class FreeTriangleMeshDialogs {

    private final JFrame parent;

    public final FreeTriangleMeshSphereDialog spheres;

    public FreeTriangleMeshDialogs(JFrame parent) {
        this.parent = parent;

        spheres = new FreeTriangleMeshSphereDialog(parent);
    }

}
