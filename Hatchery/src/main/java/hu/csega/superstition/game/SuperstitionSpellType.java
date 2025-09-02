package hu.csega.superstition.game;

public enum SuperstitionSpellType {
    FIREBALL(75.0),
    FIRE_RAIN(80.0);

    SuperstitionSpellType(double coolDown) {
        this.coolDown = coolDown;
    }

    public double getCoolDown() {
        return coolDown;
    }

    private final double coolDown;
}
