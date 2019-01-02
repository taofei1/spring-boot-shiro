package singleton;

/**
 * 饿汉式
 * 如果不使用浪费内存
 */
public class Singleton1 {
    private static Singleton1 ourInstance = new Singleton1();

    public static Singleton1 getInstance() {
        return ourInstance;
    }

    private Singleton1() {
    }
}
