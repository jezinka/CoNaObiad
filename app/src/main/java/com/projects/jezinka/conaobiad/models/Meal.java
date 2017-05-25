package com.projects.jezinka.conaobiad.models;

import java.util.List;

public class Meal {
    private long id;
    private String name;
    List<Ingredient> ingredients;
    private boolean checked;
    private String recipe;

    public Meal() {
        super();
    }

    public Meal(long id, String name, String recipe) {
        this.id = id;
        this.name = name;
        this.recipe = recipe;
        this.checked = false;
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

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }
}
