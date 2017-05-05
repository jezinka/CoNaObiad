package com.projects.jezinka.conaobiad.models;

import java.util.List;

public class Meal {
    private long id;
    private String name;
    List<Ingredient> ingredients;
    private boolean checked;

    public Meal() {
        super();
    }

    public Meal(long id, String name) {
        this.id = id;
        this.name = name;
        this.checked = false;
    }

    public Meal(long id, String name, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
