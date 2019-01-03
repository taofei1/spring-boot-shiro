package proxy.CustomProxy.jdkProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface CustomHandler {
     Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException;
}
