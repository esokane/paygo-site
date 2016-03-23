package com.paygo.domain;

/**
 * report bouught by user and saved in DB
 */
public class Report extends AbstractReport{
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
