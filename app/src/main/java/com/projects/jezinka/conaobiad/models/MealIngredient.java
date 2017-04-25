package com.projects.jezinka.conaobiad.models;

public class MealIngredient {
    private int mealId;
    private int ingredientId;

    public MealIngredient(int mealId, int ingredientId) {
        this.mealId = mealId;
        this.ingredientId = ingredientId;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }
}
