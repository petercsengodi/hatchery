package hu.csega.common.math.hu.csega.common.math;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import hu.csega.common.math.TriangleUtil;

import org.junit.Test;

public class TriangleUtilTest {

    @Test
    public void testZIfContainedOrInfinity() {
        assertTrue("Should NOT be contained because of bounds.", TriangleUtil.zIfContainedOrInfinity(
                /* p1 */ 1.0, 1.0, 0.0,
                /* p2 */ 2.0, 2.0, 0.0,
                /* p3 */ 1.5, -1.0, 0.0,
                /* t */ -5.0, 10.0) >= Double.POSITIVE_INFINITY);

        assertTrue("Should NOT be contained, but it is inside the bounds.", TriangleUtil.zIfContainedOrInfinity(
                /* p1 */ 1.0, 1.0, 0.0,
                /* p2 */ 2.0, 2.0, 0.0,
                /* p3 */ 1.5, -1.0, 0.0,
                /* t */ 1.0001, 1.5) >= Double.POSITIVE_INFINITY);

        assertTrue("Should be contained, but it is inside the bounds.", TriangleUtil.zIfContainedOrInfinity(
                /* p1 */ 1.0, 1.0, -1.0,
                /* p2 */ 2.0, 2.0, -2.0,
                /* p3 */ 1.5, -1.0, 0.0,
                /* t */ 1.5, 0.0) < 0.0);
    }

    @Test
    public void testContainment() {
        assertFalse("Should NOT be contained because of bounds.", TriangleUtil.triangleContains(1.0, 1.0, 2.0, 2.0, 1.5, -1.0, -5.0, 10.0));
        assertFalse("Should NOT be contained, but it is inside the bounds.", TriangleUtil.triangleContains(1.0, 1.0, 2.0, 2.0, 1.5, -1.0, 1.0001, 1.5));
        assertTrue("Should be contained, but it is inside the bounds.", TriangleUtil.triangleContains(1.0, 1.0, 2.0, 2.0, 1.5, -1.0, 1.5, 0.0));
    }

}
