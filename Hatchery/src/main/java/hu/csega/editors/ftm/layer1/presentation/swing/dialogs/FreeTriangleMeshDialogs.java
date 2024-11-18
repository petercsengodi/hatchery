package hu.csega.editors.ftm.layer1.presentation.swing.dialogs;

import javax.swing.*;

public class FreeTriangleMeshDialogs {

    public final FreeTriangleMeshSphereDialog spheres;

    public FreeTriangleMeshDialogs(JFrame parent) {
        spheres = new FreeTriangleMeshSphereDialog(parent);
    }

}
