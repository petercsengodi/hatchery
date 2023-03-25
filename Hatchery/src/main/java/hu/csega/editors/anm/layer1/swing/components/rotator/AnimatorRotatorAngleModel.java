package hu.csega.editors.anm.layer1.swing.components.rotator;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class AnimatorRotatorAngleModel implements BoundedRangeModel {

    @Override
    public int getMinimum() {
        return -180;
    }

    @Override
    public void setMinimum(int newMinimum) {

    }

    @Override
    public int getMaximum() {
        return 180;
    }

    @Override
    public void setMaximum(int newMaximum) {

    }

    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public void setValue(int newValue) {

    }

    @Override
    public void setValueIsAdjusting(boolean b) {

    }

    @Override
    public boolean getValueIsAdjusting() {
        return false;
    }

    @Override
    public int getExtent() {
        return 1;
    }

    @Override
    public void setExtent(int newExtent) {

    }

    @Override
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {

    }

    @Override
    public void addChangeListener(ChangeListener x) {

    }

    @Override
    public void removeChangeListener(ChangeListener x) {

    }
}