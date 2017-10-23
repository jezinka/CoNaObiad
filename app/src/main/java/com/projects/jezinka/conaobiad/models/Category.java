package com.projects.jezinka.conaobiad.models;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class Category {
    private long id;
    private String name;

    private boolean checked;

    public Category() {
        super();
    }

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
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
