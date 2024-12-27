package hu.csega.superstition.game.play;

public class SpellInProgress {

    double startX;
    double startY;
    double startZ;

    double endX;
    double endY;
    double endZ;

    double t;

    long lastTimestamp;

    double movingRotation;

    double hitPoint;

    public SpellInProgress(long timestamp, double startX, double startY, double startZ,
                           double endX, double endY, double endZ) {
        this.lastTimestamp = timestamp;
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;

        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public void animate(long timestamp) {
        double delta = timestamp - lastTimestamp;
        lastTimestamp = timestamp;

        t += delta * SPEED;
    }

    public boolean isOver() {
        return t > 1.0;
    }

    public double getCurrentX() {
        return (endX - startX) * t + startX;
    }

    public double getCurrentY() {
        return (endY - startY) * t + startY;
    }

    public double getCurrentZ() {
        return (endZ - startZ) * t + startZ;
    }

    public double getHitPoint() {
        return hitPoint;
    }

    public void setHitPoint(double hitPoint) {
        this.hitPoint = hitPoint;
    }

    private static final double SPEED = 0.002;
}
