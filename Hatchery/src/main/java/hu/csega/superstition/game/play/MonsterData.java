package hu.csega.superstition.game.play;

import hu.csega.superstition.game.map.MapTile;

import java.io.Serializable;

public class MonsterData implements Serializable {

    public double maxHitDamage = 100.0;

    public MonsterData(String animation) {
        this(animation, 100.0);
    }
    public MonsterData(String animation, double initialHealth) {
        this.animation = animation;
        this.health = initialHealth;
        this.scale = 0.1;
        this.cooldown = 1;
    }

    public void identifyPosition(double x, double y, double z, MapTile mapTile) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mapTile = mapTile;
    }

    public void setLevel(double level) {
        this.expectedXP = (long) level;
        this.health = this.expectedXP * 10.0;
        this.maxHitDamage = this.expectedXP * 2.0;
    }

    public void setCooldown(double seconds) {
        this.cooldown = seconds;
    }

    public boolean shouldCastSpellNow(double elapsedSeconds) {
        cooldown = cooldown - elapsedSeconds;
        if(cooldown < 0.0)
            cooldown = 0.0;

        if(fy > 0.0) {
            return false;
        }

        return cooldown <= 0.0;
    }

    public String animation;

    public double x;
    public double y;
    public double z;
    public double scale;
    public double movingRotation;

    public double fx;
    public double fy;
    public double fz;

    public double vx;
    public double vy;
    public double vz;

    public double ax = -1;
    public double ay = -10;
    public double az = -1;

    public double health;
    public long expectedXP;
    public double cooldown;

    public MapTile mapTile;

    /* FIXME this is not serializable. */
    public Object target;

    private static final long serialVersionUID = 1L;
}
