package hu.csega.superstition.game.map;

import hu.csega.common.math.ScalarUtil;
import hu.csega.games.engine.GameEngineFacade;
import hu.csega.games.engine.g3d.GameObjectHandler;
import hu.csega.games.engine.g3d.GameObjectPlacement;
import hu.csega.games.engine.intf.GameGraphics;
import hu.csega.superstition.SuperstitionGameStarter;
import hu.csega.superstition.game.SuperstitionGameElements;
import hu.csega.superstition.game.SuperstitionPlayer;
import hu.csega.superstition.game.SuperstitionSerializableModel;
import hu.csega.superstition.game.SuperstitionSpellType;
import hu.csega.superstition.game.play.CollisionUtil;
import hu.csega.superstition.game.play.MonsterData;
import hu.csega.superstition.game.play.SpellInProgress;
import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class SuperstitionMapRenderer {

	private final Matrix4f rotation = new Matrix4f();
	private final Vector4f target = new Vector4f();
	private final Vector4f up = new Vector4f();

	private Robot robot;
	private long lastCheck;

	int lastAnimIndex;

	private final List<String> logsOnScreen = new ArrayList<>();

	public List<MonsterData> monstersAround = new ArrayList<>();

	public void renderGame(GameEngineFacade facade, SuperstitionSerializableModel universe, SuperstitionGameElements elements) {
		GameGraphics g = facade.graphics();
	}

}
