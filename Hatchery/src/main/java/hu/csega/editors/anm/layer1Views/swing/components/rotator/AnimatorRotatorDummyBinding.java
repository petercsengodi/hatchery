package hu.csega.editors.anm.layer1Views.swing.components.rotator;

import hu.csega.toolshed.logging.Logger;
import hu.csega.toolshed.logging.LoggerFactory;

class AnimatorRotatorDummyBinding implements AnimatorRotatorBinding {

    private AnimatorRotatorComponent parent;
    private double x, y, z;

    AnimatorRotatorDummyBinding(AnimatorRotatorComponent parent) {
        this.parent = parent;
    }

    @Override
    public void changeXRotation(double change) {
        x += change;
        logger.debug("Dummy X angle set to: " + x);
        parent.repaintCanvas();
    }

    @Override
    public void changeYRotation(double change) {
        y += change;
        logger.debug("Dummy Y angle set to: " + y);
        parent.repaintCanvas();
    }

    @Override
    public void changeZRotation(double change) {
        z += change;
        logger.debug("Dummy Z angle set to: " + z);
        parent.repaintCanvas();
    }

    @Override
    public double currentXRotation() {
        return x;
    }

    @Override
    public double currentYRotation() {
        return y;
    }

    @Override
    public double currentZRotation() {
        return z;
    }

    private static final Logger logger = LoggerFactory.createLogger(AnimatorRotatorDummyBinding.class);
}
