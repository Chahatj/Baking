package com.chahat.baking;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.chahat.baking.Object.RecipeStepObject;
import com.chahat.baking.ui.RecipeDetailFragment;
import com.chahat.baking.utils.JsonUtils;
import com.chahat.baking.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnNextPrevClickListner {

    private String id;
    private String recipeId,recipeName;
    private boolean isIngredientShown;
    private static final int NEXTLOADER = 1;
    private static final int PREVLOADER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.recipe_detail);
        }

        Intent intent = null;

        if (savedInstanceState!=null){
            id = savedInstanceState.getString("Id");
            isIngredientShown = savedInstanceState.getBoolean("IngredientShown");
            recipeId = savedInstanceState.getString("recipeId");
            recipeName = savedInstanceState.getString("recipeName");
        }else {
            intent = getIntent();

            if (!intent.hasExtra("Id")) return;

            id = intent.getStringExtra("Id");
            recipeId = intent.getStringExtra("recipeId");
            isIngredientShown = intent.getBooleanExtra("IngredientShown",false);
            recipeName = intent.getStringExtra("recipeName");

        }



        if (savedInstanceState == null){

            RecipeDetailFragment fragment = new RecipeDetailFragment();
            fragment.setId(id);
            fragment.setRecipeName(recipeName);
            fragment.setIngredientShown(isIngredientShown);
            if (intent.hasExtra("VideoURL") && intent.hasExtra("Description")){
                fragment.setVideoURL(intent.getStringExtra("VideoURL"));
                fragment.setDescription(intent.getStringExtra("Description"));
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.video_container,fragment)
                    .commit();

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Id",id);
        outState.putString("recipeId",recipeId);
        outState.putBoolean("IngredientShown",isIngredientShown);
        outState.putString("recipeName",recipeName);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNextClick(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("recipeId",recipeId);
        bundle.putString("Id",id);
        if (getSupportLoaderManager().getLoader(NEXTLOADER)==null){
            getSupportLoaderManager().initLoader(NEXTLOADER,bundle,nextLoader);
        }else {
            getSupportLoaderManager().restartLoader(NEXTLOADER,bundle,nextLoader);
        }
    }

    @Override
    public void onPrevClick(String id) {

        Bundle bundle = new Bundle();
        bundle.putString("recipeId",recipeId);
        bundle.putString("Id",id);
        if (getSupportLoaderManager().getLoader(PREVLOADER)==null){
            getSupportLoaderManager().initLoader(PREVLOADER,bundle,prevLoader);
        }else {
            getSupportLoaderManager().restartLoader(PREVLOADER,bundle,prevLoader);
        }

    }

    private final LoaderManager.LoaderCallbacks<RecipeStepObject> nextLoader = new LoaderManager.LoaderCallbacks<RecipeStepObject>() {
        @Override
        public Loader<RecipeStepObject> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<RecipeStepObject>(getApplicationContext()) {

                RecipeStepObject recipeStepObject = null;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();

                    if (recipeStepObject!=null){
                        deliverResult(recipeStepObject);
                    }else {
                        forceLoad();
                    }
                }

                @Override
                public void deliverResult(RecipeStepObject data) {
                    super.deliverResult(data);
                    recipeStepObject = data;
                }

                @Override
                public RecipeStepObject loadInBackground() {

                    URL url = NetworkUtils.builtURL();
                    try {
                        String response = NetworkUtils.getResponseFromHttpURL(url);
                        return JsonUtils.getNextRecipeStep(response,args.getString("recipeId"),args.getString("Id"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<RecipeStepObject> loader, RecipeStepObject data) {

            if (data!=null){
                Message message = new Message();
                message.obj = data;
                handler.sendMessage(message);
            }
        }

        @Override
        public void onLoaderReset(Loader<RecipeStepObject> loader) {

        }
    };

    private final LoaderManager.LoaderCallbacks<RecipeStepObject> prevLoader = new LoaderManager.LoaderCallbacks<RecipeStepObject>() {
        @Override
        public Loader<RecipeStepObject> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<RecipeStepObject>(getApplicationContext()) {

                RecipeStepObject recipeStepObject = null;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();

                    if (recipeStepObject!=null){
                        deliverResult(recipeStepObject);
                    }else {
                        forceLoad();
                    }
                }

                @Override
                public void deliverResult(RecipeStepObject data) {
                    super.deliverResult(data);
                    recipeStepObject = data;
                }

                @Override
                public RecipeStepObject loadInBackground() {

                    URL url = NetworkUtils.builtURL();
                    try {
                        String response = NetworkUtils.getResponseFromHttpURL(url);
                        return JsonUtils.getPrevRecipeStep(response,args.getString("recipeId"),args.getString("Id"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<RecipeStepObject> loader, RecipeStepObject data) {

            if (data!=null){

                Message message = new Message();
                message.obj = data;
                handler.sendMessage(message);
            }
        }

        @Override
        public void onLoaderReset(Loader<RecipeStepObject> loader) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            RecipeStepObject recipeStepObject = (RecipeStepObject) msg.obj;
            showFragment(recipeStepObject);
        }
    };

    private void showFragment(RecipeStepObject data){
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setId(data.getId());
        fragment.setRecipeName(recipeName);
        fragment.setIngredientShown(isIngredientShown);
        fragment.setVideoURL(data.getVideoURL());
        fragment.setDescription(data.getDescription());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.video_container,fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler!=null){
            handler=null;
        }

    }
}
