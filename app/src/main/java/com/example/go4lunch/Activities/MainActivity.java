package com.example.go4lunch.Activities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;

import com.example.go4lunch.Fragments.MapsFragment;
import com.example.go4lunch.Models.RestaurantPlace;
import com.example.go4lunch.R;
import com.example.go4lunch.Utils.GithubStreams;
import com.example.go4lunch.ViewModel.ViewModel;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {
    private static final int RC_SIGN_IN = 123;
    //FOR DESIGN
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int DELAY_SHORT = 1000;
    private ActivityMainBinding binding;
    private static final int REQUEST_PERMISSIONS_LOCATION = 567;
    private ViewModel viewModel;
    //FOR DATA
    private DisposableObserver<RestaurantPlace> disposable;
    final MutableLiveData<Boolean> hasPermissions = new MutableLiveData<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //viewModel = new ViewModelProvider(this).get(ViewModel.class);

        configureMapsFragment();
        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();
        handleClickNavDrawer();
    }




    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        permission();
        setupAppAccordingToPermissions();
    }

    @SuppressLint("NonConstantResourceId")
    private void handleClickNavDrawer() {
        binding.activityMainNaviationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.activity_main_your_lunch:

                    break;
                case R.id.activity_main_settings:

                    break;
                case R.id.activity_main_logout:
                    System.out.println("------------------------------");
                    AuthUI.getInstance().signOut(getApplicationContext());
                    break;
            }
            return true;
        });
    }



    private void configureMapsFragment() {
        MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_layout);
        if(mapsFragment==null){
            mapsFragment = new MapsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main_frame_layout,mapsFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(this::checkIfUserIsSignedIn, DELAY_SHORT);

    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_your_lunch:
                break;
            case R.id.activity_main_settings:
                break;
            case R.id.activity_main_logout:
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

    // 1 - Configure Toolbar
    private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void checkIfUserIsSignedIn() {
        if ( FirebaseAuth.getInstance().getCurrentUser()==null) {
            createSignInIntent();
        } else {
            //observeUsers();
        }
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_naviation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Contracts to start activities
     */
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private final ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                       /* Intent returnedIntent = result.getData();
                        if (returnedIntent != null) {
                            String selectedRestaurantID = returnedIntent.getStringExtra(KEY_SELECTED_RESTAURANT_ID);
                            String selectedRestaurantName = returnedIntent.getStringExtra(KEY_SELECTED_RESTAURANT_NAME);
                            viewModel.setIdRestaurantToFocusOn(selectedRestaurantID);
                            showSnackBarMessage(String.format(getString(R.string.message_activity_result_ok), selectedRestaurantName));
                        }
                    } else {
                        showSnackBarMessage(getString(R.string.message_activity_result_not_ok));

                        */
                    }
                }
            }
    );


    /**
     * FirebaseUI related methods
     */
    public void createSignInIntent() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                // Unstable twitter login, follow matter here: https://github.com/firebase/firebase-js-sdk/issues/4256
                //new AuthUI.IdpConfig.TwitterBuilder().build(),
                //new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setLockOrientation(true)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {

        } else {
            /*
            if (response == null) {
                showSnackBarMessage(getString(R.string.error_authentication_canceled));
            } else if (response.getError() != null) {
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBarMessage(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBarMessage(getString(R.string.error_unknown_error));
                }
            }

             */
            handler.postDelayed(this::checkIfUserIsSignedIn, 1000);


        }
    }

    // Show Snack Bar with a message
    private void showSnackBar( String message){
        Snackbar.make(binding.activityMainDrawerLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("------------permission result----------------");
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

        if (hasPermissions.getValue()) {

            System.out.println("-------------1fois------------------");
            // 1.2 - Execute the stream subscribing to Observable defined inside GithubStream
            this.disposable = GithubStreams.getPlaceResultsLiveData("48.550720,7.763412").subscribeWith(new DisposableObserver<RestaurantPlace>() {
                @Override
                public void onNext(RestaurantPlace restaurantPlace) {
                    Log.e("TAG","On Next");
                    // 1.3 - Update UI with list of users
                    System.out.println("--------------esult"+restaurantPlace.getResults().get(1).getName());
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("TAG","On Error"+Log.getStackTraceString(e));
                }

                @Override
                public void onComplete() {
                    Log.e("TAG","On Complete !!");
                }
            });
            //observeLocation();
        } else {

            requestPermission();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        observeLocation();
        Location location = null;
        location.setLongitude(48.550720);
        location.setLatitude(7.763412);
        System.out.println("--------------------------------");

        // 1.2 - Execute the stream subscribing to Observable defined inside GithubStream
        this.disposable = GithubStreams.getPlaceResultsLiveData(location.toString()).subscribeWith(new DisposableObserver<RestaurantPlace>() {
            @Override
            public void onNext(RestaurantPlace restaurantPlace) {
                Log.e("TAG","On Next");
                // 1.3 - Update UI with list of users
                System.out.println("--------------esult"+restaurantPlace);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Log.e("TAG","On Complete !!");
            }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }






    private void observeLocation() {
        //viewModel.startTrackingLocation(this, this);
        //viewModel.getCurrentLocation().observe(this, this::updateLocationAndFetchRestaurantList);

       //mLocation.setLongitude(48.550720);
       //mLocation.setLatitude(7.763412);

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
        System.out.println("-------------denied-----------------");
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestPermission();
        }

    }
}