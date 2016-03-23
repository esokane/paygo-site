package com.paygo.domain;

/**
 * Country enum, used for searching company
 */
public enum Country {
    USA(1,"USA"),
    CANADA(2,"Canada");

    private int value;

    private String name;

    Country(int value, String name){
       this.value = value;
       this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

}
