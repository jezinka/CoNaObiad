package com.projects.jezinka.conaobiad.models;

import java.util.Date;

public class Dinner {

    private long id;
    private Meal meal;
    private Date date;

    public Dinner() {
        super();
    }

    public Dinner(long id, Meal meal, Date date) {
        this.id = id;
        this.meal = meal;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Meal getMeal() {
        return meal;
    }

    public Date getDate() {
        return date;
    }

    public String getMealName() {
        return this.meal.getName();
    }

    public long getMealId() {
        return this.meal.getId();
    }

    public String getRecipe() {
        return this.meal.getRecipe();
    }
}
