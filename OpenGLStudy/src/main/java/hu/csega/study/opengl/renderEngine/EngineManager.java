package hu.csega.study.opengl.renderEngine;

import hu.csega.study.opengl.OpenGLStart;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {

    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 1000;

    private static int fps;
    private static float frameTime = 1.0f / FRAMERATE;

    private boolean running;

    private GLFWErrorCallback errorCallback;

    public void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        OpenGLStart.WINDOW_MANAGER.init();
    }

    public void start() throws Exception {
        init();
        if(running) {
            return;
        }
        run();
    }

    public void run() {
        running = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while(running) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            // input

            while (unprocessedTime > frameTime) {
                render = true;
                unprocessedTime -= frameTime;

                if(OpenGLStart.WINDOW_MANAGER.windowShouldClose()) {
                    stop();
                }

                if(frameCounter >= NANOSECOND) {
                    setFps(frames);
                    OpenGLStart.WINDOW_MANAGER.setTitle("FPS: " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }

                if(render) {
                    update();
                    render();
                    frames++;
                }
            }
        }

        cleanUp();
    }

    public void stop() {
        if(!running) {
            return;
        }
        running = false;
    }

    public void input() {

    }

    public void render() {
        OpenGLStart.WINDOW_MANAGER.update();
    }

    public void update() {

    }

    public void cleanUp() {
        OpenGLStart.WINDOW_MANAGER.cleanUp();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
