package com.example.go4lunch;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.view.Gravity;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.DrawerActions;
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

@RunWith(AndroidJUnit4.class)
public class LogouTest {




        // FOR DATA
        private Context context;
        private UiDevice mUiDevice;

        @Rule
        public ActivityScenarioRule<MainActivity> mActivityRule =
                new ActivityScenarioRule<>(MainActivity.class);

        @Before
        public void setup() {this.context =  getApplicationContext().getApplicationContext();}

        @Before
        public void before() throws Exception {
            mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        }

        /**
         * We ensure that our info item  is correct
         */
        @Test
        public void check_login_with_email_and_passsword() throws UiObjectNotFoundException {

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
/*
            mText = mUiDevice.findObject(new UiSelector().text("Lorsque vous utilisez l'application"));
            mText.click();

 */


            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(withId(R.id.activity_main_drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                    .perform(DrawerActions.open());

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(withId(R.id.activity_main_logout))
                    .perform(click());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(withId(com.firebase.ui.auth.R.id.email_button))
                    .check(matches(isDisplayed()));


        }


    }
