package hu.csega.superstition.game.play;

import hu.csega.superstition.game.SuperstitionPlayer;
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

    SuperstitionPlayer player;
    double rotationSpeed;
    double rotationAngle;
    double max;

    boolean blocker;

    double heightMirror;

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

    public void rotateAroundPlayer(SuperstitionPlayer player, double rotationSpeed, double rotationAngle, double max) {
        this.player = player;

        this.rotationAngle = rotationAngle;
        this.rotationSpeed = rotationSpeed;
        this.max = max;
    }

    public void setHitPoint(double hitPoint) {
        this.hitPoint = hitPoint;
    }

    public void setHeightMirror(double heightMirror) {
        this.heightMirror = heightMirror;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void animate(long timestamp) {
        double delta = timestamp - lastTimestamp;
        lastTimestamp = timestamp;

        t += delta * speed;

        if(rotationSpeed > 0.0) {
            rotationAngle += rotationSpeed * delta;
        }
    }

    public boolean isOver() {
        return t > 1.0;
    }

    public boolean isBlocker() {
        return blocker;
    }

    public void setBlocker(boolean blocker) {
        this.blocker = blocker;
    }

    public double getCurrentX() {
        double cx = (endX - startX) * t + startX;

        if(rotationSpeed > 0.0) {
            if(max > 0.0) {
                cx = Math.min(cx, max);
            }

            cx = cx * Math.cos(rotationAngle);
        }

        if(player != null) {
            cx += player.x;
        }

        return cx;
    }

    public double getCurrentY() {
        double cy = (endY - startY) * t + startY;
        if(player != null) {
            cy += player.y;
        }

        if(heightMirror != 0.0 && cy > heightMirror) {
            cy = heightMirror - cy;
        }

        return cy;
    }

    public double getCurrentZ() {
        double cz = (endZ - startZ) * t + startZ;
        if(rotationSpeed > 0.0) {
            if(max > 0.0) {
                cz = Math.min(cz, max);
            }

            cz = cz * Math.sin(rotationAngle);
        }

        if(player != null) {
            cz += player.z;
        }

        return cz;
    }

    public double getHitPoint() {
        return hitPoint;
    }

    public SuperstitionSpellType getType() {
        return type;
    }

    public int spellModelIndex() {
        return type.getSpellModelIndex();
    }
}
