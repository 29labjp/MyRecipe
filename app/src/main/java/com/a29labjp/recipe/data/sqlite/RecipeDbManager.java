package com.a29labjp.recipe.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a29labjp.recipe.data.model.Recipe;
import com.a29labjp.recipe.data.model.RecipeMaterial;
import com.a29labjp.recipe.data.model.RecipeMaterialRelation;
import com.a29labjp.recipe.data.model.RecipeStep;
import com.a29labjp.recipe.data.model.RecipeTag;
import com.a29labjp.recipe.data.model.RecipeTagRelation;
import com.a29labjp.recipe.data.sqlite.RecipeDbConst.*;

import java.util.ArrayList;

public class RecipeDbManager {
    private static final String TAG = "RecipeDbManager";

    private RecipeDbHelper mDbHelper;

    public RecipeDbManager(Context context) {
        mDbHelper = new RecipeDbHelper(context);
    }

    public ArrayList<Recipe> queryRecipeThumbs() {
        ArrayList<Recipe> ret = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                RecipeEntry.TABLE_NAME,
                null,
                null,
                null,
                null, null, null);

        if(cursor == null) {
            Log.i(TAG, "queryRecipeTable: no data");
            return ret;
        }
        while (cursor.moveToNext()) {
            Recipe recipe = makeRecipefromCursor(cursor);
            ret.add(recipe);
        }
        cursor.close();

        return ret;
    }


    public Recipe queryFullRecipe(long recipeId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Recipe recipe = queryRecipeTable(db, recipeId);
        if (recipe == null) {
            Log.i(TAG, "queryFullRecipe: no data");
            db.close();
            return null;
        }

        recipe.tagList = queryRecipeTagTable(db, recipeId);
        recipe.materialList = queryRecipeMaterialTable(db, recipeId);
        recipe.stepList = queryStepTable(db, recipeId);

        db.close();
        return recipe;
    }

    public Recipe insertRecipe(Recipe recipe) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long newRecipeId = insertRecipesTable(db, recipe);
        insertMaterialsTableAndRelation(db, newRecipeId, recipe.materialList);
        insertStepsTable(db, newRecipeId, recipe.stepList);
        insertTagsTableAndRelation(db, newRecipeId, recipe.tagList);

        db.close();
        return queryFullRecipe(newRecipeId);
    }

    public Recipe updateRecipe(Recipe recipe) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        updateRecipesTable(db, recipe);
        updateStepsTable(db, recipe.id, recipe.stepList);
        updateMaterialTableAndRelation(db, recipe.id, recipe.materialList);
        updateTagsTableAndRelation(db, recipe.id, recipe.tagList);
        db.close();
        return queryFullRecipe(recipe.id);
    }

    public void deleteRecipe(Recipe recipe) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long recipeId = recipe.id;
        deleteRecipesTable(db, recipeId);
        deleteRecipeMaterialsRelationTable(db, recipeId);
        deleteStepsTable(db, recipeId);
        deleteRecipeTagsRelationTable(db, recipeId);
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDbHelper.deleteAll(db);
        db.close();
    }

    private Recipe queryRecipeTable(SQLiteDatabase db, long recipeId){
        Recipe recipe = null;

        String selection = RecipeEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(recipeId) };
        Cursor cursor = db.query(
                RecipeEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null, null);

        if(cursor == null) {
            Log.i(TAG, "queryRecipeTable: no data");
            return null;
        }
        while (cursor.moveToNext()) {
            recipe = makeRecipefromCursor(cursor);
        }
        cursor.close();

        return recipe;
    }

    private Recipe makeRecipefromCursor(Cursor cursor){
        Recipe recipe = new Recipe();
        recipe.id = cursor.getLong(cursor.getColumnIndexOrThrow(RecipeEntry._ID));
        recipe.name = cursor.getString(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_NAME));
        recipe.imageUri = cursor.getString(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_IMAGE_URI));
        recipe.categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_CATEGORY_ID));
        recipe.rate = cursor.getInt(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_RATE));
        recipe.serves = cursor.getString(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_SERVES));
        recipe.comment = cursor.getString(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_COMMENT));
        recipe.source = cursor.getString(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_SOURCE));
        recipe.lastUpdated = cursor.getString(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_LAST_UPDATED));
        return recipe;
    }

    private ArrayList<RecipeTagRelation> queryRecipeTagTable(SQLiteDatabase db, long recipeId) {
        ArrayList<RecipeTagRelation> recipeTagRelations = new ArrayList<>();
        String selection = RecipeTagEntry.COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        Cursor cursor = db.query(
                RecipeTagEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                RecipeTagRelation recipeTagRelation = makeRecipeTagRelationfromCursor(cursor);
                recipeTagRelation.tag = queryTagTable(db,
                        cursor.getLong(cursor.getColumnIndexOrThrow(RecipeTagEntry.COLUMN_TAG_ID)));
                recipeTagRelations.add(recipeTagRelation);
            }
            cursor.close();
        }
        return recipeTagRelations;
    }

    private RecipeTag queryTagTable(SQLiteDatabase db, long tagId) {
        RecipeTag tag = null;

        String selection = TagEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(tagId)};
        Cursor cursor = db.query(
                TagEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                tag = makeRecipeTagfromCursor(cursor);
            }
            cursor.close();
        }
        return tag;
    }

    private RecipeTagRelation makeRecipeTagRelationfromCursor(Cursor cursor){
        RecipeTagRelation recipeTagRelation = new RecipeTagRelation();
        recipeTagRelation.id = cursor.getLong(cursor.getColumnIndexOrThrow(RecipeTagEntry._ID));
        return recipeTagRelation;
    }

    private RecipeTag makeRecipeTagfromCursor(Cursor cursor){
        RecipeTag recipeTag = new RecipeTag();
        recipeTag.id = cursor.getLong(cursor.getColumnIndexOrThrow(TagEntry._ID));
        recipeTag.name = cursor.getString(cursor.getColumnIndexOrThrow(TagEntry.COLUMN_NAME));
        return recipeTag;
    }


    private ArrayList<RecipeMaterialRelation>  queryRecipeMaterialTable(SQLiteDatabase db, long recipeId) {
        ArrayList<RecipeMaterialRelation> materialList = new ArrayList<>();
        String selection = RecipeMaterialEntry.COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        Cursor cursor = db.query(
                RecipeMaterialEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                RecipeMaterialRelation materialRelation = makeRecipeMaterialRelationFromCursor(cursor);
                materialRelation.material = queryRecipeMaterialRelation(db,
                        cursor.getLong(cursor.getColumnIndexOrThrow(RecipeMaterialEntry.COLUMN_MATERIAL_ID)));
                materialList.add(materialRelation);
            }
            cursor.close();
        }
        return materialList;
    }

    private RecipeMaterialRelation makeRecipeMaterialRelationFromCursor(Cursor cursor) {
        RecipeMaterialRelation recipeMaterialRelation = new RecipeMaterialRelation();
        recipeMaterialRelation.id = cursor.getLong(cursor.getColumnIndexOrThrow(RecipeMaterialEntry._ID));
        recipeMaterialRelation.group = cursor.getString(cursor.getColumnIndexOrThrow(RecipeMaterialEntry.COLUMN_GROUP));
        recipeMaterialRelation.quantity = cursor.getString(cursor.getColumnIndexOrThrow(RecipeMaterialEntry.COLUMN_QUANTITY));
        return recipeMaterialRelation;
    }

    private RecipeMaterial queryRecipeMaterialRelation(SQLiteDatabase db, long materialId) {
        RecipeMaterial material = null;

        String selection = RecipeEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(materialId)};
        Cursor cursor = db.query(
                MaterialEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null, null);

        if (cursor == null) {
            Log.i(TAG, "queryRecipeMaterialRelation: no data");
            return null;
        }
        while (cursor.moveToNext()) {
            material = makeRecipeMaterialFromCursor(cursor);
        }
        cursor.close();

        return material;
    }

    private RecipeMaterial makeRecipeMaterialFromCursor(Cursor cursor) {
        RecipeMaterial recipeMaterial = new RecipeMaterial();
        recipeMaterial.id = cursor.getLong(cursor.getColumnIndexOrThrow(MaterialEntry._ID));
        recipeMaterial.name = cursor.getString(cursor.getColumnIndexOrThrow(MaterialEntry.COLUMN_NAME));
        return recipeMaterial;
    }

    private ArrayList<RecipeMaterial> queryMaterialTable(SQLiteDatabase db, long[] tagIds) {
        ArrayList<RecipeMaterial> materialList = new ArrayList<>();

        StringBuilder selectionBuilder  = new StringBuilder();
        selectionBuilder.append(MaterialEntry._ID + " IN (");
        String[] selectionArgs = new String[tagIds.length];
        for (int i = 0; i < tagIds.length; i++) {
            selectionBuilder.append("?,");
            selectionArgs[i] = String.valueOf(tagIds[i]);
        }
        selectionBuilder.deleteCharAt(selectionBuilder.length()-1);
        selectionBuilder.append(")");
        String selection = selectionBuilder.toString();
        Cursor cursor = db.query(
                MaterialEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                RecipeMaterial material = makeRecipeMaterialfromCursor(cursor);
                materialList.add(material);
            }
            cursor.close();
        }
        return materialList;
    }

    private RecipeMaterial makeRecipeMaterialfromCursor(Cursor cursor){
        RecipeMaterial recipeMaterial = new RecipeMaterial();
        recipeMaterial.id = cursor.getLong(cursor.getColumnIndexOrThrow(MaterialEntry._ID));
        recipeMaterial.name = cursor.getString(cursor.getColumnIndexOrThrow(MaterialEntry.COLUMN_NAME));
        return recipeMaterial;
    }

    private ArrayList<RecipeStep> queryStepTable(SQLiteDatabase db, long recipeId) {
        ArrayList<RecipeStep> stepList = new ArrayList<>();
        String selection = StepEntry.COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        Cursor cursor = db.query(
                StepEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                RecipeStep step = makeRecipeStepfromCursor(cursor);
                stepList.add(step);
            }
            cursor.close();
        }
        return stepList;
    }

    private RecipeStep makeRecipeStepfromCursor(Cursor cursor){
        RecipeStep recipeStep = new RecipeStep();
        recipeStep.id = cursor.getLong(cursor.getColumnIndexOrThrow(StepEntry._ID));
        recipeStep.no = cursor.getInt(cursor.getColumnIndexOrThrow(StepEntry.COLUMN_NO));
        recipeStep.content = cursor.getString(cursor.getColumnIndexOrThrow(StepEntry.COLUMN_CONTENT));
        return recipeStep;
    }

    private ContentValues convertRecipeForDbValue(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(RecipeEntry.COLUMN_NAME, recipe.name);
        values.put(RecipeEntry.COLUMN_IMAGE_URI, recipe.imageUri);
        values.put(RecipeEntry.COLUMN_CATEGORY_ID, recipe.categoryId);
        values.put(RecipeEntry.COLUMN_RATE, recipe.rate);
        values.put(RecipeEntry.COLUMN_SERVES, recipe.serves);
        values.put(RecipeEntry.COLUMN_COMMENT, recipe.comment);
        values.put(RecipeEntry.COLUMN_SOURCE, recipe.source);
        values.put(RecipeEntry.COLUMN_LAST_UPDATED, recipe.lastUpdated);
        return values;
    }

    private long insertRecipesTable(SQLiteDatabase db, Recipe recipe) {
        long newId = db.insert(RecipeEntry.TABLE_NAME, null, convertRecipeForDbValue(recipe));
        return newId;
    }

    private void insertMaterialsTableAndRelation(SQLiteDatabase db, long recipeId, ArrayList<RecipeMaterialRelation> materialList) {
        for(RecipeMaterialRelation recipeMaterialRelation : materialList) {
            ContentValues values = new ContentValues();
            values.put(MaterialEntry.COLUMN_NAME, recipeMaterialRelation.material.name);
            long newId = -1;
            try {
                newId = db.insertWithOnConflict(MaterialEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ABORT);
            } catch (SQLException ex){
                String selection = MaterialEntry.COLUMN_NAME + " = ?";
                String[] selectionArgs = new String[]{recipeMaterialRelation.material.name};
                Cursor cursor = db.query(
                        MaterialEntry.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        newId = cursor.getLong(cursor.getColumnIndexOrThrow(MaterialEntry._ID));
                    }
                    cursor.close();
                }
            }
            // TODO: newId == -1 の場合は予期せぬエラーなのでトランザクションロールバックすべき
            if (newId != -1) {
                ContentValues relateValues = new ContentValues();
                relateValues.put(RecipeMaterialEntry.COLUMN_RECIPE_ID, recipeId);
                relateValues.put(RecipeMaterialEntry.COLUMN_MATERIAL_ID, newId);
                relateValues.put(RecipeMaterialEntry.COLUMN_GROUP, recipeMaterialRelation.group);
                relateValues.put(RecipeMaterialEntry.COLUMN_QUANTITY, recipeMaterialRelation.quantity);
                db.insert(RecipeMaterialEntry.TABLE_NAME, null, relateValues);
            }
        }
    }

    private void insertStepsTable(SQLiteDatabase db, long recipeId, ArrayList<RecipeStep> stepList) {
        for(RecipeStep recipeStep : stepList) {
            insertStepsTable(db, recipeId, recipeStep);
        }
    }

    private long insertStepsTable(SQLiteDatabase db, long recipeId, RecipeStep recipeStep) {
        ContentValues values = new ContentValues();
        values.put(StepEntry.COLUMN_RECIPE_ID, recipeId);
        values.put(StepEntry.COLUMN_NO, recipeStep.no);
        values.put(StepEntry.COLUMN_CONTENT, recipeStep.content);

        long ret = -1;
        try {
            ret = db.insertWithOnConflict(StepEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ABORT);
        } catch (SQLException e) {

            String selection = StepEntry.COLUMN_RECIPE_ID + " = ? AND " + StepEntry.COLUMN_NO + " = ?";
            String[] selectionArgs = {String.valueOf(recipeId), String.valueOf(recipeStep.no)};
            Cursor cursor = db.query(StepEntry.TABLE_NAME, null, selection, selectionArgs, null, null , null);

            if(cursor != null) {
                while(cursor.moveToNext()) {
                    ret = cursor.getLong(cursor.getColumnIndexOrThrow(StepEntry._ID));
                    String oldContent = cursor.getString(cursor.getColumnIndexOrThrow(StepEntry.COLUMN_CONTENT));
                    boolean needUpdate = false;
                    if(recipeStep.content == null) {
                        if(oldContent != null) {
                            needUpdate = true;
                        }
                    }

                    if(!recipeStep.content.equals(oldContent)) {
                        needUpdate = true;
                    }

                    if(needUpdate) {
                        String selectionForUpdate = StepEntry._ID + " = ?";
                        String[] selectionArgsForUpdate = {String.valueOf(ret)};
                        db.update(StepEntry.TABLE_NAME, values, selectionForUpdate, selectionArgsForUpdate);
                    }
                }
            }
        }
        return ret;
    }

    private void insertTagsTableAndRelation(SQLiteDatabase db, long recipeId, ArrayList<RecipeTagRelation> tagList) {
        for(RecipeTagRelation recipeTagRelation : tagList) {
            long newId = insertTagsTable(db, recipeTagRelation.tag);
            insertTagsTableAndRelation(db, recipeId, newId);
        }
    }

    private long insertTagsTable(SQLiteDatabase db, RecipeTag recipeTag) {
        ContentValues values = new ContentValues();
        values.put(TagEntry.COLUMN_NAME, recipeTag.name);
        long newId = -1;
        try {
            newId = db.insertWithOnConflict(TagEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ABORT);
        } catch (SQLException ex){
            String selection = TagEntry.COLUMN_NAME + " = ?";
            String[] selectionArgs = new String[]{recipeTag.name};
            Cursor cursor = db.query(
                    TagEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    newId = cursor.getLong(cursor.getColumnIndexOrThrow(TagEntry._ID));
                }
                cursor.close();
            }
        }
        return newId;
    }

    private long insertTagsTableAndRelation(SQLiteDatabase db, long recipeId, long newId) {
        // TODO: newId == -1 の場合は予期せぬエラーなのでトランザクションロールバックすべき
        if (newId == -1) {
            return 0;
        }
        ContentValues relateValues = new ContentValues();
        relateValues.put(RecipeTagEntry.COLUMN_RECIPE_ID, recipeId);
        relateValues.put(RecipeTagEntry.COLUMN_TAG_ID, newId);
        long ret = db.insertWithOnConflict(RecipeTagEntry.TABLE_NAME, null, relateValues, SQLiteDatabase.CONFLICT_IGNORE);
        return ret;
    }


    private long updateRecipesTable(SQLiteDatabase db, Recipe recipe) {
        String selection = RecipeEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipe.id)};
        int result = db.update(
                RecipeEntry.TABLE_NAME,
                convertRecipeForDbValue(recipe),
                selection,
                selectionArgs);
        return result;
    }

    private void updateStepsTable(SQLiteDatabase db, long recipeId, ArrayList<RecipeStep> newSteps) {
        ArrayList<RecipeStep> oldSteps = queryStepTable(db, recipeId);
        insertStepsTable(db, recipeId, newSteps);
        if(oldSteps.size() > newSteps.size()) {
            int deleteStart = oldSteps.size() - (oldSteps.size() - newSteps.size());
            String selection = StepEntry.COLUMN_RECIPE_ID + " =? AND " + StepEntry.COLUMN_NO + " > ?";
            String[] selectionArgs = {String.valueOf(recipeId), String.valueOf(deleteStart)};
            db.delete(
                    StepEntry.TABLE_NAME,
                    selection,
                    selectionArgs);
        }
    }

    private void updateTagsTableAndRelation(SQLiteDatabase db, long recipeId, ArrayList<RecipeTagRelation> tagRelations) {
        // tagの更新/削除はレシピからの更新ではなく、別途タグ管理から行う
        // そのため、tagに関しては 既存まま or 追加 のみ
        // Relationのみ更新/削除も行う
        ArrayList<RecipeTagRelation> oldTagRelations = queryRecipeTagTable(db, recipeId);

        // Relationに関しては、変更分を追加、不要分を削除で対応する
        insertTagsTableAndRelation(db, recipeId, tagRelations);
        for(RecipeTagRelation oldTagRelation : oldTagRelations) {
            boolean deleteFlag = true;
            for (RecipeTagRelation tagRelation : tagRelations) {
                if(oldTagRelation.tag.name.equals(tagRelation.tag.name)) {
                    deleteFlag = false;
                    break;
                }
            }
            if(deleteFlag) {
                String selection = RecipeTagEntry._ID + " = ?";
                String[] selectionArgs = {String.valueOf(oldTagRelation.id)};
                int result = db.delete(
                        RecipeTagEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
            }
        }
    }


    private void updateMaterialTableAndRelation(SQLiteDatabase db, long recipeId, ArrayList<RecipeMaterialRelation> materialRelations) {
        // materialの更新/削除はレシピからの更新ではなく、別途材料管理から行う
        // そのため、tagに関しては 既存まま or 追加 のみ
        // Relationのみ更新/削除も行う
        ArrayList<RecipeMaterialRelation> oldMaterialRelations = queryRecipeMaterialTable(db, recipeId);

        // Relationに関しては、変更分を追加、不要分を削除で対応する
        insertMaterialsTableAndRelation(db, recipeId, materialRelations);
        for(RecipeMaterialRelation oldMaterialRelation : oldMaterialRelations) {
            boolean deleteFlag = true;
            for (RecipeMaterialRelation materialRelation : materialRelations) {
                if(equalsMaterial(oldMaterialRelation, materialRelation)) {
                    deleteFlag = false;
                    break;
                }
            }
            if(deleteFlag) {
                String selection = RecipeMaterialEntry._ID + " = ?";
                String[] selectionArgs = {String.valueOf(oldMaterialRelation.id)};
                int result = db.delete(
                        RecipeMaterialEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                result = result;
            }
        }
    }

    private boolean equalsMaterial(RecipeMaterialRelation a, RecipeMaterialRelation b) {
        if(a == null || b == null) {
            return false;
        }
        if(a.material == null || b.material == null) {
            return false;
        }
        if(!a.material.name.equals(b.material.name)) {
            return false;
        }
        if(!a.group.equals(b.group)) {
            return false;
        }
        if(!a.quantity.equals(b.quantity)) {
            return false;
        }
        return true;
    }

    private int deleteRecipesTable(SQLiteDatabase db, long recipeId) {
        String selection = RecipeEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        int result = db.delete(
                RecipeEntry.TABLE_NAME,
                selection,
                selectionArgs);

        return result;
    }

    private int deleteRecipeMaterialsRelationTable(SQLiteDatabase db, long recipeId) {
        String selection = RecipeMaterialEntry.COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        int result = db.delete(
                RecipeMaterialEntry.TABLE_NAME,
                selection,
                selectionArgs);

        return result;
    }

    private int deleteStepsTable(SQLiteDatabase db, long recipeId) {
        String selection = StepEntry.COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        int result = db.delete(
                StepEntry.TABLE_NAME,
                selection,
                selectionArgs);

        return result;
    }

    private int deleteRecipeTagsRelationTable(SQLiteDatabase db, long recipeId) {
        String selection = RecipeTagEntry.COLUMN_RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        int result = db.delete(
                RecipeTagEntry.TABLE_NAME,
                selection,
                selectionArgs);

        return result;
    }
}
