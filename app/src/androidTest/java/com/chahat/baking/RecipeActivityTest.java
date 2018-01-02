package com.chahat.baking;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;

import com.chahat.baking.ui.RecipeListFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by chahat on 28/7/17.
 */
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    private IdlingResource mIdlingResource;

    @Rule
    public final ActivityTestRule<RecipeActivity> activityActivityTestRule = new ActivityTestRule<RecipeActivity>(RecipeActivity.class);

    @Before
    public void registerIdlingResource() {

        setUpFragment();

    }

    @Test
    public void checkRecipeIngredientCardClick(){

        onView(withId(R.id.textViewStep)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewStep)).check(matches(withText("Recipe Steps:")));

        onView(withId(R.id.ingredient_text)).check(matches(isDisplayed()));
        onView(withId(R.id.ingredient_text)).check(matches(withText("Recipe Ingredients")));

        onView(withId(R.id.recipe_list_recycler_view)).check(matches(isDisplayed()));
    }

    public void setUpFragment(){
        RecipeListFragment recipeListFragment = new RecipeListFragment();
        recipeListFragment.setId("1");

        FragmentManager fragmentManager = activityActivityTestRule.getActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.recipeContainer,recipeListFragment)
                .commit();

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.waitForIdleSync();

        mIdlingResource = recipeListFragment.getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource!=null){
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
