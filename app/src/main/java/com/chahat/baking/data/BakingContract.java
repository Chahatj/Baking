package com.chahat.baking.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chahat on 27/7/17.
 */

public class BakingContract {

    private static final String CONTENT_AUTHORITY = "com.chahat.baking";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    private static final String PATH_INGREDIENT = "recipe_ingredient";

    public static class IngredientEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENT).build();

        public static final String TABLE_NAME = "ingredients";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_RECIPE_NAME = "recipe_name";
        public static final String COLUMN_INGREDIENT_QUANTITY = "ingredient_quantity";
        public static final String COLUMN_INGREDIENT_MEASURE = "ingredient_measure";
        public static final String COLUMN_INGREDIENT_INGREDIENT = "ingredient_ingredient";
    }
}
