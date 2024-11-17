package hu.csega.editors.ftm.layer1.presentation.swing.dialogs;

import hu.csega.editors.ftm.layer1.presentation.swing.layout.FlexibleLayoutManager;

import java.awt.*;

import javax.swing.*;

public class FreeTriangleMeshSphereDialog extends AbstractFreeTriangleMeshDialog {

    public FreeTriangleMeshSphereDialog(JFrame parent) {
        super("Create sphere", parent, 800, 600);

        FlexibleLayoutManager layoutManager = getFlexibleLayoutManager();
        layoutManager.addComponent(new JLabel("Hello!"), (containerWidth, containerHeight) -> {
            int left = containerWidth / 3;
            int top = containerHeight / 3;
            int width = 100;
            int height = 100;
            return new Rectangle(left, top, width, height);
        });

        this.pack();
    }

}
