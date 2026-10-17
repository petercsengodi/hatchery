package hu.csega.study.opengl.renderEngine;

import org.lwjgl.opengl.GL11;

public class RenderManager {

    public void init() throws Exception {

    }

    public void render() {

    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }
}
