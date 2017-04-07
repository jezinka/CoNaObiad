package com.projects.jezinka.conaobiad.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Dinner {

    private int id;
    private Meal meal;
    private Date date;

    public Dinner() {
        super();
    }

    public Dinner(int id, Meal meal, Date date) {
        this.id = id;
        this.meal = meal;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateString() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));
        StringBuffer row = new StringBuffer();

        row.append(new SimpleDateFormat("EEEE").format(date));
        row.append(" - ");
        row.append(df.format(date));
        return row.toString();
    }
}
