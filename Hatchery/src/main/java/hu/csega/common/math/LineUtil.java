package hu.csega.common.math;

public class LineUtil {

    public static double overOrUnderLine(double px1, double py1, double px2, double py2, double tx, double ty) {
        double xDiff = px2 - px1;
        double yDiff = py2 - py1;
        double xAbsDiff = Math.abs(xDiff);
        double yAbsDiff = Math.abs(yDiff);
        boolean xNear = xAbsDiff < ScalarUtil.EPSILON;
        boolean yNear = yAbsDiff < ScalarUtil.EPSILON;

        // Checking the trivial case when line is actually a point.
        if(xNear && yNear) {
            return 0.0; // TODO: Is this good?
        }

        // Which direction to test? X or Y?
        if(xNear || xAbsDiff < yAbsDiff) {
            double m = xDiff / yDiff; // <x> = m * (<y> - py1) + px1
            double v = m * (ty - py1) + px1;
            return tx - v;
        } else {
            double m = yDiff / xDiff; // <y> = m * (<x> - px1) + py1
            double v = m * (tx - px1) + py1;
            return ty - v;
        }
    }
}
