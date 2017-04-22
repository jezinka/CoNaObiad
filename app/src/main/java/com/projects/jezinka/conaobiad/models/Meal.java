package com.projects.jezinka.conaobiad.models;

/**
 * Created by jezinka on 30.03.17.
 */

public class Meal {
    private int id;
    private String name;

    public Meal() {
        super();
    }

    public Meal(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
