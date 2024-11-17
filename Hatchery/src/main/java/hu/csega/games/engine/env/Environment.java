package hu.csega.games.engine.env;

import hu.csega.games.units.DefaultImplementation;
import hu.csega.games.units.Unit;

@Unit
@DefaultImplementation(EnvironmentImpl.class)
public interface Environment {

	void registerForDisposing(Disposable disposable);

	void notifyExiting();

}
