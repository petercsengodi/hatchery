package hu.csega.editors.anm.layer1.swing.connect;

import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

public class ConnectJointsDialog extends JFrame implements ComponentListener {

    private ConnectionTreeModel model;
    private JScrollPane scroller;
    private JTree tree;

    public ConnectJointsDialog() {
        super("Select a joint to connect selected part!");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.addComponentListener(this);

        model = new ConnectionTreeModel();

        tree = new JTree(model);
        scroller = new JScrollPane(tree);
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scroller, BorderLayout.CENTER);

        JButton ok = new JButton("OK");
        ok.addActionListener(event -> setVisible(false));

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(event -> setVisible(false));

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(ok);
        buttons.add(cancel);
        contentPane.add(buttons, BorderLayout.SOUTH);

        this.pack();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 800);
    }

    public void showConnectJointsDialog() {
        model.update();
        tree.updateUI();
        scroller.updateUI();
        setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {
        logger.info("Shown.");
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        model.clear();
    }

    private static final Logger logger = LoggerFactory.createLogger(ConnectJointsDialog.class);

    private static final long serialVersionUID = 1L;
}
