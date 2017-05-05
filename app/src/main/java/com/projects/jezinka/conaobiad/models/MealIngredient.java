package com.projects.jezinka.conaobiad.models;

public class MealIngredient {
    private long mealId;
    private long ingredientId;

    public MealIngredient(int mealId, int ingredientId) {
        this.mealId = mealId;
        this.ingredientId = ingredientId;
    }

    public long getMealId() {
        return mealId;
    }

    public void setMealId(long mealId) {
        this.mealId = mealId;
    }

    public long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(long ingredientId) {
        this.ingredientId = ingredientId;
    }
}
