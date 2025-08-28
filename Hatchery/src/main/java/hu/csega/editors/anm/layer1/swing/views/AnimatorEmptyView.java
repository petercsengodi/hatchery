package hu.csega.editors.anm.layer1.swing.views;

import hu.csega.editors.common.lens.EditorPoint;
import hu.csega.editors.ftm.layer1.presentation.swing.view.FreeTriangleMeshPictogram;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.library.mesh.v1.ftm.FreeTriangleMeshVertex;

import java.awt.*;
import java.util.Set;

public class AnimatorEmptyView extends AnimatorView {

    public AnimatorEmptyView(GameEngineFacade facade, AnimatorViewCanvas canvas) {
        super(facade, canvas);
    }

    @Override
    public String label() {
        return "nothing";
    }

    @Override
    protected EditorPoint transformToScreen(EditorPoint p) {
        return p;
    }

    @Override
    protected EditorPoint transformToModel(double x, double y) {
        return new EditorPoint(x, y, 0.0, 0.0);
    }

    @Override
    protected void translate(double x, double y) {
    }

    @Override
    protected void selectAll(EditorPoint topLeft, EditorPoint bottomRight, boolean add) {
    }

    @Override
    protected void selectFirst(EditorPoint p, double radius, boolean add) {
    }

    @Override
    protected void moveSelected(EditorPoint p1, EditorPoint p2) {
    }

    @Override
    protected void pictogramAction(int action, int dx, int dy, EditorPoint started, EditorPoint ended, Rectangle selection) {
    }

    @Override
    protected void paintView(Graphics2D g, int width, int height) {
    }

    @Override
    protected void generatePictograms(int numberOfSelectedItems, int selectionMinX, int selectionMinY, int selectionMaxX, int selectionMaxY, Set<FreeTriangleMeshPictogram> pictograms) {
    }
}
