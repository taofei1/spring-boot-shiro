package jvm;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static java.lang.invoke.MethodHandles.lookup;

public class MethodHandleTest {
    public static void main(String[] args) throws Throwable {
        Object ob = System.currentTimeMillis() % 2 == 0 ? System.out : new ClassA();
        getPrintlnMH(ob).invokeExact("111");

    }

    private static MethodHandle getPrintlnMH(Object reveiver) throws NoSuchMethodException, IllegalAccessException {
        MethodType mt = MethodType.methodType(void.class, String.class);
        return lookup().findVirtual(reveiver.getClass(), "println", mt).bindTo(reveiver);
    }

    static class ClassA {
        public void println(String str) {
            System.out.println(str);
        }
    }
}
