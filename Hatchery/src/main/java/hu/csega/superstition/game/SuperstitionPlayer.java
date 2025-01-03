package hu.csega.superstition.game;

public class SuperstitionPlayer {

	double health = 200;

	double x;
	double y;
	double z;
	double movingRotation;
	double sightHorizontalRotation;
	double sightVerticalRotation;

	double spellCoolDown;
	double castingSpellRightNow;
	long lastTimestamp = System.currentTimeMillis();

	boolean spellLoadedForFiring;
	long xp;

	public boolean isOnCoolDown() {
		return spellCoolDown > 0.0;
	}

	public void startSpellCasting(double coolDown) {
		if(spellCoolDown <= 0.0) {
			this.castingSpellRightNow = SPELL_CAST_MAX;
			this.spellCoolDown = coolDown;
			this.spellLoadedForFiring = true;
		}
	}

	public int spellCastingIndex() {
		int result = (int) Math.floor(castingSpellRightNow);
		if(result > 49)
			result = 49;
		else if(result < 0)
			result = 0;
		return result;
	}

	public void animate(long timestamp) {
		double delta = timestamp - lastTimestamp;
		lastTimestamp = timestamp;

		if(castingSpellRightNow > 0.0)
			castingSpellRightNow -= delta * SPELL_CAST_SPEED;

		if(spellCoolDown > 0.0)
			spellCoolDown -= delta * COOL_DOWN_SPEED;
	}

	public boolean shouldCastNow() {
		if(spellLoadedForFiring && castingSpellRightNow < SPELL_CAST_MAX / 2.0) {
			spellLoadedForFiring = false;
			return true;
		}

		return false;
	}

	private static final double SPELL_CAST_MAX = 50.0;
	private static final double SPELL_CAST_SPEED = 0.06;
	private static final double COOL_DOWN_SPEED = 0.06;

}
