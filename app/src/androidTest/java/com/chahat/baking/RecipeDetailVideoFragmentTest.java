package com.chahat.baking;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;

import com.chahat.baking.ui.RecipeDetailFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by chahat on 30/7/17.
 */
@RunWith(AndroidJUnit4.class)
public class RecipeDetailVideoFragmentTest {

    private IdlingResource mIdlingResource;

    @Rule
    public final ActivityTestRule<RecipeDetailActivity> activityActivityTestRule = new ActivityTestRule<>(RecipeDetailActivity.class);

    @Before
    public void registerIdlingResource() {

        setUpFragment();
    }


    @Test
    public void RecipeDetailVideoTest(){
        onView(withId(R.id.playerView)).check(matches(isDisplayed()));
        onView(withId(R.id.description_text_view)).check(matches(isDisplayed()));
        onView(withId(R.id.prev_image_view)).check(matches(isDisplayed()));
        onView(withId(R.id.next_image_view)).check(matches(isDisplayed()));
    }

    public void setUpFragment(){
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setId("1");
        fragment.setRecipeName("Nutella Pie");
        fragment.setIngredientShown(false);
        fragment.setVideoURL("https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4");
        fragment.setDescription("Recipe Introduction");


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
