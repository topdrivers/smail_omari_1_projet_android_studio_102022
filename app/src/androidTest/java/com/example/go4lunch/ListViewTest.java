package com.example.go4lunch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.go4lunch.utils.RecyclerViewItemCountAssertion.withItemCount;
import static com.example.go4lunch.utils.TestUtils.withRecyclerView;
import static org.hamcrest.core.IsNull.notNullValue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ListViewTest {
    @Rule
    public ActivityScenarioRule<ListMeetingActivity> mActivityRule =
            new ActivityScenarioRule<>(ListMeetingActivity.class);

    @Before
    public void setUp() {
        ActivityScenario<ListMeetingActivity> mActivity = mActivityRule.getScenario();
        assertThat(mActivity, notNullValue());
    }

    /**
     * We ensure that our recyclerview is displaying at least on item
     */
    @Test
    public void myMeetingList_shouldNotBeEmpty() {
        // First scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(R.id.recyclerview_list_view))
                .check(matches(hasMinimumChildCount(1)));
    }

    /**
     * When we delete an item, the item is no more shown
     */
    @Test
    public void check_several_items_informations_inrecycler_view_list() {
        // Given : We remove the element at position 2
        // This is fixed
        int ITEMS_COUNT = 6;

        onView(ViewMatchers.withId(R.id.recyclerview_list_view))
                .check(withItemCount(ITEMS_COUNT));



        onView(withRecyclerView(R.id.recyclerview_list_view).atPositionOnView(0, R.id.fragment_list_view_restaurant_name))
                .check(matches(withText("L'Écrin des Saveurs")));

        onView(withRecyclerView(R.id.recyclerview_list_view).atPositionOnView(1, R.id.fragment_list_view_restaurant_address))
                .check(matches(withText("137 Route du Polygone, Strasbourg")));

        onView(withRecyclerView(R.id.recyclerview_list_view).atPositionOnView(2, R.id.fragment_list_view_restaurant_open_until))
                .check(matches(withText("Open until 2.30 pm")));


    }

}
