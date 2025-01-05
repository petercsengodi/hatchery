package hu.csega.common.math;

public class TriangleUtil {

    public static double zIfContainedOrInfinity(double px1, double py1, double pz1,
                                         double px2, double py2, double pz2,
                                         double px3, double py3, double pz3,
                                         double tx, double ty) {
        if(triangleContains(px1, py1, px2, py2, px3, py3, tx, ty)) {
            double l1 = ScalarUtil.distance(px1, py1, tx, ty);
            double l2 = ScalarUtil.distance(px2, py2, tx, ty);
            double l3 = ScalarUtil.distance(px3, py3, tx, ty);
            double l = l1 + l2 + l3;

            if(l < ScalarUtil.EPSILON) { // Intentionally not <abs>, as negative values are wrong as well.
                throw new RuntimeException("The <l> variable should be heavily positive at this point: " + l);
            }

            // TODO: Optimize multiplication/division somehow?
            double r1 = l1 / l;
            double r2 = l2 / l;
            double r3 = l3 / l;
            return r1 * pz1 + r2 * pz2 + r3 * pz3;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    public static boolean triangleContains(double px1, double py1,
                                           double px2, double py2,
                                           double px3, double py3,
                                           double tx, double ty) {

        // 1. Quick test: If <t> is outside the bounds, no way it is contained.
        if(tx < px1 && tx < px2 && tx < px3) { return false; }
        if(tx > px1 && tx > px2 && tx > px3) { return false; }
        if(ty < py1 && ty < py2 && ty < py3) { return false; }
        if(ty > py1 && tx > py2 && ty > py3) { return false; }

        // 2. For all (x1,y1)-(x2,y2) lines we need to check it <t> is on the same side as (x3,y3).
        double overOrUnder1 = LineUtil.overOrUnderLine(px1, py1, px2, py2, px3, py3);
        if(Math.abs(overOrUnder1) < ScalarUtil.EPSILON) {
            // The third point is on the edge, the triangle is just a line.
            // Further calculation is not needed.
            return false;
        }

        double overOrUnder2 = LineUtil.overOrUnderLine(px1, py1, px2, py2, tx, ty);
        if(overOrUnder1 < 0.0 && overOrUnder2 > 0.0 || overOrUnder1 > 0.0 && overOrUnder2 < 0.0) {
            // The point to test is not on the same side as the third point.
            // The point is outside the triangle.
            return false;
        }

        overOrUnder1 = LineUtil.overOrUnderLine(px3, py3, px1, py1, px2, py2);
        if(Math.abs(overOrUnder1) < ScalarUtil.EPSILON) {
            // The third point is on the edge, the triangle is just a line.
            // Further calculation is not needed.
            return false;
        }

        overOrUnder2 = LineUtil.overOrUnderLine(px3, py3, px1, py1, tx, ty);
        if(overOrUnder1 < 0.0 && overOrUnder2 > 0.0 || overOrUnder1 > 0.0 && overOrUnder2 < 0.0) {
            // The point to test is not on the same side as the third point.
            // The point is outside the triangle.
            return false;
        }

        overOrUnder1 = LineUtil.overOrUnderLine(px2, py2, px3, py3, px1, py1);
        if(Math.abs(overOrUnder1) < ScalarUtil.EPSILON) {
            // The third point is on the edge, the triangle is just a line.
            // Further calculation is not needed.
            return false;
        }

        overOrUnder2 = LineUtil.overOrUnderLine(px2, py2, px3, py3, tx, ty);
        if(overOrUnder1 < 0.0 && overOrUnder2 > 0.0 || overOrUnder1 > 0.0 && overOrUnder2 < 0.0) {
            // The point to test is not on the same side as the third point.
            // The point is outside the triangle.
            return false;
        }

        // 3. We checked everything, and everything is fine.
        return true;
    }

    public static boolean triangleCounterClockwise() {
        // Calculate "signed area", if positive, then counter-clockwise.
        return true;
    }

}
