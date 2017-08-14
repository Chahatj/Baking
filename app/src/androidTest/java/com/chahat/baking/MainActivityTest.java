package com.chahat.baking;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.chahat.baking.ui.BakingListFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.contrib.RecyclerViewActions;

/**
 * Created by chahat on 30/7/17.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private IdlingResource mIdlingResource;

    @Rule
    public final ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void registerIdlingResource() {

        setUpFragment();

    }

    public void setUpFragment(){

        FragmentManager fragmentManager = activityActivityTestRule.getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.baking_list_fragment);

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.waitForIdleSync();

        mIdlingResource = ((BakingListFragment) fragment).getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void check(){
        onView(withId(R.id.baking_list_recycler_view)).check(matches(isDisplayed()));

        onView(withId(R.id.baking_list_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.ingredient_text)).check(matches(isDisplayed()));
        onView(withId(R.id.ingredient_text)).check(matches(withText("Recipe Ingredients")));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource!=null){
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
