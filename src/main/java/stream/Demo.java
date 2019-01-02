package stream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Demo {
    public static void main(String[] args){
        List<String> list=new ArrayList<>();
        list.add("And ");
        list.add("And ");
        list.add("hello");
        list.add("And ");
        list.add("world");
     //   list.forEach(System.out::print);
        Stream<String> stream=list.stream();
        //过滤重复
      //  List<String> newA=stream.distinct().collect(Collectors.toList());
      //  List<String> newAA=stream.map((x)->x.toLowerCase()).filter((x)->x.contains("a")).collect(Collectors.toList());
        //分页
       // System.out.println(stream.skip(2).limit(2).collect(Collectors.toList()));
        //匹配
        Predicate<String> p1=(x)->x.contains("a");
        Predicate<String> p2=(x)->x.contains("b");

        if(stream.anyMatch((x)->x.contains("ld"))){
            System.out.println("存在");
        }
        //p1.or(p2)
        if(stream.anyMatch(p1.and(p2))){
            System.out.println("存在");
        }
     /*   if(stream.anyMatch(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                //xxxxx
                return false;
            }
        }));*/



    }
}
