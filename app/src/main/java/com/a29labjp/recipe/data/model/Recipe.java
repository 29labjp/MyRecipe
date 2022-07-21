package com.a29labjp.recipe.data.model;

import java.util.ArrayList;

public class Recipe {
    /** レシピID */
    public long id;
    /** 料理名 */
    public String name;
    /** 画像URI */
    public String imageUri;
    /** カテゴリID */
    public int categoryId;
    /** タグリスト */
    public ArrayList<RecipeTagRelation> tagList = new ArrayList<>();
    /** 評価(0~5) */
    public int rate;
    /** 何人前か */
    public String serves;
    /** 材料リスト */
    public ArrayList<RecipeMaterialRelation> materialList = new ArrayList<>();
    /** 手順リスト */
    public ArrayList<RecipeStep> stepList = new ArrayList<>();
    /** コメント*/
    public String comment;
    /** 参考元 */
    public String source;
    /** 更新日 */
    public String lastUpdated;
}