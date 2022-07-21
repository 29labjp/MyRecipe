package com.a29labjp.recipe.data;

import android.content.Context;

import com.a29labjp.recipe.data.model.Recipe;

import java.util.ArrayList;

public class RecipeProvider {

    private Context mContext;

    public RecipeProvider(Context context) {
        mContext = context;
    }

    public ArrayList<Recipe> getForList() {
        ArrayList<Recipe> ret = new ArrayList<>();
        return ret;
    }

    public Recipe queryFromId(int id) {
        Recipe ret = null;
        return ret;
    }

    public void insert(Recipe recipe) {

    }

    public void update(Recipe recipe) {

    }

    public void delete(Recipe recipe) {

    }
}
