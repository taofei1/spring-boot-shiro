package singleton;


public class Singleton3 {
    private static Singleton3 ourInstance =null;
    /**
     * 懒汉式线程安全但是会降低性能
     */
    public synchronized static Singleton3 getInstance() {
        if(ourInstance==null) {
            ourInstance= new Singleton3();
        }
        return ourInstance;
    }

    /**1.memory=allocate() 在堆上分配内存内控间
     * 2.ctorInstance()   初始化类成员
     * 3instance=memory   让实例指向堆内存的空间
     * 线程非安全
     * 多核cpu,jmm指令重排序 ，2 3可能调换
     * 解决方案可在实例上加volatile
     * @return
     */
    public  static Singleton3 getInstance2() {
        if(ourInstance==null) {
            synchronized (Singleton3.class) {
                if (ourInstance == null) {
                    ourInstance = new Singleton3();
                }
            }
        }
        return ourInstance;
    }

    private Singleton3() {
    }
}
