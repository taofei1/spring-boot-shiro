package proxy.CustomProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LL {
    private void get(){}
    protected  void got(){}
    static void git(){}
    void gett(){}
    public void sort(){
        System.out.println(123);
    }
    public static void main(String[] args){
        System.out.println(111);
        Method[] fs=LL.class.getDeclaredMethods();
        for (int i = 0; i < fs.length; i++) {
            System.out.println(fs[i].getName()+fs[i].getModifiers());
        }
    }
}
