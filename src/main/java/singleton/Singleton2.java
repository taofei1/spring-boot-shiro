package singleton;
/**
 * 懒汉式
 * 线程非安全
 */
public class Singleton2 {
    private static Singleton2 ourInstance = null;

    public static Singleton2 getInstance() {
        if(ourInstance==null) {
           ourInstance=new Singleton2();
        }
        return ourInstance;
    }

    private Singleton2() {
    }
}
