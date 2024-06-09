package hu.csega.superstition.game.play;

public class CollisionUtil {

    public static double sqr(double p2, double p1) {
        double d = p2 - p1;
        return d * d;
    }

    public static boolean close(double x1, double y1, double x2, double y2) {
        return sqr(x2, x1) + sqr(y2, y1) <= 500.0;
    }

    public static boolean close(double x1, double y1, double z1, double x2, double y2, double z2) {
        return sqr(x2, x1) + sqr(y2, y1) + sqr(z2, z1) <= 125.0;
    }

}
