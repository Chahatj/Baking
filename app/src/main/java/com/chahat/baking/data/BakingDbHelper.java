package com.chahat.baking.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chahat on 27/7/17.
 */

public class BakingDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "baking.db";
    private static final int DATABASE_VERSION = 1;

    public BakingDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_INGREDIENT_TABLE = "CREATE TABLE " +
                BakingContract.IngredientEntry.TABLE_NAME + " ( " +
                BakingContract.IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BakingContract.IngredientEntry.COLUMN_RECIPE_ID + " TEXT NOT NULL," +
                BakingContract.IngredientEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL," +
                BakingContract.IngredientEntry.COLUMN_INGREDIENT_QUANTITY + " TEXT NOT NULL," +
                BakingContract.IngredientEntry.COLUMN_INGREDIENT_MEASURE + " TEXT NOT NULL," +
                BakingContract.IngredientEntry.COLUMN_INGREDIENT_INGREDIENT + " TEXT NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(CREATE_INGREDIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
