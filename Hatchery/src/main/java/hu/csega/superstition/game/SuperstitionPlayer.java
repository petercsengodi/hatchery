package hu.csega.superstition.game;

import java.io.Serializable;

public class SuperstitionPlayer implements Serializable {

	public double health = 200;

	public double x;
	public double y;
	public double z;

	public double movingRotation;
	public double sightHorizontalRotation;
	public double sightVerticalRotation;

	int spellType;
	double spellCoolDown;
	double castingSpellRightNow;
	public long lastTimestamp = System.currentTimeMillis();

	public SuperstitionSpellType spellCastingWish;
	public SuperstitionSpellType spellLoadedForFiring;
	public long xp;

	public SuperstitionPlayer() {

	}

	public boolean isOnCoolDown() {
		return spellCoolDown > 0.0;
	}

	public int getSpellType() {
		return spellType;
	}

	public void startSpellCasting(SuperstitionSpellType type) {
		if(type == SuperstitionSpellType.SHOCKER)
			spellType = 1;
		else
			spellType = 0;

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

		if(castingSpellRightNow > 0.0) {
			double speed = (spellType == 0 ? SPELL_CAST_SPEED : SPELL_CAST_SPEED2);
			castingSpellRightNow -= delta * speed;
		}

		if(castingSpellRightNow < 0.0) {
			castingSpellRightNow = 0.0;
			spellType = 0;
		}

		if(spellCoolDown > 0.0) {
			double speed = (spellType == 0 ? COOL_DOWN_SPEED : COOL_DOWN_SPEED2);
			spellCoolDown -= delta * speed;
		}

		if(spellCoolDown <= 0.0) {
			spellCoolDown = 0.0;
		}
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

	private static final double SPELL_CAST_SPEED2 = 0.12;
	private static final double COOL_DOWN_SPEED2 = 0.12;

    private static final long serialVersionUID = 1L;

}
