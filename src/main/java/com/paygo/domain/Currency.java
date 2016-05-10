package com.paygo.domain;

/**
 * enumeration for Currency
 */
public enum Currency {

    USD(1);

    private int value;

    Currency(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }





}
