package com.github.java8.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Demo {

    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>();
        stringList.add("1");
        List<Integer> integerList = stringList.stream().map(Integer::parseInt).collect(Collectors.toList());
        integerList.forEach(i -> {
            System.out.println(i);
        });
    }
}
