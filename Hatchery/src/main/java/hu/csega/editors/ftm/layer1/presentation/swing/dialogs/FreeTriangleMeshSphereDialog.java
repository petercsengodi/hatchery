package hu.csega.editors.ftm.layer1.presentation.swing.dialogs;

import hu.csega.editors.ftm.layer1.presentation.swing.layout.FlexibleLayoutManager;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshModel;
import hu.csega.games.units.UnitStore;

import java.awt.*;

import javax.swing.*;

public class FreeTriangleMeshSphereDialog extends AbstractFreeTriangleMeshDialog {

    private final JLabel densityLabel = new JLabel("Density:");
    private final JTextField densityField = new JTextField();
    private final JLabel recommendationLabel = new JLabel(" (4-10 recommended)");
    private final JButton generateSphereButton = new JButton("Generate Sphere!");
    private final JLabel errorMessage = new JLabel("");

    public FreeTriangleMeshSphereDialog(JFrame parent) {
        super("Create sphere", parent, 800, 600);

        FlexibleLayoutManager layoutManager = getFlexibleLayoutManager();
        layoutManager.addComponent(densityLabel, (containerWidth, containerHeight) -> new Rectangle(10, 10, 100, 20));
        layoutManager.addComponent(densityField, (containerWidth, containerHeight) -> new Rectangle(110, 10, 100, 20));
        layoutManager.addComponent(recommendationLabel, (containerWidth, containerHeight) -> new Rectangle(210, 10, 100, 20));
        layoutManager.addComponent(generateSphereButton, (containerWidth, containerHeight) -> new Rectangle(10, 30, 300, 20));
        layoutManager.addComponent(errorMessage, (containerWidth, containerHeight) -> new Rectangle(10, 50, 500, 20));

        generateSphereButton.addActionListener(event -> {
            String text = densityField.getText();
            if(text == null) {
                densityField.setText("");
                errorMessage.setText("Enter valid integer!");
            } else {
                text = text.trim();
                if(text.isEmpty()) {
                    errorMessage.setText("Enter valid integer!");
                } else {
                    try {
                        int density = Integer.parseInt(text);
                        errorMessage.setText("");
                        generateSphere(density); // Will invalidate everything.
                        this.setVisible(false);
                    } catch(NumberFormatException ex) {
                        densityField.setText(null);
                        errorMessage.setText("Enter valid number!");
                    }
                }
            }
        });

        this.pack();
    }

    public void generateSphere(int density) {
        GameEngineFacade facade = UnitStore.instance(GameEngineFacade.class);
        FreeTriangleMeshModel model = (FreeTriangleMeshModel) facade.model();

        // FIXME: width and height
        model.createBasicSphere(30.0, 30.0, 30.0, density);

        facade.window().repaintEverything();
    }

}
