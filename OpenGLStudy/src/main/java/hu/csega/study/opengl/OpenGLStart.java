package hu.csega.study.opengl;

import hu.csega.study.opengl.renderEngine.WindowManager;
import org.lwjgl.Version;

public class OpenGLStart {

    public static void main(String[] args) {
        System.out.println("Let's goooooo!!!!!!!! " + Version.getVersion());
        System.out.println("Many thanks to: ThinMatrix https://youtube.com/@ThinMatrix, DevGenie Academy https://www.youtube.com/@devgenieacademy8889");

        WindowManager manager = new WindowManager("Render Engine", 1600, 900, false);
        manager.init();

        while(!manager.windowShouldClose()) {
            manager.update();
        }

        manager.cleanUp();
    }
}
