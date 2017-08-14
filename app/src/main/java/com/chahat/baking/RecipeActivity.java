package com.chahat.baking;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.chahat.baking.Object.RecipeStepObject;
import com.chahat.baking.ui.RecipeDetailFragment;
import com.chahat.baking.ui.RecipeListFragment;

public class RecipeActivity extends AppCompatActivity implements RecipeListFragment.OnListItemClick,View.OnClickListener,RecipeDetailFragment.OnNextPrevClickListner{

    private boolean isPhone;
    private String id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        if (savedInstanceState!=null){
            id = savedInstanceState.getString("Id");
            name = savedInstanceState.getString("recipeName");
        }else {
            Intent intent = getIntent();

            if (!intent.hasExtra("recipeId")) return;

            id = intent.getStringExtra("recipeId");
            name = intent.getStringExtra("recipeName");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name);
        }

        isPhone = getResources().getBoolean(R.bool.is_phone);

        CardView cardView = (CardView) findViewById(R.id.ingredients_card_view);
        cardView.setOnClickListener(this);

        if (savedInstanceState==null){

            RecipeListFragment recipeListFragment = new RecipeListFragment();
            recipeListFragment.setId(id);

            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(R.id.recipeContainer,recipeListFragment)
                    .commit();

            if (!isPhone){
                RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                recipeDetailFragment.setId(id);
                recipeDetailFragment.setRecipeName(name);
                recipeDetailFragment.setIngredientShown(true);

                fragmentManager.beginTransaction()
                        .add(R.id.video_container,recipeDetailFragment)
                        .commit();
            }

        }


    }


    @Override
    public void onListItemClick(RecipeStepObject recipeStepObject) {

        Log.d("RecipeActivity","ListItemClicked");

        if (isPhone){
            Intent intent1 = new Intent(getApplicationContext(),RecipeDetailActivity.class);
            intent1.putExtra("recipeId",recipeStepObject.getRecipeId());
            intent1.putExtra("recipeName",name);
            intent1.putExtra("Id",recipeStepObject.getId());
            intent1.putExtra("Description",recipeStepObject.getDescription());
            intent1.putExtra("VideoURL",recipeStepObject.getVideoURL());
            intent1.putExtra("IngredientShown",false);
            startActivity(intent1);
        }else {

            FragmentManager fragmentManager = getSupportFragmentManager();

            RecipeDetailFragment recipeVideo = new RecipeDetailFragment();
            recipeVideo.setId(recipeStepObject.getId());
            recipeVideo.setRecipeName(name);
            recipeVideo.setVideoURL(recipeStepObject.getVideoURL());
            recipeVideo.setDescription(recipeStepObject.getDescription());
            recipeVideo.setIngredientShown(false);

            fragmentManager.beginTransaction()
                    .replace(R.id.video_container,recipeVideo)
                    .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Id",id);
        outState.putString("recipeName",name);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        if (isPhone){
            Intent intent1 = new Intent(getApplicationContext(),RecipeDetailActivity.class);
            intent1.putExtra("Id",id);
            intent1.putExtra("recipeName",name);
            intent1.putExtra("IngredientShown",true);
            startActivity(intent1);
        }else {
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
            recipeDetailFragment.setId(id);
            recipeDetailFragment.setRecipeName(name);
            recipeDetailFragment.setIngredientShown(true);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.video_container,recipeDetailFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    public void onNextClick(String id) {

    }

    @Override
    public void onPrevClick(String id) {

    }
}
