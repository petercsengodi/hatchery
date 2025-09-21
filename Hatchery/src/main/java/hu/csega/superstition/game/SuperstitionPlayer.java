package hu.csega.superstition.game;

public class SuperstitionPlayer {

	public double health = 200;

	public double x;
	public double y;
	public double z;

	public double movingRotation;
	public double sightHorizontalRotation;
	public double sightVerticalRotation;

	double spellCoolDown;
	double castingSpellRightNow;
	public long lastTimestamp = System.currentTimeMillis();

	public SuperstitionSpellType spellLoadedForFiring;
	public long xp;

	public boolean isOnCoolDown() {
		return spellCoolDown > 0.0;
	}

	public void startSpellCasting(SuperstitionSpellType type) {
		if(spellCoolDown <= 0.0) {
			this.castingSpellRightNow = SPELL_CAST_MAX;
			this.spellCoolDown = type.getCoolDown();
			this.spellLoadedForFiring = type;
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

	public SuperstitionSpellType shouldCastNow() {
		if(spellLoadedForFiring != null && castingSpellRightNow < SPELL_CAST_MAX / 2.0) {
			SuperstitionSpellType ret = spellLoadedForFiring;
			spellLoadedForFiring = null;
			return ret;
		}

		return null;
	}

	private static final double SPELL_CAST_MAX = 50.0;
	private static final double SPELL_CAST_SPEED = 0.06;
	private static final double COOL_DOWN_SPEED = 0.06;

}
