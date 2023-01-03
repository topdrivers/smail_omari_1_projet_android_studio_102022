package com.example.go4lunch;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;
import static androidx.test.espresso.matcher.ViewMatchers.isFocused;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.go4lunch.activities.MainActivity;
import com.example.go4lunch.databinding.ActivityMainBinding;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

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

        //onView(withText("Continuer avec")).check(matches(isDisplayed()));
        //onView(withId(android.R.id.button1)).perform(click());
        /*
        Looper.prepare();

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        final AlertDialog dialog = adb.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();//or

         */

       // onView(withText("AUCUN DES COMPTES CI-DESSUS"))
/*
        onView(withText("AUCUN DES COMPTES CI-DESSUS"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

 */





/*

        onView(withText("AUCUN DES COMPTES CI-DESSUS"))
                .perform(click());

        onView(isRoot()).perform(ViewActions.pressBack());

 */

/*
        onView(withId(androidx.test.espresso.contrib.R.id.home))
                .perform(click());

 */


/*
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.email),
                        withParent(withId(R.id.input_layout_email)),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("a@a.aa"));

 */

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
/*
        mText = mUiDevice.findObject(new UiSelector().text("Lorsque vous utilisez l'application"));
        mText.click();

 */







        //check field name equals Réunion 1
        /*
        onView(withId(R.id.bottom_list_view_button))
                .check(matches(withTitle(context.getString((R.string.textview_name_for_test)))));

         */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.map))
                .check(matches(isDisplayed()));

    }

}
