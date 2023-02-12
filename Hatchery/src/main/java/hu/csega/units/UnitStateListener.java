package hu.csega.units;

public interface UnitStateListener<UNIT, STATE extends Enum<?>> {

	void onState(UNIT unit, STATE state);
	
}
