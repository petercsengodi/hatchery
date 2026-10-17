package hu.csega.study.opengl;

import hu.csega.study.opengl.renderEngine.EngineManager;
import hu.csega.study.opengl.renderEngine.GameLogic;
import hu.csega.study.opengl.renderEngine.WindowManager;
import hu.csega.study.opengl.test.TestGameLogic;
import org.lwjgl.Version;

public class OpenGLStart {

    public static WindowManager WINDOW_MANAGER;
    public static EngineManager ENGINE_MANAGER;
    public static GameLogic GAME_LOGIC;

    public static void main(String[] args) {
        System.out.println("Let's goooooo!!!!!!!! " + Version.getVersion());
        System.out.println("Many thanks to: ThinMatrix https://youtube.com/@ThinMatrix, DevGenie Academy https://www.youtube.com/@devgenieacademy8889");

        GAME_LOGIC = new TestGameLogic();
        WINDOW_MANAGER = new WindowManager("Render Engine", 1600, 900, false);
        ENGINE_MANAGER = new EngineManager();

        try {
            ENGINE_MANAGER.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
