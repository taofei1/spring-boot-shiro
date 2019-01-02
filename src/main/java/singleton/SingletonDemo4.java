package singleton;

/**
 * 枚举单例
 */
public enum SingletonDemo4 {
    INSTANCE;
    private InnerClass innerClass_instance;
     SingletonDemo4(){
        System.out.println("=========");
        innerClass_instance=new InnerClass();
    }

    public InnerClass getInnerClass_instance() {
        return innerClass_instance;
    }

    private static class InnerClass{

    }
}
/**
 * 使用枚举的单例模式
 *
 * @author yzl
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
 class EnumSingleton{
    private EnumSingleton(){}
    public static EnumSingleton getInstance(){
        return Singleton.INSTANCE.getInstance();
    }

    private  enum Singleton{
        INSTANCE;

        private EnumSingleton singleton;
        //JVM会保证此方法绝对只调用一次
        private Singleton(){
            singleton = new EnumSingleton();
        }
        public EnumSingleton getInstance(){
            return singleton;
        }
    }
}
