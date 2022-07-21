package com.a29labjp.recipe.data.sqlite;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.a29labjp.recipe.data.model.Recipe;
import com.a29labjp.recipe.data.model.RecipeMaterial;
import com.a29labjp.recipe.data.model.RecipeMaterialRelation;
import com.a29labjp.recipe.data.model.RecipeStep;
import com.a29labjp.recipe.data.model.RecipeTag;
import com.a29labjp.recipe.data.model.RecipeTagRelation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class RecipeDbManagerTest {
    RecipeDbManager recipeDbManager;

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        recipeDbManager = new RecipeDbManager(context);
        recipeDbManager.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void queryRecipeThumbs() {
        Recipe inputRecipe = makeNewRecipe();
        recipeDbManager.insertRecipe(inputRecipe);
        ArrayList<Recipe> recipesAfterInsert = recipeDbManager.queryRecipeThumbs();
        assertThat(recipesAfterInsert.size(), is(1));

        recipeDbManager.insertRecipe(inputRecipe);
        ArrayList<Recipe> recipesAfterInsert2 = recipeDbManager.queryRecipeThumbs();
        assertThat(recipesAfterInsert2.size(), is(2));
    }

    @Test
    public void queryFullRecipe() {
        Recipe inputRecipe = makeNewRecipe();
        recipeDbManager.insertRecipe(inputRecipe);
        ArrayList<Recipe> recipesAfterInsert = recipeDbManager.queryRecipeThumbs();
        assertThat(recipesAfterInsert.get(0).tagList.size(), is(0));
        assertThat(recipesAfterInsert.get(0).materialList.size(), is(0));
        assertThat(recipesAfterInsert.get(0).stepList.size(), is(0));

        Recipe resultRecipe = recipeDbManager.queryFullRecipe(recipesAfterInsert.get(0).id);
        assertThat(resultRecipe.tagList.size(), is(2));
        assertThat(resultRecipe.materialList.size(), is(2));
        assertThat(resultRecipe.stepList.size(), is(2));
    }

    @Test
    public void insertRecipe() {
        Recipe inputRecipe = makeNewRecipe();
        Recipe resultRecipe = recipeDbManager.insertRecipe(inputRecipe);

        assertThat(resultRecipe.id, is((long)1));
        assertThat(resultRecipe.name, is(inputRecipe.name));
        assertThat(resultRecipe.imageUri, is(inputRecipe.imageUri));
        assertThat(resultRecipe.categoryId, is(inputRecipe.categoryId));
        assertThat(resultRecipe.rate, is(inputRecipe.rate));
        assertThat(resultRecipe.serves, is(inputRecipe.serves));
        assertThat(resultRecipe.comment, is(inputRecipe.comment));
        assertThat(resultRecipe.source, is(inputRecipe.source));
        assertThat(resultRecipe.lastUpdated, is(inputRecipe.lastUpdated));
        assertThat(resultRecipe.tagList.size(), is(inputRecipe.tagList.size()));
        for(int i=0; i<inputRecipe.tagList.size(); i++) {
            assertThat(resultRecipe.tagList.get(i).tag.id, is((long)i+1));
            assertThat(resultRecipe.tagList.get(i).tag.name, is(inputRecipe.tagList.get(i).tag.name));
        }
        assertThat(resultRecipe.stepList.size(), is(inputRecipe.stepList.size()));
        for(int i=0; i<inputRecipe.stepList.size(); i++) {
            assertThat(resultRecipe.stepList.get(i).no, is(inputRecipe.stepList.get(i).no));
            assertThat(resultRecipe.stepList.get(i).content, is(inputRecipe.stepList.get(i).content));
        }
        assertThat(resultRecipe.materialList.size(), is(inputRecipe.materialList.size()));
        for(int i=0; i<inputRecipe.materialList.size(); i++) {
            assertThat(resultRecipe.materialList.get(i).material.id, is((long)i+1));
            assertThat(resultRecipe.materialList.get(i).material.name, is(inputRecipe.materialList.get(i).material.name));
            assertThat(resultRecipe.materialList.get(i).quantity, is(inputRecipe.materialList.get(i).quantity));
            assertThat(resultRecipe.materialList.get(i).group, is(inputRecipe.materialList.get(i).group));
        }
    }

    @Test
    public void insertRecipe_onlyName() {
        Recipe inputRecipe = new Recipe();
        inputRecipe.name = "name";
        Recipe resultRecipe = recipeDbManager.insertRecipe(inputRecipe);

        assertThat(resultRecipe.id, is((long)1));
        assertThat(resultRecipe.name, is(inputRecipe.name));
        assertThat(resultRecipe.tagList.size(), is(0));
        assertThat(resultRecipe.stepList.size(), is(0));
        assertThat(resultRecipe.materialList.size(), is(0));
    }

    @Test
    public void insertRecipe_withConflict_Recipe() {
        Recipe inputRecipe = makeNewRecipe();
        Recipe resultRecipe = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(resultRecipe.id, is((long)1));

        Recipe resultRecipe2 = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(resultRecipe2.id, is((long)2));
    }

    @Test
    public void insertRecipe_withConflict_Tag() {
        Recipe inputRecipe = makeNewRecipe();
        Recipe resultRecipe = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(resultRecipe.id, is((long)1));

        String orgTagName = inputRecipe.tagList.get(0).tag.name;
        inputRecipe.tagList.clear();
        inputRecipe.tagList.add(makeRecipeTagRelation(orgTagName));
        inputRecipe.tagList.add(makeRecipeTagRelation("tag_diff"));

        Recipe resultRecipe2 = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(resultRecipe2.id, is((long)2));
        assertThat(resultRecipe2.tagList.size(), is(inputRecipe.tagList.size()));
        assertThat(resultRecipe2.tagList.get(0).tag.id, is((long)1));
        assertThat(resultRecipe2.tagList.get(0).tag.name, is(inputRecipe.tagList.get(0).tag.name));
        assertThat(resultRecipe2.tagList.get(1).tag.id, is((long)3));
        assertThat(resultRecipe2.tagList.get(1).tag.name, is(inputRecipe.tagList.get(1).tag.name));
    }

    @Test
    public void insertRecipe_withConflict_Material() {
        Recipe inputRecipe = makeNewRecipe();
        Recipe resultRecipe = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(resultRecipe.id, is((long)1));

        String orgMaterialName = inputRecipe.materialList.get(0).material.name;
        inputRecipe.materialList.clear();
        inputRecipe.materialList.add(makeRecipeMaterialRelation(orgMaterialName, "quantity_diff", "tag_diff"));
        inputRecipe.materialList.add(makeRecipeMaterialRelation("name_diff", "quantity", "A"));

        Recipe resultRecipe2 = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(resultRecipe2.id, is((long)2));
        assertThat(resultRecipe2.materialList.size(), is(inputRecipe.materialList.size()));
        assertThat(resultRecipe2.materialList.get(0).material.id, is((long)1));
        assertThat(resultRecipe2.materialList.get(0).group, is(inputRecipe.materialList.get(0).group));
        assertThat(resultRecipe2.materialList.get(0).quantity, is(inputRecipe.materialList.get(0).quantity));
        assertThat(resultRecipe2.materialList.get(0).material.name, is(inputRecipe.materialList.get(0).material.name));
        assertThat(resultRecipe2.materialList.get(1).material.id, is((long)3));
        assertThat(resultRecipe2.materialList.get(1).group, is(inputRecipe.materialList.get(1).group));
        assertThat(resultRecipe2.materialList.get(1).quantity, is(inputRecipe.materialList.get(1).quantity));
        assertThat(resultRecipe2.materialList.get(1).material.name, is(inputRecipe.materialList.get(1).material.name));
    }

    @Test
    public void updateRecipe_updateRecipeTable() {
        Recipe inputRecipe = makeNewRecipe();
        Recipe recipeAfterInsert = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(recipeAfterInsert.id, is((long)1));
        assertThat(recipeAfterInsert.name, is(inputRecipe.name));

        recipeAfterInsert.name = "name_update";
        Recipe recipeAfterUpdate = recipeDbManager.updateRecipe(recipeAfterInsert);
        assertThat(recipeAfterUpdate.id, is((long)1));
        assertThat(recipeAfterUpdate.name, is(recipeAfterInsert.name));
    }

    @Test
    public void updateRecipe_updateStepTable() {
        Recipe inputRecipe = makeNewRecipe();
        Recipe recipeAfterInsert = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(recipeAfterInsert.id, is((long)1));
        assertThat(recipeAfterInsert.name, is(inputRecipe.name));
        int tmpOrgStepNo = recipeAfterInsert.stepList.get(0).no;
        String tmpOrgStepContent = recipeAfterInsert.stepList.get(0).content;

        recipeAfterInsert.stepList.get(0).content = "content_diff";
        recipeAfterInsert.stepList.add(makeRecipeStep(3, "content3"));
        Recipe recipeAfterUpdate = recipeDbManager.updateRecipe(recipeAfterInsert);
        assertThat(recipeAfterUpdate.stepList.size(), is(3));
        assertThat(recipeAfterUpdate.stepList.get(0).id, is((long)1));
        assertThat(recipeAfterUpdate.stepList.get(0).content, is(recipeAfterInsert.stepList.get(0).content));
        assertThat(recipeAfterUpdate.stepList.get(2).id, is((long)3));
        assertThat(recipeAfterUpdate.stepList.get(2).content, is(recipeAfterInsert.stepList.get(2).content));

        recipeAfterInsert.stepList.clear();
        recipeAfterInsert.stepList.add(makeRecipeStep(tmpOrgStepNo,tmpOrgStepContent));
        Recipe recipeAfterUpdate2 = recipeDbManager.updateRecipe(recipeAfterInsert);
        assertThat(recipeAfterUpdate2.stepList.size(), is(1));
        assertThat(recipeAfterUpdate2.stepList.get(0).id, is((long)1));
        assertThat(recipeAfterUpdate2.stepList.get(0).content, is(recipeAfterInsert.stepList.get(0).content));
    }

    @Test
    public void updateRecipe_updateTagTable() {
        Recipe inputRecipe = makeNewRecipe();
        Recipe recipeAfterInsert = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(recipeAfterInsert.id, is((long)1));
        assertThat(recipeAfterInsert.name, is(inputRecipe.name));
        String tmpOrgTagName = recipeAfterInsert.tagList.get(0).tag.name;

        recipeAfterInsert.tagList.get(0).tag.name = "tag_diff";
        recipeAfterInsert.tagList.add(makeRecipeTagRelation("tag3"));
        Recipe recipeAfterUpdate = recipeDbManager.updateRecipe(recipeAfterInsert);
        assertThat(recipeAfterUpdate.tagList.size(), is(3));

        recipeAfterInsert.tagList.clear();
        recipeAfterInsert.tagList.add(makeRecipeTagRelation("tag4"));
        Recipe recipeAfterUpdate2 = recipeDbManager.updateRecipe(recipeAfterInsert);
        assertThat(recipeAfterUpdate2.tagList.size(), is(1));

        recipeAfterInsert.tagList.clear();
        recipeAfterInsert.tagList.add(makeRecipeTagRelation(tmpOrgTagName));
        Recipe recipeAfterUpdate3 = recipeDbManager.updateRecipe(recipeAfterInsert);
        assertThat(recipeAfterUpdate3.tagList.size(), is(1));
    }

    @Test
    public void updateRecipe_updateMaterialTable() {
        Recipe inputRecipe = makeNewRecipe();
        Recipe recipeAfterInsert = recipeDbManager.insertRecipe(inputRecipe);
        assertThat(recipeAfterInsert.id, is((long)1));
        assertThat(recipeAfterInsert.name, is(inputRecipe.name));
        String tmpOrgMaterialName = recipeAfterInsert.materialList.get(0).material.name;
        String tmpOrgMaterialGroup = recipeAfterInsert.materialList.get(0).group;
        String tmpOrgMaterialQuantity= recipeAfterInsert.materialList.get(0).quantity;

        recipeAfterInsert.materialList.get(0).material.name = "material_diff";
        recipeAfterInsert.materialList.add(makeRecipeMaterialRelation("material3", "quantily3", "group3"));
        Recipe recipeAfterUpdate = recipeDbManager.updateRecipe(recipeAfterInsert);
        assertThat(recipeAfterUpdate.materialList.size(), is(3));

        recipeAfterInsert.materialList.clear();
        recipeAfterInsert.materialList.add(makeRecipeMaterialRelation("material4", "quantily4", "group4"));
        Recipe recipeAfterUpdate2 = recipeDbManager.updateRecipe(recipeAfterInsert);
        assertThat(recipeAfterUpdate2.materialList.size(), is(1));

        recipeAfterInsert.materialList.clear();
        recipeAfterInsert.materialList.add(makeRecipeMaterialRelation(tmpOrgMaterialName, tmpOrgMaterialQuantity, tmpOrgMaterialGroup));
        Recipe recipeAfterUpdate3 = recipeDbManager.updateRecipe(recipeAfterInsert);
        assertThat(recipeAfterUpdate3.materialList.size(), is(1));
    }

    @Test
    public void deleteRecipe() {
        Recipe inputRecipe = makeNewRecipe();
        recipeDbManager.insertRecipe(inputRecipe);
        ArrayList<Recipe> recipesAfterInsert = recipeDbManager.queryRecipeThumbs();
        assertThat(recipesAfterInsert.size(), is(1));

        recipeDbManager.deleteRecipe(recipesAfterInsert.get(0));
        ArrayList<Recipe> recipesAfterDelete= recipeDbManager.queryRecipeThumbs();
        assertThat(recipesAfterDelete.size(), is(0));
    }

    private Recipe makeNewRecipe(){
        Recipe recipe = new Recipe();
        recipe.id = -1;
        recipe.name = "name";
        recipe.imageUri = "imageUri";
        recipe.categoryId = 0;

        ArrayList<RecipeTagRelation> tagRelations = new ArrayList<>();
        tagRelations.add(makeRecipeTagRelation("tag1"));
        tagRelations.add(makeRecipeTagRelation("tag2"));
        recipe.tagList = tagRelations;

        recipe.rate = 3;
        recipe.serves = "2";

        ArrayList<RecipeStep> steps = new ArrayList<>();
        steps.add(makeRecipeStep(1, "content1"));
        steps.add(makeRecipeStep(2, "content2"));
        recipe.stepList = steps;

        ArrayList<RecipeMaterialRelation> materials = new ArrayList<>();
        materials.add(makeRecipeMaterialRelation("material1", "1", "A"));
        materials.add(makeRecipeMaterialRelation("material2", "2", "B"));
        recipe.materialList = materials;

        recipe.comment = "comment";
        recipe.source = "sourceURL";
        recipe.lastUpdated = "YYYY/MM/DD";
        return recipe;
    }

    private RecipeTagRelation makeRecipeTagRelation(String name){
        RecipeTagRelation tagRelation = new RecipeTagRelation();
        tagRelation.tag = makeRecipeTag(name);
        return tagRelation;
    }

    private RecipeTag makeRecipeTag(String name){
        RecipeTag tag = new RecipeTag();
        tag.id = -1;
        tag.name = name;
        return tag;
    }

    private RecipeStep makeRecipeStep(int no, String content){
        RecipeStep step = new RecipeStep();
        step.no = no;
        step.content = content;
        return step;
    }

    private RecipeMaterialRelation makeRecipeMaterialRelation(String name, String quantity, String group){
        RecipeMaterialRelation materialRelation = new RecipeMaterialRelation();
        materialRelation.material = makeRecipeMaterial(name);
        materialRelation.quantity = quantity;
        materialRelation.group = group;
        return materialRelation;
    }

    private RecipeMaterial makeRecipeMaterial(String name){
        RecipeMaterial material = new RecipeMaterial();
        material.id = -1;
        material.name = name;
        return material;
    }
}