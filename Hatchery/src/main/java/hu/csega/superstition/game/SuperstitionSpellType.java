package hu.csega.superstition.game;

public enum SuperstitionSpellType {
    BLOCKER(100.0, 2),
    SPIKES(75.0, 0),
    SPIRAL(50.0, 0),
    HARD_PUSH(100.0, 0),
    FIREBALL(75.0, 0),
    ICE_RAIN(80.0, 1);

    SuperstitionSpellType(double coolDown, int spellModelIndex) {
        this.coolDown = coolDown;
        this.spellModelIndex = spellModelIndex;
    }

    public double getCoolDown() {
        return coolDown;
    }

    public int getSpellModelIndex() {
        return spellModelIndex;
    }

    private final double coolDown;
    private final int spellModelIndex;
}
