package stream;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.DoubleStream;

public class Demo2 {
    private String name;
    private double price;
    private Integer amount;

    public Demo2(String name, double price, Integer amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    public static void main(String[] args){
        List<Demo2> all= Arrays.asList(new Demo2("11",23.4,2),new Demo2("32",34,2));
       // all.stream().map((x)->x.getAmount()*x.getPrice()).forEach(System.out::println);
        double d=all.stream().map((x)->x.getAmount()*x.getPrice()).reduce((sum,m)->sum+m).get();
        DoubleSummaryStatistics doubleSummaryStatistics = all.stream().mapToDouble((x) -> x.getAmount() * x.getPrice()).summaryStatistics();

        System.out.println(d);

    }
}
