package com.chahat.baking;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import com.chahat.baking.ui.RecipeDetailFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by chahat on 30/7/17.
 */
@RunWith(AndroidJUnit4.class)
public class RecipeDetailActivityTest {

    private IdlingResource mIdlingResource;

    @Rule
    public final ActivityTestRule<RecipeDetailActivity> activityActivityTestRule = new ActivityTestRule<>(RecipeDetailActivity.class);

    @Before
    public void registerIdlingResource() {

        setUpFragment();
    }


    @Test
    public void matchToolbarTitle() {
        CharSequence toolbarTitle = "Recipe Detail";
        onView(isAssignableFrom(Toolbar.class)).check(matches(toolbarTitle(is(toolbarTitle))));

        int orientation = InstrumentationRegistry.getTargetContext().getResources().getConfiguration().orientation;

        Activity recipeDetailActivity = activityActivityTestRule.getActivity();
        recipeDetailActivity.setRequestedOrientation((orientation == Configuration.ORIENTATION_PORTRAIT) ?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        onView(isAssignableFrom(Toolbar.class)).check(matches(toolbarTitle(is(toolbarTitle))));
    }

    @Test
    public void RecipeDetailIngredientTest(){
        onView(withId(R.id.ingredient_recycler_view)).check(matches(isDisplayed()));
    }

    private static Matcher<Object> toolbarTitle(final Matcher<CharSequence> matchText) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return matchText.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
            }
        };
    }

    public void setUpFragment(){
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setId("1");
        fragment.setRecipeName("Nutella Pie");
        fragment.setIngredientShown(true);
        fragment.setVideoURL("");
        fragment.setDescription("");


        FragmentManager fragmentManager = activityActivityTestRule.getActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.video_container,fragment)
                .commit();

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.waitForIdleSync();

        mIdlingResource = fragment.getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource!=null){
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }


}
