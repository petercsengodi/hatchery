package hu.csega.editors.anm.components;

import java.util.List;

import hu.csega.editors.anm.layer1Views.swing.components.partlist.AnimatorPartListItem;
import hu.csega.games.library.animation.v1.anm.Animation;
import hu.csega.games.common.CommonDataTransformer;
import hu.csega.games.common.CommonDrain;
import hu.csega.games.common.CommonSource;

public interface ComponentExtractPartList extends CommonDataTransformer<Animation, List<AnimatorPartListItem>>,
CommonSource<List<AnimatorPartListItem>>, CommonDrain<Animation> {

}
