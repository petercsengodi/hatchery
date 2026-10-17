package hu.csega.study.opengl.test;

import hu.csega.study.opengl.OpenGLStart;
import hu.csega.study.opengl.renderEngine.GameLogic;
import hu.csega.study.opengl.renderEngine.RenderManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGameLogic implements GameLogic {

    private int direction = 0;
    private float color = 0.0f;

    private RenderManager renderManager = new RenderManager();

    @Override
    public void init() throws Exception {

        renderManager.init();
    }

    @Override
    public void input() {
        if(OpenGLStart.WINDOW_MANAGER.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            direction = 1;
        } else if(OpenGLStart.WINDOW_MANAGER.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
          direction = 0;
        }
    }

    @Override
    public void update() {
        color += (direction * 0.01f);
        if(color > 1f) {
            color = 1f;
        } else if(color < 0f) {
            color = 0f;
        }
    }

    @Override
    public void render() {
        if(OpenGLStart.WINDOW_MANAGER.isResize()) {
            GL11.glViewport(0, 0, OpenGLStart.WINDOW_MANAGER.getWidth(), OpenGLStart.WINDOW_MANAGER.getHeight());
            OpenGLStart.WINDOW_MANAGER.setResize(true);
        }

        OpenGLStart.WINDOW_MANAGER.setClearColor(color, 1f, color, 0.0f);
        renderManager.clear();
    }

    @Override
    public void cleanUp() {
        renderManager.cleanUp();
    }
}
