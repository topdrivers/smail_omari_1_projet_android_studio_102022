package com.example.go4lunch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.go4lunch.utils.RecyclerViewItemCountAssertion.withItemCount;
import static com.example.go4lunch.utils.TestUtils.withRecyclerView;
import static org.hamcrest.core.IsNull.notNullValue;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.go4lunch.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import butterknife.OnClick;

@RunWith(AndroidJUnit4.class)
public class ListViewTest {

    // FOR DATA
    private Context context;
    private UiDevice mUiDevice;
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        ActivityScenario<MainActivity> mActivity = mActivityRule.getScenario();
        assertThat(mActivity, notNullValue());
    }

    @Before
    public void before() throws Exception {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }



    /**
     * We ensure that our recyclerview is displaying at least on item
     */
    @Test
    public void myListView_shouldNotBeEmpty() throws UiObjectNotFoundException {

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Click email and password button option
        onView(withId(com.firebase.ui.auth.R.id.email_button))
                .perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        UiObject mText = mUiDevice.findObject(new UiSelector().text("AUCUN DES COMPTES CI-DESSUS"));
        mText.click();




        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //System.out.println("------------get id-----------"+getApplicationContext().);


        onView(withId(com.firebase.ui.auth.R.id.email))
                .perform(click());



        onView(withId(com.firebase.ui.auth.R.id.email))
                //.setText("smail.omari@laposte.net");
                .perform(replaceText("smail.omari@laposte.net"));

        onView(withId(com.firebase.ui.auth.R.id.button_next))
                .perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(com.firebase.ui.auth.R.id.password))
                .perform(click());

        onView(withId(com.firebase.ui.auth.R.id.password))
                .perform(replaceText("123456"));



        onView(withId(com.firebase.ui.auth.R.id.button_done))
                .perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(ViewMatchers.withId(R.id.bottom_list_view_button)).perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // First scroll to the position that needs to be matched
        onView(ViewMatchers.withId(R.id.recyclerview_list_view))
                .check(matches(hasMinimumChildCount(1)));
    }

    /**
     * CHECK ITEMS INFORMATION IN THE RECYCLER VIEW
     */
    @Test
    public void check_several_items_informations_inrecycler_view_list() {

        // This is fixed
        int ITEMS_COUNT = 6;

        onView(ViewMatchers.withId(R.id.recyclerview_list_view))
                .check(withItemCount(ITEMS_COUNT));



        onView(withRecyclerView(R.id.recyclerview_list_view).atPositionOnView(0, R.id.item_name))
                .check(matches(withText("L'Écrin des Saveurs")));

        onView(withRecyclerView(R.id.recyclerview_list_view).atPositionOnView(1, R.id.item_address))
                .check(matches(withText("137 Route du Polygone, Strasbourg")));

        onView(withRecyclerView(R.id.recyclerview_list_view).atPositionOnView(2, R.id.item_name))
                .check(matches(withText("Open until 2.30 pm")));


    }





}
