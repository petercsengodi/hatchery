package hu.csega.editors.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationUtil {

    public static byte[] serialize(Serializable object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(object);
        } catch (IOException ex) {
            throw new RuntimeException("Error occurred when trying to serialize object!", ex);
        }
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] array, Class<T> resultClass) {
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        try (ObjectInputStream in = new ObjectInputStream(bais)) {
            T ret = (T) in.readObject();
            if(!resultClass.isInstance(ret)) {
                throw new RuntimeException("Expected class: " + resultClass.getName() + " Deserialized object: " + ret.getClass().getName());
            }

            return ret;
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException("Error occurred when trying to deserialize object!", ex);
        }

    }
}
