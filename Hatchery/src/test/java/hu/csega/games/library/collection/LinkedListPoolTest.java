package hu.csega.games.library.collection;

import java.util.ArrayList;
import java.util.List;

import hu.csega.games.engine.impl.LinkedListPool;
import org.joml.Vector3f;

public class LinkedListPoolTest {

    public static void main(String[] args) {
        LinkedListPool<Vector3f> pool = new LinkedListPool<>(Vector3f.class, 10, null);
        List<Vector3f> list = new ArrayList<>();

        System.out.println(pool.getCounter());
        for (int i = 0; i < 20; i++)
            list.add(pool.allocate());

        System.out.println(pool.getCounter());
        for(Vector3f v : list)
            pool.release(v);

        System.out.println(pool.getCounter());
    }

}
