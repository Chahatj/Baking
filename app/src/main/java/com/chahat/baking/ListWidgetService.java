package com.chahat.baking;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.chahat.baking.data.BakingContract;

/**
 * Created by chahat on 27/7/17.
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(this);
    }

    public class ListRemoteViewFactory implements RemoteViewsFactory{

        final Context mContext;
        Cursor mCursor;

        public ListRemoteViewFactory(Context context){
            mContext = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

            if (mCursor!=null) mCursor.close();

            mCursor = mContext.getContentResolver().query(BakingContract.IngredientEntry.CONTENT_URI,null,
                    null,null,null);
        }

        @Override
        public void onDestroy() {
            mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor!=null) return mCursor.getCount();
            else return 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {

            if (mCursor==null || mCursor.getCount()==0) return null;

            mCursor.moveToPosition(i);

            String quantity = mCursor.getString(mCursor.getColumnIndex(BakingContract.IngredientEntry.COLUMN_INGREDIENT_QUANTITY));
            String measure = mCursor.getString(mCursor.getColumnIndex(BakingContract.IngredientEntry.COLUMN_INGREDIENT_MEASURE));
            String ingredient = mCursor.getString(mCursor.getColumnIndex(BakingContract.IngredientEntry.COLUMN_INGREDIENT_INGREDIENT));

            RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.ingredient_item);

            remoteViews.setTextViewText(R.id.quantity_text_view,quantity);
            remoteViews.setTextViewText(R.id.measure_text_view,measure);
            remoteViews.setTextViewText(R.id.ingredient_text_view,ingredient);

            Intent intent = new Intent();
            intent.putExtra("Id",mCursor.getString(mCursor.getColumnIndex(BakingContract.IngredientEntry.COLUMN_RECIPE_ID)));
            intent.putExtra("recipeId",mCursor.getString(mCursor.getColumnIndex(BakingContract.IngredientEntry.COLUMN_RECIPE_ID)));
            intent.putExtra("IngredientShown",true);
            intent.putExtra("recipeName",mCursor.getString(mCursor.getColumnIndex(BakingContract.IngredientEntry.COLUMN_RECIPE_NAME)));

            remoteViews.setOnClickFillInIntent(R.id.ingredient_container,intent);

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
