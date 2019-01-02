package proxy.proxyJDKandCGLIB;

import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Auther: 13965
 * @Date: 2018/9/19 14:09
 * @Description:
 * @Version: 1.0
 */
    @SuppressWarnings("restriction")
    public class JDKProxy {
        public static void main(String[] args) throws IOException {
/*            byte[] proxyClassFile = ProxyGenerator.generateProxyClass("proxy.proxyJDKandCGLIB.ConcreteClass", new Class[]{JavaProxyInterface.class}); // proxyName 为类名，interfaces为顶层接口Class
//如果需要看，可以将字节码写入文件进行观察
            File file = new File("D:/Ddd.class");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(proxyClassFile);
            fileOutputStream.flush();
            fileOutputStream.close();*/

            /**com.sun.proxy.$Proxy0/1/2/3...
             * Proxy.newProxyInstance会生成真正的代理类并加载在内存中，
             * 实际生成的类是jdk生成的代理类，执行的是代理的同名方法
             * 而代理同名方法的逻辑却是发射调用我们的InvocationHandler的invoke方法
             * 并且传入生成的对象、Method对象和方法参数,因此
             * 执行的是InvocationHandler的逻辑
             */
           /* JavaProxyInterface newJavaProxyInterface = (JavaProxyInterface) Proxy.newProxyInstance(
                    JDKProxy.class.getClassLoader(), new Class[] { JavaProxyInterface.class },
                    new MyInvocationHandler(new ConcreteClass()));
            //这里可以看到这个类以及被代理，在执行方法前会执行aopMethod（）。这里需要注意的是oneDay（）方法和oneDayFinal（）的区别。oneDayFinal的方法aopMethod执行1次，oneDay的aopMethod执行1次
            newJavaProxyInterface.gotoSchool("xiaoma");
            newJavaProxyInterface.gotoWork();
            newJavaProxyInterface.oneDayFinal();
            newJavaProxyInterface.oneDay();*/

            // 保存生成的代理类的字节码文件
            System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

            // jdk动态代理测试
            JavaProxyInterface concreteClass = new MyInvocationHandler(new ConcreteClass()).getProxy();
            concreteClass.gotoSchool("hello");
        }
    }



