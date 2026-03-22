package hu.csega.games.engine.impl;

import org.joml.Vector4f;

public class GameEngineObjectPools {

    public static final LinkedListPool<Vector4f> VECTOR4F = new LinkedListPool<>(Vector4f.class, 20, null);
}
