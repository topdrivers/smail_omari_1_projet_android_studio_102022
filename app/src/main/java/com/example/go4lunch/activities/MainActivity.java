package com.example.go4lunch.activities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


import static com.facebook.FacebookSdk.sdkInitialize;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.fragments.ListViewFragment;
import com.example.go4lunch.fragments.MapsFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.fragments.SettingsFragment;
import com.example.go4lunch.fragments.WorkmatesFragment;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.UserViewModelFactory;
import com.example.go4lunch.model.User;
import com.example.go4lunch.viewModel.UserViewModel;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.Disposable;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks, BottomNavigationView.OnNavigationItemSelectedListener{
    private static final int RC_SIGN_IN = 123 ;
    private static final int REQ_CODE_SPEECH_INPUT = 12 ;
    //FOR DESIGN
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int DELAY_SHORT = 1000;
    private ActivityMainBinding binding;
    private static final int REQUEST_PERMISSIONS_LOCATION = 567;

    private final MapsFragment mapsFragment = new MapsFragment();
    private final ListViewFragment listViewFragment= new ListViewFragment();
    private final WorkmatesFragment workmatesFragment = new WorkmatesFragment();
    private final SettingsFragment settingsFragment = new SettingsFragment();
    final MutableLiveData<Boolean> hasPermissions = new MutableLiveData<>();
    public static UserViewModel userViewModel;
    Disposable disposable;
    private Locale mCurrentLocale;
    private int fromFragment = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomNavigationView();
        configureUserViewModel();
        sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        getSerialisable();
/*
        Button buttonSpeak = findViewById(R.id.option_menu_search);
        buttonSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });



        Button settingsButton = findViewById(R.id.activity_main_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(myIntent);
            }
        });

 */


    }

    private void getSerialisable() {

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();


        if (bundle != null) {
            fromFragment = (int) bundle.getSerializable("fromFragment");
        }
        if(fromFragment == 20){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout,listViewFragment)
                    .commit();
            BottomNavigationView bottomNavigationView;
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation_view);
            bottomNavigationView.setSelectedItemId(R.id.bottom_list_view_button);

        }else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout,mapsFragment)
                    .commit();
        }
        fromFragment=0;

    }


    private void configureUserViewModel() {
        UserViewModelFactory userViewModelFactory = Injection.provideUserViewModelFactory(this);
        userViewModel = new ViewModelProvider(this,userViewModelFactory).get(UserViewModel.class);
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        permission();
        setupAppAccordingToPermissions();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(this::checkIfUserIsSignedIn, DELAY_SHORT);

    }

    @Override
    public void onBackPressed() {
        //  Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //  Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_your_lunch:
                break;
            case R.id.activity_main_settings:

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_frame_layout,settingsFragment)
                        .commit();
                break;

            case R.id.activity_main_logout:
                userViewModel.signOut(this);
                handler.postDelayed(this::checkIfUserIsSignedIn, 1000);
                break;

            case R.id.bottom_map_button:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_frame_layout,mapsFragment)
                        .commit();
                break;
            case R.id.bottom_list_view_button:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_frame_layout,listViewFragment)
                        .commit();
                break;
            case R.id.bottom_workmates_button:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_frame_layout,workmatesFragment)
                        .commit();
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    // Configure Toolbar
    private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.baseline_search_white));
    }

    // Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void checkIfUserIsSignedIn() {
        if ( !userViewModel.isCurrentUserLogged()) {
            createSignInIntent();
        } else {
            observeUsers();
        }
    }

    //  Configure NavigationView
    private void configureNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_naviation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Configure NavigationView
    private void configureBottomNavigationView(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.bottom_map_button);
    }

    /**
     * Contracts to start activities
     */
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    /**
     * FirebaseUI related methods
     */
    public void createSignInIntent() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                // Unstable twitter login, follow matter here: https://github.com/firebase/firebase-js-sdk/issues/4256
                new AuthUI.IdpConfig.TwitterBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setLockOrientation(true)
                .build(), RC_SIGN_IN;
        signInLauncher.launch(signInIntent);



    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(binding.activityMainFrameLayout, message, Snackbar.LENGTH_LONG).show();
    }



    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            showSnackBarMessage(getString(R.string.connection_succeed));
        } else {
            if (response == null) {
                showSnackBarMessage(getString(R.string.error_authentication_canceled));
            } else if (response.getError() != null) {
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBarMessage(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBarMessage(getString(R.string.error_unknown_error));
                }
            }


            handler.postDelayed(this::checkIfUserIsSignedIn, 1000);


        }
    }



    // Show Snack Bar with a message
    private void showSnackBar( String message){
        Snackbar.make(binding.activityMainDrawerLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    ActionMenuItemView input = ((ActionMenuItemView) findViewById(R.id.option_menu_search));
                    input.setText(result.get(0)); // set the input data to the editText alongside if want to.

                }
                break;
            }

        }
    }



    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
                //userManager.createUser();
                showSnackBar(getString(R.string.connection_succeed));
            } else {
                // ERRORS
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError() != null) {
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                }
            }

        }
    }



    private void observeUsers() {
        userViewModel.getDataBaseInstanceUser();
        userViewModel.getCurrentUserDataFromFireStore(userViewModel.getCurrentUser().getUid());
        userViewModel.observeCurrentUser().observe(this, this::setupNavigationHeader);
    }

    private void setupNavigationHeader(User user) {
        NavigationView navigationView = findViewById(R.id.activity_main_naviation_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView userAvatar = headerView.findViewById(R.id.activity_main_header_profile_image);
        TextView userName = headerView.findViewById(R.id.activity_main_nav_header_name_text_view);
        TextView userEmail = headerView.findViewById(R.id.activity_main_nav_header_email_text_view);

        if ( FirebaseAuth.getInstance().getCurrentUser()!=null) {
            if (user.getUrlPicture() != null) {
                Glide.with(this)
                        .load(user.getUrlPicture())
                        .circleCrop()
                        .into(userAvatar);
            } else {
                Glide.with(this)
                        .load(R.drawable.image_profil)
                        .circleCrop()
                        .into(userAvatar);
            }

            userName.setText(user.getUsername());
            userEmail.setText(user.getUserEmail());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private LiveData<Boolean> permission(){
        hasPermissions.setValue(EasyPermissions.hasPermissions(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION));
        return hasPermissions;
    }

    private void setupAppAccordingToPermissions() {

        if (hasPermissions.getValue() ) {

        } else {

            requestPermission();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }

    public void requestPermission() {

        EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_rationale_location),
                REQUEST_PERMISSIONS_LOCATION,
                ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        );
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestPermission();
        }

    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            mapsFragment.searchView.setQuery(query, false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCurrentLocale = getResources().getConfiguration().locale;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Locale locale = getLocale(this);

        if (!locale.equals(mCurrentLocale)) {

            mCurrentLocale = locale;
            recreate();
        }
    }

    public static Locale getLocale(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String lang = sharedPreferences.getString("language", "en");
        switch (lang) {
            case "English":
                lang = "en";
                break;
            case "Spanish":
                lang = "es";
                break;
        }
        return new Locale(lang);
    }

}






