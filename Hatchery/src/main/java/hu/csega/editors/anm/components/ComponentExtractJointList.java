package hu.csega.editors.anm.components;

import hu.csega.editors.anm.layer1Views.swing.components.jointlist.AnimatorJointListItem;
import hu.csega.games.common.CommonDataTransformer;
import hu.csega.games.common.CommonDrain;
import hu.csega.games.common.CommonSource;
import hu.csega.games.library.animation.v1.anm.AnimationPart;

import java.util.List;

public interface ComponentExtractJointList extends CommonDataTransformer<AnimationPart, List<AnimatorJointListItem>>,
CommonSource<List<AnimatorJointListItem>>, CommonDrain<AnimationPart> {

}
