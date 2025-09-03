package hu.csega.superstition.game;

public enum SuperstitionSpellType {
    FIREBALL(75.0, 0),
    ICE_RAIN(80.0, 1);

    SuperstitionSpellType(double coolDown, int spellModelIndex) {
        this.coolDown = coolDown;
        this.spellModelIndex = spellModelIndex;
    }

    public double getCoolDown() {
        return coolDown;
    }

    public int getspellModelIndex() {
        return spellModelIndex;
    }

    private final double coolDown;
    private final int spellModelIndex;
}
