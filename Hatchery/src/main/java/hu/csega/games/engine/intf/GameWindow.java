package hu.csega.games.engine.intf;

import hu.csega.games.engine.env.Disposable;

import java.awt.Container;

public interface GameWindow extends Disposable {

	void register(GameWindowListener listener);
	void add(GameCanvas canvas, Container container);
	void setFullScreen(boolean fullScreen);
	void showWindow();
	void closeWindow();
	void closeApplication();
	void repaintEverything();

}
