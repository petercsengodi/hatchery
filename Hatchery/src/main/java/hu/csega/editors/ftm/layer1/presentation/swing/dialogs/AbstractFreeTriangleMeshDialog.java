package hu.csega.editors.ftm.layer1.presentation.swing.dialogs;

import hu.csega.editors.ftm.layer1.presentation.swing.layout.FlexibleLayoutManager;
import hu.csega.games.engine.env.Disposable;
import hu.csega.games.engine.env.Environment;
import hu.csega.games.units.UnitStore;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.*;

import javax.swing.*;

public class AbstractFreeTriangleMeshDialog extends JFrame implements Disposable {

    private final JFrame parent;
    private final FlexibleLayoutManager layoutManager;
    private final Dimension preferredSize;

    protected AbstractFreeTriangleMeshDialog(String title, JFrame parent, int preferredWidth, int preferredHeight) {
        super(title);
        this.parent = parent;
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.layoutManager = new FlexibleLayoutManager(this.getContentPane());
        this.preferredSize = new Dimension(preferredWidth, preferredHeight);
        this.setLocationRelativeTo(null);

        Environment env = UnitStore.instance(Environment.class);
        env.registerForDisposing(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public FlexibleLayoutManager getFlexibleLayoutManager() {
        return layoutManager;
    }

    @Override
    public void dispose() {
        logger.info("Disposing dialog: " + this.getClass().getName());
        super.dispose();
    }

    private static final Logger logger = LoggerFactory.createLogger(AbstractFreeTriangleMeshDialog.class);
}
