package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Ingredient;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.models.MealIngredient;

import java.util.ArrayList;
import java.util.List;

public class MealIngredientContract extends BaseTable implements BaseColumns {

    String COLUMN_MEAL_ID;
    String COLUMN_INGREDIENT_ID;

    private MealContract mealContract;
    private IngredientContract ingredientContract;

    public MealIngredientContract() {

        this.TABLE_NAME = "meal_ingredient";
        this.COLUMN_INGREDIENT_ID = "ingredient_id";
        this.COLUMN_MEAL_ID = "meal_id";

        this.mealContract = new MealContract();
        this.ingredientContract = new IngredientContract();

        this.SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                this.COLUMN_INGREDIENT_ID + " int, " +
                this.COLUMN_MEAL_ID + " int, " +
                        " FOREIGN KEY(" + COLUMN_MEAL_ID + ") REFERENCES " + this.mealContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE," +
                        " FOREIGN KEY(" + COLUMN_INGREDIENT_ID + ") REFERENCES " + this.ingredientContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE" +
                ")";
    }

    @NonNull
    private String getIngredientsWithChecked() {
        return "select ingredient._id, ingredient.name, " +
                "  CASE WHEN EXISTS( " +
                "             SELECT 1 from ingredient as ingredient2    " +
                "               join meal_ingredient as meal_ingredient2  on ingredient2._id= meal_ingredient2.ingredient_id     " +
                "               join meal as meal2 on meal2._id= meal_ingredient2.meal_id     " +
                "               where ingredient2._id = ingredient._id and meal2._id = ? " +
                "        ) THEN 1 " +
                "        ELSE 0  " +
                "    END as checked  " +
                "  from ingredient " +
                " order by checked desc, ingredient.name COLLATE NOCASE";
    }

    @NonNull
    private String getIngredientsSortedQuery() {
        return getIngredientsQuery() + " order by 1 COLLATE NOCASE";
    }

    @NonNull
    private String getIngredientsQuery() {
        IngredientContract ingredientContract = this.ingredientContract;
        MealContract mealContract = this.mealContract;

        return "select " + ingredientContract.getTableName() + "." + ingredientContract.COLUMN_NAME +
                " from " + this.getTableName() +
                " join " + mealContract.getTableName() +
                " on " + mealContract.getTableName() + "." + _ID + "= " + this.getTableName() + "." + this.COLUMN_MEAL_ID +
                " join " + ingredientContract.getTableName() +
                " on " + ingredientContract.getTableName() + "." + _ID + "= " + this.getTableName() + "." + this.COLUMN_INGREDIENT_ID +
                " where " + mealContract.getTableName() + "." + _ID + "=? ";
    }

    public boolean insert(CoNaObiadDbHelper dbHelper, long mealId, long ingredientId) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("meal_id", mealId);
        contentValues.put("ingredient_id", ingredientId);

        dbHelper.insert(tableName, contentValues);
        return true;
    }

    @NonNull
    MealIngredient getFromCursor(Cursor res) {
        return new MealIngredient(0, 0);
    }

    public List<String> getIngredientsForMeal(Meal meal, SQLiteOpenHelper helper) {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(getIngredientsSortedQuery(), new String[]{Long.toString(meal.getId())});

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                array_list.add(res.getString(0));
            } while (res.moveToNext());
        }

        db.close();
        return array_list;
    }

    public Ingredient[] getIngredientsWithMeal(Meal meal, SQLiteOpenHelper helper) {
        ArrayList<Ingredient> array_list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(getIngredientsWithChecked(), new String[]{Long.toString(meal.getId())});

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                int id = res.getInt(0);
                String name = res.getString(1);
                int checked = res.getInt(2);
                array_list.add(new Ingredient(id, name, checked == 1));
            } while (res.moveToNext());
        }

        db.close();
        return array_list.toArray(new Ingredient[array_list.size()]);
    }

    public void deleteForMeal(CoNaObiadDbHelper helper, long mealId) {
        helper.delete(this.getTableName(), mealId, "meal_id");
    }

    public String getShoppingList(List<Meal> meals, SQLiteOpenHelper helper) {

        ArrayList<String> array_list = new ArrayList<>();

        String[] queryArgs = getMealIds(meals);
        String query = getShoppingListQuery(meals);

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(query, queryArgs);

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                String name = res.getString(0);
                int count = res.getInt(1);
                array_list.add(count + " x " + name + "\n");
            } while (res.moveToNext());
        }

        db.close();
        return TextUtils.join("\n", array_list);
    }

    @NonNull
    private String[] getMealIds(List<Meal> meals) {
        String[] stringArgs = new String[meals.size()];

        for (int i = 0; i < meals.size(); i++) {
            stringArgs[i] = Long.toString(meals.get(i).getId());
        }
        return stringArgs;
    }

    @NonNull
    private String getShoppingListQuery(List<Meal> meals) {
        StringBuffer sb = new StringBuffer();
        sb.append("select name, count(name) from (");

        for (int i = 0; i < meals.size(); i++) {

            sb.append(getIngredientsQuery());

            if (i < meals.size() - 1) {
                sb.append(" union all ");
            }
        }

        sb.append(") group by name order by name collate nocase;");
        return sb.toString();
    }
}
