package com.example.myfirstapp;

/**
 * Created by Thomas on 29/04/16.
 */
public class Logs_Type {
    private String name;
    private int id;
    private String unit;

    public Logs_Type(int id, String name, String unit) {
        this.id=id;
        this.name=name;
        this.unit=unit;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getUnit() {
        return unit;
    }
}
