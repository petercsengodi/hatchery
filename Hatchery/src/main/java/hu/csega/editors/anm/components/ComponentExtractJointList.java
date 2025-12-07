package hu.csega.editors.anm.components;

import hu.csega.editors.anm.common.CommonComponent;
import hu.csega.editors.anm.layer1Views.swing.data.AnimatorJointListItem;

import java.util.List;

public interface ComponentExtractJointList extends CommonComponent {

    List<AnimatorJointListItem> extractJointList();

}
