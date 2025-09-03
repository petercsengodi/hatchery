package hu.csega.superstition.game.play;

import hu.csega.superstition.game.SuperstitionSpellType;

public class SpellInProgress {

    SuperstitionSpellType type;

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
    double speed;

    public SpellInProgress(SuperstitionSpellType type, long timestamp, double startX, double startY, double startZ,
                           double endX, double endY, double endZ) {
        this.type = type;
        this.lastTimestamp = timestamp;

        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;

        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;

        this.speed = 0.002;
    }

    public void setHitPoint(double hitPoint) {
        this.hitPoint = hitPoint;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void animate(long timestamp) {
        double delta = timestamp - lastTimestamp;
        lastTimestamp = timestamp;

        t += delta * speed;
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

    public SuperstitionSpellType getType() {
        return type;
    }

    public int spellModelIndex() {
        return type.getspellModelIndex();
    }
}
