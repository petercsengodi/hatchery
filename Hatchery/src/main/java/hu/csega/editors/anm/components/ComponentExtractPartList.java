package hu.csega.editors.anm.components;

import hu.csega.editors.anm.common.CommonComponent;
import hu.csega.editors.anm.layer1Views.swing.data.AnimatorPartListItem;

import java.util.List;

public interface ComponentExtractPartList extends CommonComponent {

    List<AnimatorPartListItem> extractPartList();

}
