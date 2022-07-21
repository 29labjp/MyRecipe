package com.a29labjp.recipe.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.a29labjp.recipe.data.sqlite.RecipeDbConst.*;

public class RecipeDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Recipe.db";

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAllTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: If need
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void createAllTable(SQLiteDatabase db) {
        createRecipeTable(db);
        createMaterialTable(db);
        createStepTable(db);
        createCategoryTable(db);
        createTagTable(db);
        createRecipeMaterialTable(db);
        createRecipeTagTable(db);
    }

    public void deleteAll(SQLiteDatabase db) {
        db.delete(RecipeEntry.TABLE_NAME, null, null);
        db.delete(MaterialEntry.TABLE_NAME, null, null);
        db.delete(StepEntry.TABLE_NAME, null, null);
        db.delete(CategoryEntry.TABLE_NAME, null, null);
        db.delete(TagEntry.TABLE_NAME, null, null);
        db.delete(RecipeMaterialEntry.TABLE_NAME, null, null);
        db.delete(RecipeTagEntry.TABLE_NAME, null, null);
    }

    private void createRecipeTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                RecipeEntry._ID + " INTEGER PRIMARY KEY," +
                RecipeEntry.COLUMN_NAME + " TEXT NOT NULL," +
                RecipeEntry.COLUMN_IMAGE_URI + " TEXT," +
                RecipeEntry.COLUMN_CATEGORY_ID + " INTEGER," +
                RecipeEntry.COLUMN_RATE + " INTEGER," +
                RecipeEntry.COLUMN_SERVES + " TEXT," +
                RecipeEntry.COLUMN_COMMENT + " TEXT," +
                RecipeEntry.COLUMN_SOURCE + " TEXT," +
                RecipeEntry.COLUMN_LAST_UPDATED + " TEXT);");
    }

    private void createMaterialTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MaterialEntry.TABLE_NAME + " (" +
                MaterialEntry._ID + " INTEGER PRIMARY KEY," +
                MaterialEntry.COLUMN_NAME + " TEXT NOT NULL," +
                "UNIQUE(" + MaterialEntry.COLUMN_NAME + "));");
    }

    private void createStepTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + StepEntry.TABLE_NAME + " (" +
                StepEntry._ID + " INTEGER PRIMARY KEY," +
                StepEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL," +
                StepEntry.COLUMN_NO + " INTEGER NOT NULL," +
                StepEntry.COLUMN_CONTENT + " TEXT," +
                "UNIQUE(" + StepEntry.COLUMN_RECIPE_ID + "," +
                            StepEntry.COLUMN_NO + "));");
    }

    private void createCategoryTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                CategoryEntry.COLUMN_NAME + " TEXT NOT NULL," +
                "UNIQUE(" + CategoryEntry.COLUMN_NAME + "));");
    }

    private void createTagTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TagEntry.TABLE_NAME + " (" +
                TagEntry._ID + " INTEGER PRIMARY KEY," +
                TagEntry.COLUMN_NAME + " TEXT NOT NULL," +
                "UNIQUE(" + TagEntry.COLUMN_NAME + "));");
    }

    private void createRecipeMaterialTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RecipeMaterialEntry.TABLE_NAME + " (" +
                RecipeMaterialEntry._ID + " INTEGER PRIMARY KEY," +
                RecipeMaterialEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL," +
                RecipeMaterialEntry.COLUMN_MATERIAL_ID + " INTEGER NOT NULL," +
                RecipeMaterialEntry.COLUMN_GROUP + " TEXT," +
                RecipeMaterialEntry.COLUMN_QUANTITY + " TEXT," +
                "UNIQUE(" + RecipeMaterialEntry.COLUMN_RECIPE_ID + "," +
                            RecipeMaterialEntry.COLUMN_MATERIAL_ID + "," +
                            RecipeMaterialEntry.COLUMN_GROUP + "," +
                            RecipeMaterialEntry.COLUMN_QUANTITY + "));");
    }

    private void createRecipeTagTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RecipeTagEntry.TABLE_NAME + " (" +
                RecipeTagEntry._ID + " INTEGER PRIMARY KEY," +
                RecipeTagEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL," +
                RecipeTagEntry.COLUMN_TAG_ID + " INTEGER NOT NULL," +
                "UNIQUE(" + RecipeTagEntry.COLUMN_RECIPE_ID + "," +
                            RecipeTagEntry.COLUMN_TAG_ID + "));");
    }
}
