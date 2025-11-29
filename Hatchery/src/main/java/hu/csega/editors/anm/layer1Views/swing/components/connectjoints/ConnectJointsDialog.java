package hu.csega.editors.anm.layer1Views.swing.components.connectjoints;

import hu.csega.editors.anm.layer4Data.model.AnimatorModel;
import hu.csega.games.units.UnitStore;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class ConnectJointsDialog extends JDialog implements ComponentListener {

    private AnimatorModel animatorModel;

    private ConnectionTreeModel model;
    private JTree tree;

    public ConnectJointsDialog(JFrame parent) {
        super(parent, "Select a joint to connectjoints selected part!");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.addComponentListener(this);
        this.setModal(true);

        this.model = new ConnectionTreeModel();

        this.tree = new JTree(model);
        this.tree.setEditable(false);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        JScrollPane scroller = new JScrollPane(tree);
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scroller, BorderLayout.CENTER);

        JButton ok = new JButton("OK");
        ok.addActionListener(event -> okAction());

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(event -> cancelAction());

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(ok);
        buttons.add(cancel);
        contentPane.add(buttons, BorderLayout.SOUTH);

        this.pack();
    }

    private void okAction() {
        TreePath path = tree.getSelectionPath();
        if(path == null) {
            return;
        }

        Object selected = path.getLastPathComponent();
        if(!(selected instanceof ConnectionTreeJoint)) {
            return;
        }

        setVisible(false);
        componentHidden(null);

        if(animatorModel == null) {
            animatorModel = UnitStore.instance(AnimatorModel.class);
        }

        String identifier = ((ConnectionTreeJoint) selected).getIdentifier();
        animatorModel.connectSelectedPart(identifier);
    }

    private void cancelAction() {
        setVisible(false);
        componentHidden(null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 800);
    }

    public void showConnectJointsDialog() {
        model.update();
        tree.updateUI();
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
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        model.clear();
    }

    private static final long serialVersionUID = 1L;
}
