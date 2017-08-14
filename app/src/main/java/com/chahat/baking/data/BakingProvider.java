package com.chahat.baking.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by chahat on 27/7/17.
 */

public class BakingProvider extends ContentProvider {

    private BakingDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new BakingDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(BakingContract.IngredientEntry.TABLE_NAME,
                strings,s,strings1,null,null,s1);

        Log.d("inquery","inquery");

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(BakingContract.IngredientEntry.TABLE_NAME,null,contentValues);

        if (id!=0){
            Log.d("inserted",id+"");
            Uri returnUri = ContentUris.withAppendedId(BakingContract.IngredientEntry.CONTENT_URI,id);
            return returnUri;
        }else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deleted = db.delete(BakingContract.IngredientEntry.TABLE_NAME,s,strings);
        return  deleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int updated = db.update(BakingContract.IngredientEntry.TABLE_NAME,contentValues,s,strings);
        Log.d("updated",updated+"");

        return updated;
    }
}
