package hu.csega.editors.ftm.layer1.presentation.swing.menu;

import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshCanvas;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshSideView;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.units.UnitStore;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FreeTriangleMeshContextMenu extends JPopupMenu {

    private FreeTriangleMeshCanvas parent;

    public FreeTriangleMeshContextMenu(FreeTriangleMeshCanvas parent) {
        this.parent = parent;

        final FreeTriangleMeshSideView sideView;
        if(parent instanceof FreeTriangleMeshSideView)
            sideView = (FreeTriangleMeshSideView) parent;
        else
            sideView = null;

        if(sideView != null) {
            JMenuItem createNewVertex = new JMenuItem("Create new vertex (CTRL + left button)");
            this.add(createNewVertex);
            createNewVertex.addActionListener(e -> sideView.createVertexAtXY(x, y));
        }

        JMenuItem createNewTriangle = new JMenuItem("Create new triangle (T)");
        this.add(createNewTriangle);
        JMenuItem reverseTriangle = new JMenuItem("Reverse triangle (R)");
        this.add(reverseTriangle);
        JMenuItem nextTriangle = new JMenuItem("Next triangle (N)");
        this.add(nextTriangle);
        JMenuItem mergeVertices = new JMenuItem("Merge vertices (M)");
        this.add(mergeVertices);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private int x;
    private int y;

    private static final long serialVersionUID = 1L;
}
