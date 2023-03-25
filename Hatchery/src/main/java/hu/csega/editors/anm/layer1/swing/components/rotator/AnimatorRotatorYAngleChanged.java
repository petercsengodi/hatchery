package hu.csega.editors.anm.layer1.swing.components.rotator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class AnimatorRotatorYAngleChanged implements ChangeListener {

    private final AnimatorRotatorComponent parent;
    private final BoundedRangeModel model;

    private int lastValue;

    AnimatorRotatorYAngleChanged(AnimatorRotatorComponent animatorRotatorComponent, BoundedRangeModel model) {
        this.parent = animatorRotatorComponent;
        this.model = model;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int currentValue = model.getValue();
        int delta = currentValue - lastValue;
        lastValue = currentValue;

        AnimatorRotatorBinding binding = parent.getBinding();
        binding.changeYRotation(delta);
    }

}
