package proxy.CustomProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyHandler implements CustomHandler{
     HelloService helloService;
    public MyHandler(HelloService helloService) {
        this.helloService = helloService;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        System.out.println(12321);
        return method.invoke(helloService,args);
    }
}
