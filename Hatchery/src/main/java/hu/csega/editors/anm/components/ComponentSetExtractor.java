package hu.csega.editors.anm.components;

import hu.csega.editors.anm.common.CommonComponent;
import hu.csega.editors.anm.layer2Transformation.parts.AnimatorSetPart;

import java.util.List;

public interface ComponentSetExtractor extends CommonComponent {

    List<AnimatorSetPart> extractSetParts();

}
