package proxy.CustomProxy;

import java.io.IOException;
import java.lang.reflect.Field;

public class Test {

    public static void main(String[] args) throws Exception {
        HelloService helloService= (HelloService) CustomProxy.getProxy(HelloService.class,new MyHandler(new HelloServiceImpl()) );
      // HelloService helloService=new $Proxy(new MyHandler(new HelloServiceImpl()));
        System.out.println(helloService.ss("ÄãºÃ",00));

       // System.out.println(c.getModifiers());
 /*      HelloService helloService=new $Proxy(new HelloServiceImpl());
       helloService.sayHello();*/
     // System.out.println(System.getProperty("user.dir"));

        //System.out.println(Test.class.getResource("/").getPath());
        //System.out.println(Test.class.getClassLoader().getResource("com.").getPath());

    }
}
