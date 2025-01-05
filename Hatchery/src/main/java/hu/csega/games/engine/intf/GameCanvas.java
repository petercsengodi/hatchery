package hu.csega.games.engine.intf;

import hu.csega.games.engine.env.Disposable;

public interface GameCanvas extends Disposable {

	int getWidth();

	int getHeight();

	void repaint();

}
