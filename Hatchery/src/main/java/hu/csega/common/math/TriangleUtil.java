package hu.csega.common.math;

public class TriangleUtil {

    /**
     * @param counterClockWise If null, no extra checks will be done. If true, then only counter-clockwise triangles
     *                         will be checked. If false, then only clockwise triangles will be checked.
     * @return Positive infinity, if test point is not in triangle, otherwise a calculated Z value.
     */
    public static double zIfContainedOrInfinity(double px1, double py1, double pz1,
                                         double px2, double py2, double pz2,
                                         double px3, double py3, double pz3,
                                         double tx, double ty, Boolean counterClockWise) {

        if(counterClockWise != null) {
            double v = triangleSignedArea(px1, py1, px2, py2, px3, py3);
            if(counterClockWise && v > 0.0 || !counterClockWise && v < 0.0) {
                // TODO: This should be the other way around, but I don't know why it works this way.
                return Double.POSITIVE_INFINITY;
            }
        }

        if(triangleContains(px1, py1, px2, py2, px3, py3, tx, ty)) {
            double l1 = ScalarUtil.distance(px1, py1, tx, ty);
            if(l1 < ScalarUtil.EPSILON) {
                return pz1; // If very near to <p1>, return <z1>.
            }

            double l2 = ScalarUtil.distance(px2, py2, tx, ty);
            if(l2 < ScalarUtil.EPSILON) {
                return pz2; // If very near to <p2>, return <z2>.
            }

            double l3 = ScalarUtil.distance(px3, py3, tx, ty);
            if(l3 < ScalarUtil.EPSILON) {
                return pz3; // If very near to <p3>, return <z3>.
            }

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
        if(ty > py1 && ty > py2 && ty > py3) { return false; }

        // 2. For all (x?,y?)-(x?,y?) lines we need to check it <t> is on the same side as (x?,y?).

        // 2. a. Line: (x1,y1)-(x2,y2) Point: (x3,y3)
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

        // 2. b. Line: (x3,y3)-(x1,y1) Point: (x2,y2)
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

        // 2. c. Line: (x2,y2)-(x3,y3) Point: (x1,y1)
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

    public static double triangleSignedArea(double x1, double y1, double x2, double y2, double x3, double y3) {
        return (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
    }

    public static boolean triangleCounterClockwise(double x1, double y1, double x2, double y2, double x3, double y3) {
        // Calculate "signed area", if positive, then counter-clockwise.
        // TODO: This should be the other way around, but I don't know why it works this way.
        return triangleSignedArea(x1, y1, x2, y2, x3, y3) < 0.0;
    }

}
