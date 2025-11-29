package hu.csega.editors.anm.layer1Views.swing.components.rotator;

public interface AnimatorRotatorBinding {

    void changeXRotation(double change);

    void changeYRotation(double change);

    void changeZRotation(double change);

    double currentXRotation();

    double currentYRotation();

    double currentZRotation();

}
