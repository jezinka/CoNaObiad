package com.projects.jezinka.conaobiad.models;

import java.util.List;

public class Meal {
    private int id;
    private String name;
    List<Ingredient> ingredients;

    public Meal() {
        super();
    }

    public Meal(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Meal(int id, String name, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
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
