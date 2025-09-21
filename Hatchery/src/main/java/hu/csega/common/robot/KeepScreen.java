package hu.csega.common.robot;

import java.awt.*;

public class KeepScreen {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting KeepScreen...");
        Robot robot = new Robot();
        long startTime = System.currentTimeMillis();

        while (true) {
            if(System.currentTimeMillis() - startTime > TWENTY_HOURS)
                break;

            Thread.sleep(10_000L);
            // robot.mouseMove(0, 0);
            Point location = MouseInfo.getPointerInfo().getLocation();
            robot.mouseMove(location.x, location.y);
        }
    }

    public static final long TWENTY_HOURS = 20 * 3600 * 1000L;
}
