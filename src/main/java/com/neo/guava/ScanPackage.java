package com.neo.guava;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;

public class ScanPackage {

    public static void main(String[]  args) throws IOException {
        StringBuffer sb=new StringBuffer();
        String packageName="com.neo.entity";
        ClassPath classpath = ClassPath.from(ScanPackage.class.getClassLoader());
        ImmutableSet<ClassPath.ClassInfo> topLevelClasses = classpath.getTopLevelClasses(packageName);
        ImmutableSet<ClassPath.ClassInfo> allClasses = classpath.getAllClasses();
        ImmutableSet<ClassPath.ClassInfo> topLevelClasses1 = classpath.getTopLevelClasses();
        ImmutableSet<ClassPath.ResourceInfo> resources = classpath.getResources();
        ImmutableSet<ClassPath.ClassInfo> topLevelClassesRecursive = classpath.getTopLevelClassesRecursive("com.neo.service");
      //  System.out.println("===========getTopLevelClasses()============");
       // topLevelClasses1.forEach((classInfo)->System.out.println(classInfo.getName()));
      //  System.out.println("===========getAllClasses()============");
       // allClasses.forEach((classInfo)->System.out.println(classInfo.getName()));
        System.out.println("===========getTopLevelClasses(packageName)============");
        for(ClassPath.ClassInfo classInfo:topLevelClasses){
            System.out.println(classInfo.getName());
        }
       // System.out.println("===========getResources()============");
       // resources.forEach((r)->System.out.println(r.getResourceName()+" "+r.url()));
        System.out.println("===========getTopLevelClassesRecursive()============");
        topLevelClassesRecursive.forEach((classInfo)->System.out.println(classInfo.getName()));

    }
}
