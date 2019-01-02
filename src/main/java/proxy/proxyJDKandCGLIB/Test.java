package proxy.proxyJDKandCGLIB;

import java.lang.reflect.Method;

public class Test {
    public static void main(String[]  args) throws ClassNotFoundException, NoSuchMethodException {
        Class m=Class.forName("proxy.proxyJDKandCGLIB.JavaProxyInterface");
        Method[] methods = m.getMethods();
        for (Method mm:methods){
            System.out.println(mm.getName());
        }
    }
}
