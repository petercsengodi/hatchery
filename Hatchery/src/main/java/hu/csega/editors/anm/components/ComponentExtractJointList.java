package hu.csega.editors.anm.components;

import hu.csega.editors.anm.layer1Views.swing.data.AnimatorJointListItem;

import java.util.List;

public interface ComponentExtractJointList {

    List<AnimatorJointListItem> extractJointList();

    void invalidate();

}
