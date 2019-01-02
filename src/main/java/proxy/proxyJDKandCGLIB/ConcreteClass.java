package proxy.proxyJDKandCGLIB;

/**
 * 需要被代理的类，实现了顶层接口，非必须
 */
public class ConcreteClass implements JavaProxyInterface {
    @Override
    public void gotoSchool(String name) {
        System.out.println(name+"gotoSchool");
    }
    @Override
    public void gotoWork() {
        System.out.println("gotoWork");
    }
    @Override
    public void oneDay() {
        gotoSchool("xiaoma");
        gotoWork();
    }
    @Override
    public final void oneDayFinal() {
        gotoSchool("xiaoma");
        gotoWork();
    }
}