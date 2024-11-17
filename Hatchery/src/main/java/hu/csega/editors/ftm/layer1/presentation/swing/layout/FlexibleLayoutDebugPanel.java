package hu.csega.editors.ftm.layer1.presentation.swing.layout;

import java.awt.*;

import javax.swing.*;

public class FlexibleLayoutDebugPanel extends JPanel {

    private static final Color OVERLAY_COLOR = new Color(128, 128, 128, 20);

    @Override
    public void paint(Graphics g) {
        update(g);
    }

    @Override
    public void update(Graphics g) {
        Color old = g.getColor();
        g.setColor(OVERLAY_COLOR);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(old);
    }

}
