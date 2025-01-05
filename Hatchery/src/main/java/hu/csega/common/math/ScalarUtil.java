package hu.csega.common.math;

public class ScalarUtil {

    public static final double EPSILON = 0.0000001;

    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx*dx + dy*dy); // TODO: Optimize SQRT.
    }

}
