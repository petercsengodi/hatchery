package hu.csega.study.opengl.renderEngine;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class WindowManager {

    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_FAR = 1000f;
    public static final float Z_NEAR = 0.01f;

    private final boolean vSync;

    private String title;
    private int width, height;
    private boolean resize;

    private long window;
    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.projectionMatrix = new Matrix4f();
    }

    public void init() {
        // So every error will be printed to the ERR channel.
        GLFWErrorCallback.createPrint(System.err).set();

        // Starting initialization of the library.
        if(!GLFW.glfwInit())
            throw new IllegalStateException("GLFW could not be initialized!");

        // These are not final values.
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2); // Core profile requires 3.2+
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE,  GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT,  GL11.GL_TRUE);

        boolean maximized = false;
        if(width == 0 || height == 0) {
            width = 100;
            height = 100;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            maximized = true;
        }

        // Creating the window.
        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if(window == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create a GLFW window!");

        // Event listener for the size of the window being changed.
        GLFW.glfwSetFramebufferSizeCallback(window, (_window, _width, _height) -> {
            this.width = _width;
            this.height = _height;
            this.setResize(true);
        });

        // ESC makes the application quit.
        GLFW.glfwSetKeyCallback(window, (_window, _key, _scancode, _action, _mods) -> {
            if(_key == GLFW.GLFW_KEY_ESCAPE && _action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        // Window positioning on the screen.
        if(maximized) {
            GLFW.glfwMaximizeWindow(window);
        } else {
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }

        // ???
        GLFW.glfwMakeContextCurrent(window);

        // ???
        if(isVSync()) {
            GLFW.glfwSwapInterval(1);
        }

        // Actually showing the window to the user.
        GLFW.glfwShowWindow(window);

        // ???
        GL.createCapabilities();

        // Initial colors.
        GL11.glClearColor(0f, 1f, 0f, 0f);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public void update() {
        GLFW.glfwSwapBuffers(window);

        // Start rendering all the objects we've placed currently in the queue.
        GLFW.glfwPollEvents();
    }

    public void cleanUp() {
        GLFW.glfwDestroyWindow(window);
    }

    public void setClearColor(float r, float g, float b, float a) {
        GL11.glClearColor(r, g, b, a);
    }

    public boolean isKeyPressed(int keyCode) {
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(window, title);
    }

    public boolean isResize() {
        return resize;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    public boolean isVSync() {
        return vSync;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindow() {
        return window;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix() {
        float aspectRatio =  (float) width / (float) height;
        return projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio =  (float) width / (float) height;
        return matrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }
}
