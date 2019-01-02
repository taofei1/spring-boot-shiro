package singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 反射破坏单例
 */
public class TestRefectionClass {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clas=Singleton1.class;
        Constructor declaredConstructor = clas.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        System.out.println(declaredConstructor.newInstance());
    }
}
