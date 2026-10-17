package hu.csega.study.opengl.renderEngine;

public interface GameLogic {

    void init() throws Exception;
    void input();
    void update();
    void render();
    void cleanUp();
}
