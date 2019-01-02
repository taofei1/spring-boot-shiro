package proxy.CustomProxy;

import java.lang.reflect.InvocationTargetException;

public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("hello");
    }

    @Override
    public String ss(String i, Integer o) {

        return i+o;
    }

    public static void main(String[] args){
        HelloService helloService=new HelloServiceImpl();
        helloService.sayHello();
    }
}
