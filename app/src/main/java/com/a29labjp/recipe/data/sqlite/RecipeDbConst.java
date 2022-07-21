package com.a29labjp.recipe.data.sqlite;

import android.provider.BaseColumns;

public class RecipeDbConst {

    public static class RecipeEntry implements BaseColumns {
        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE_URI = "image_uri";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_SERVES = "serves";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_LAST_UPDATED = "last_updated";
    }

    public static class MaterialEntry implements BaseColumns {
        public static final String TABLE_NAME = "materials";
        public static final String COLUMN_NAME = "name";
    }

    public static class StepEntry implements BaseColumns {
        public static final String TABLE_NAME = "steps";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_NO = "number";
        public static final String COLUMN_CONTENT = "content";
    }

    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME = "name";
    }

    public static class TagEntry implements BaseColumns {
        public static final String TABLE_NAME = "tags";
        public static final String COLUMN_NAME = "name";
    }

    public static class RecipeMaterialEntry implements BaseColumns {
        public static final String TABLE_NAME = "recipe_materials";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_MATERIAL_ID = "material_id";
        public static final String COLUMN_GROUP = "grouping";
        public static final String COLUMN_QUANTITY = "quantity";
    }

    public static class RecipeTagEntry implements BaseColumns {
        public static final String TABLE_NAME = "recipe_tags";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_TAG_ID = "tag_id";
    }
}
