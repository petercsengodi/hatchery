package hu.csega.common.math;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LineUtilTest {

    @Test
    public void testOverOrUnderLine() {
        double value1 = LineUtil.overOrUnderLine(1.0, 1.0, 2.0, 2.0, 3.0, 3.0);
        assertTrue("Should be zero: " + value1 + " It's exactly on the line.", value1 < ScalarUtil.EPSILON);
        double value2 = LineUtil.overOrUnderLine(1.0, 1.0, 2.0, 2.0, 1.5, 3.0);
        assertTrue("Should be positive: " + value2 + " It's over the line.", value2 > 0.0);
        double value3 = LineUtil.overOrUnderLine(1.0, 1.0, 1.0, 2.0, 0.5, 3.0);
        assertTrue("Should be negative: " + value3 + " It's on the left side of the vertical line.", value3 < 0.0);
        double value4 = LineUtil.overOrUnderLine(1.0, 1.0, 1.1, 3.0, -0.5, 2.0);
        assertTrue("Should be negative: " + value4 + " It's on the left side of the almost vertical line.", value4 < 0.0);
    }
}
