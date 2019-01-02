//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package proxy.proxyClass.jdk;

import proxy.proxyJDKandCGLIB.JavaProxyInterface;
import proxy.proxyJDKandCGLIB.MyInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * jdk代理实际产生的类，其继承了Proxy并且实现了我们的接口，由于单一继承，因此jdk代理只能面向接口，
 * 下面的方法实际进入我们写的MyInvocationHandler的处理逻辑
 */
public final class ConcreteClass extends Proxy implements JavaProxyInterface {
    private static Method m1;
    private static Method m3;
    private static Method m5;
    private static Method m2;
    private static Method m6;
    private static Method m4;
    private static Method m0;

    public ConcreteClass(InvocationHandler var1)   {
        super(var1);
    }

    public final boolean equals(Object var1)   {
        try {
            /**
             * 对应InvocationHandler中invoke的三个参数
             * @Param this 此代理类
             * @Param m1 此方法
             * @Param var1 参数
             */
            return ((Boolean)super.h.invoke(this, m1, new Object[]{var1})).booleanValue();
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void gotoSchool(String name)  {
        try {
            super.h.invoke(this, m3,new Object[]{name});
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void oneDay()   {
        try {
            super.h.invoke(this, m5, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String toString()   {
        try {
            return (String)super.h.invoke(this, m2, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void oneDayFinal()   {
        try {
            super.h.invoke(this, m6, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void gotoWork()   {
        try {
            super.h.invoke(this, m4, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final int hashCode()   {
        try {
            return ((Integer)super.h.invoke(this, m0, (Object[])null)).intValue();
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }
    public static void main(String[]  args){
        ConcreteClass concreteClass=new ConcreteClass(new MyInvocationHandler(new proxy.proxyJDKandCGLIB.ConcreteClass()));
        concreteClass.gotoSchool("hh ");
    }

    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m3 = Class.forName("proxy.proxyJDKandCGLIB.JavaProxyInterface").getMethod("gotoSchool",String.class);
            m5 = Class.forName("proxy.proxyJDKandCGLIB.JavaProxyInterface").getMethod("oneDay");
            m2 = Class.forName("java.lang.Object").getMethod("toString");
            m6 = Class.forName("proxy.proxyJDKandCGLIB.JavaProxyInterface").getMethod("oneDayFinal");
            m4 = Class.forName("proxy.proxyJDKandCGLIB.JavaProxyInterface").getMethod("gotoWork");
            m0 = Class.forName("java.lang.Object").getMethod("hashCode");
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(var2.getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}
