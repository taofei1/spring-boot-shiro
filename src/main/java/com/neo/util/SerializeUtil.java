package com.neo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ConfigurableObjectInputStream;

import java.io.*;

@Slf4j
public class SerializeUtil {
    public static Object deserialize(byte[] data) {
        Object t = null;
        try {
            ByteArrayInputStream fileIn = new ByteArrayInputStream(data);
            ObjectInputStream in = new ConfigurableObjectInputStream(fileIn,
                    Thread.currentThread().getContextClassLoader());
            t = in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            String msg = "Deserialization of marshalled XML failed!";
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        return t;
    }

    public static byte[] serialize(Object dtoObject) {
        byte[] result = null;
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(data);
            out.writeObject(dtoObject);
            out.close();
            result = data.toByteArray();
            data.close();
        } catch (IOException e) {
            String msg = "Serialization of marshalled XML failed!";
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        return result;
    }
}
