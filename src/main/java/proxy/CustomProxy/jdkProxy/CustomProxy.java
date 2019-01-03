package proxy.CustomProxy.jdkProxy;
import proxy.CustomProxy.DynamicJavac.DynamicCompile;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class CustomProxy {
    public static Object getProxy(Class clazz,CustomHandler customHandler) throws Exception {
        String pname=clazz.getSimpleName();
        String contentClz="";
        String line="\r";
        String tab="\t";
        String packageName="package "+clazz.getPackage().getName()+";"+line
                +"import "+clazz.getName()+";"+line
                +"import java.lang.reflect.UndeclaredThrowableException;"+line;
        String clazzContent="public class $Proxy implements "+pname+"{"
                +line+tab+"CustomHandler target;"+line;
        String constructor=tab+"public $Proxy(CustomHandler target){"
                +line+tab+tab+"this.target=target;"
                +line+tab+"}";
        String methodContent="";

        //所有自己声明的方法
        Method[] methods=clazz.getDeclaredMethods();
        for(Method method:methods){
            String returnS=method.getReturnType().getName();
            Class[] params=method.getParameterTypes();
            methodContent+=line+tab+"public "+returnS+" "+method.getName()+"(" ;
            String objectArray="Object arry[]={";
            String classArray="Class[] c={";
            for (int i = 0; i < params.length; i++) {
                methodContent+=params[i].getName()+" o"+i;
                objectArray+="o"+i;
                classArray+=params[i].getName()+".class";
                if(i!=params.length-1){
                    methodContent+=",";objectArray+=",";classArray+=",";

                }

            }
            classArray+="};";
            objectArray+="};";
                   methodContent+= "){"
                    +line+tab+tab+"try{"+line+tab+tab+tab;
                   if(params.length>0){
                       methodContent+=objectArray+line;
                       methodContent+=tab+tab+tab+classArray;
                   }
                    if(returnS!="void"){
                        methodContent+=line+tab+tab+tab+"return ("+returnS+")";
                    }
                    methodContent+="target.invoke(this,"+clazz.getName()+".class.getMethod(\""+method.getName()+"\"";
                    if(params.length>0){
                        methodContent+=",c";
                    }
                    methodContent+="),";

                    if(params.length==0){
                        methodContent+="(Object[])null);";
                    }else{

                        methodContent+="arry);";
                    }
                    methodContent+=line+tab+tab+"} catch (RuntimeException | Error var2) {"
                    +line+tab+tab+tab+"throw var2;"+line
                    +tab+tab+"} catch (Throwable var3) {"+line
                    +tab+tab+tab+"throw new UndeclaredThrowableException(var3);"
                    +line+tab+tab+"}"
                    +line+tab+"}";


        }
        contentClz+=packageName+clazzContent+constructor+methodContent
                +line+"}";
        String projectPath=System.getProperty("user.dir");
        File out=new File(projectPath+"\\src\\main\\java\\proxy\\CustomProxy\\$Proxy.java");
        FileWriter writer =new FileWriter(out);
        writer.write(contentClz);
        writer.flush();
        writer.close();

        DynamicCompile dynamicCompile=new DynamicCompile();
        Class<?> aClass = dynamicCompile.compileToClass("proxy.CustomProxy.$Proxy",contentClz);
        Constructor c0=aClass.getConstructor(CustomHandler.class);
        return c0.newInstance(customHandler) ;
    }
}
