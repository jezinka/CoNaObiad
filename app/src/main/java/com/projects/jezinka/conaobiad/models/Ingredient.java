package com.projects.jezinka.conaobiad.models;


public class Ingredient {
    private long id;
    private String name;
    private boolean checked;

    public Ingredient() {
        super();
    }

    public Ingredient(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Ingredient(long id, String name, boolean checked) {
        this.id = id;
        this.name = name;
        this.checked = checked;
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
