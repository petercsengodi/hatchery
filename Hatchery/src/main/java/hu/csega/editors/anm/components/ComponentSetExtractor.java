package hu.csega.editors.anm.components;

import hu.csega.editors.anm.layer2Transformation.parts.AnimatorSetPart;

import java.util.List;

public interface ComponentSetExtractor {

    List<AnimatorSetPart> extractSetParts();

    void invalidate();

}
