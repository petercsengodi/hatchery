package hu.csega.editors.ftm.layer1.presentation.swing.view;

public class FreeTriangleMeshPictogram {

    public static final int UP_ARROW = 0;
    public static final int RIGHT_ARROW = 1;
    public static final int DOWN_ARROW = 2;
    public static final int LEFT_ARROW = 3;
    public static final int UP_RIGHT_ARROW = 4;
    public static final int DOWN_RIGHT_ARROW = 5;
    public static final int DOWN_LEFT_ARROW = 6;
    public static final int UP_LEFT_ARROW = 7;
    public static final int X_DELETE = 8;
    public static final int RESIZE_OUTWARDS = 9;
    public static final int ROTATE_COUNTER_CLOCKWISE = 10;
    public static final int ROTATE_CLOCKWISE = 11;

    public FreeTriangleMeshPictogram() {
    }

    public FreeTriangleMeshPictogram(int action, double x, double y) {
        this.action = action;
        this.x = x;
        this.y = y;
    }

    public int action;
    public double x;
    public double y;

}
