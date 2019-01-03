package proxy.CustomProxy.cglibProxy;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class MyEnhancer  extends ClassLoader implements Opcodes {
    Class superClass;
    Object proxy;
    public MyEnhancer(){
        super();
    }
    public void setSuperClass(Class superClass){
        this.superClass=superClass;
    }
    public void proxyObject(Object proxy){
        this.proxy=proxy;
    }
    public Object create(){
        String name=superClass.getName();
        List list=new ArrayList();
        Class[] interfaces=superClass.getInterfaces();
        for(Class i:interfaces){
            list.add(i.getName());
        }
        String str[]=new String[list.size()];
        if(list.size()>0){
            str= (String[]) list.toArray();
        }
        ClassWriter cw=new ClassWriter(0);
        cw.visit(V1_1,ACC_PUBLIC,name+"Proxy",null,superClass.getName().replace(".","/"),str);
        return null;
    }
}
