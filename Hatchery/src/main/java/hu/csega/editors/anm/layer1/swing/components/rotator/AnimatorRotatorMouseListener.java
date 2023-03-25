package hu.csega.editors.anm.layer1.swing.components.rotator;

import hu.csega.editors.common.lens.EditorLensPipeline;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class AnimatorRotatorMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {

    public static final double[] ZOOM_VALUES = { 0.0001, 0.001, 0.01, 0.1, 0.2, 0.3, 0.5, 0.75, 1.0, 1.25, 1.50, 2.0, 3.0, 4.0, 5.0, 10.0, 100.0 };
    public static final int DEFAULT_ZOOM_INDEX = 8;

    private boolean mouseLeftPressed = false;
    private boolean mouseRightPressed = false;
    private Point mouseLeftAt = new Point(0, 0);
    private Point mouseRightAt = new Point(0, 0);

    private boolean selectionBoxEnabled = false;
    private Point selectionStart = new Point();
    private Point selectionEnd = new Point();
    private Rectangle selectionBox = new Rectangle();

    protected EditorLensPipeline lenses = new EditorLensPipeline();
    protected int zoomIndex = DEFAULT_ZOOM_INDEX;


    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY());

        if(mouseLeftPressed) {
            int dx = mouseLeftAt.x - p.x;
            int dy = mouseLeftAt.y - p.y;
            if(selectionBoxEnabled) {
                selectionEnd.x -= dx;
                selectionEnd.y -= dy;
                // repaint();
            } else if(e.isControlDown()) {
                // moveSelected(-dx, -dy);
                // repaintEverything();
            }
            mouseLeftAt.x = p.x;
            mouseLeftAt.y = p.y;
        }

        if(mouseRightPressed) {
            int dx = p.x - mouseRightAt.x;
            int dy = p.y - mouseRightAt.y;
            // translate(dx, dy);
            // repaint();
            mouseRightAt.x = p.x;
            mouseRightAt.y = p.y;
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY());

        if(mouseRightPressed) {
            // translate(p.x - mouseRightAt.x, p.y - mouseRightAt.y);
            mouseRightAt.x = p.x;
            mouseRightAt.y = p.y;
            // repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == 1) {
            mouseLeftPressed = true;
            mouseLeftAt = new Point(e.getX(), e.getY());
            if(!e.isControlDown()) {
                selectionBoxEnabled = true;
                selectionStart.x = selectionEnd.x = mouseLeftAt.x;
                selectionStart.y = selectionEnd.y = mouseLeftAt.y;
            }
        }

        if(e.getButton() == 3) {
            mouseRightPressed = true;
            mouseRightAt = new Point(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == 1) {
            mouseLeftPressed = false;
            if(selectionBoxEnabled) {
                // calculateSelectionBox();
                // EditorPoint p1 = transformToModel(selectionStart.x, selectionStart.y);
                // EditorPoint p2 = transformToModel(selectionEnd.x, selectionEnd.y);
                // selectAll(p1.getX(), p1.getY(), p2.getX(), p2.getY(), e.isShiftDown());
                selectionBoxEnabled = false;
                //repaintEverything();
            }

            // FreeTriangleMeshModel model = getModel();
            // model.finalizeMove();
        } else if(e.getButton() == 3) {
            mouseRightPressed = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            if(e.isControlDown()) {
                // create new vertex
                // EditorPoint p = transformToModel(e.getX(), e.getY());
                // createVertexAt(p.getX(), p.getY());
                // repaintEverything();
            } else {
                // select one vertex
                // EditorPoint p = transformToModel(e.getX(), e.getY());
                // selectFirst(p.getX(), p.getY(), 5, e.isShiftDown());
                // repaintEverything();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int numberOfRotations = e.getWheelRotation();
        zoomIndex += numberOfRotations;
        if(zoomIndex < 0)
            zoomIndex = 0;
        else if(zoomIndex >= ZOOM_VALUES.length)
            zoomIndex = ZOOM_VALUES.length - 1;
        lenses.setScale(ZOOM_VALUES[zoomIndex]);
        // repaint();
    }

}
