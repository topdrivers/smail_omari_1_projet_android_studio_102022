package com.example.go4lunch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import android.view.View;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.go4lunch.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class LoginLogoutTest {
        /*

        @Rule
        public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

        @Test
        public void addAndRemoveTask() {


            MainActivity activity = rule.getActivity();
            TextView lblNoTask = activity.findViewById(R.id.lbl_no_task);
            RecyclerView listTasks = activity.findViewById(R.id.list_tasks);

            onView(withId(R.id.fab_add_task)).perform(click());
            onView(withId(R.id.txt_task_name)).perform(replaceText("Tâche example"));
            onView(withId(android.R.id.button1)).perform(click());

            // Check that lblTask is not displayed anymore
            assertThat(lblNoTask.getVisibility(), equalTo(View.GONE));
            // Check that recyclerView is displayed
            assertThat(listTasks.getVisibility(), equalTo(View.VISIBLE));
            // Check that it contains one element only
            assertThat(Objects.requireNonNull(listTasks.getAdapter()).getItemCount(), equalTo(1));

            onView(withId(R.id.img_delete)).perform(click());

            // Check that lblTask is displayed
            assertThat(lblNoTask.getVisibility(), equalTo(View.VISIBLE));
            // Check that recyclerView is not displayed anymore
            assertThat(listTasks.getVisibility(), equalTo(View.GONE));
        }

         */

    }
