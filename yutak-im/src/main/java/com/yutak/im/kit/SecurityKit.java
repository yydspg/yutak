package com.yutak.im.kit;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;


public class SecurityKit {

    public static List<String> getPair() {
        List<String> list = new ArrayList<>(2);
        list.add("test-private-key");
        list.add("test-public-key");
        return list;
    }

    public static void main(String[] args) {
        List<String> pair = getPair();
        pair.forEach(t->{
            System.out.println(t.toString());
        });
    }
}
