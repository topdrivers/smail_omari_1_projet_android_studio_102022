package com.example.go4lunch.activities;

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
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.fragments.ListViewFragment;
import com.example.go4lunch.fragments.MapsFragment;
import com.example.go4lunch.R;
import com.example.go4lunch.fragments.WorkmatesFragment;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.UserViewModelFactory;
import com.example.go4lunch.model.User;
import com.example.go4lunch.viewModel.UserViewModel;
import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Arrays;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks, BottomNavigationView.OnNavigationItemSelectedListener {
    private static final int RC_SIGN_IN = 123;
    //FOR DESIGN
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int DELAY_SHORT = 1000;
    private ActivityMainBinding binding;
    private static final int REQUEST_PERMISSIONS_LOCATION = 567;

    private MapsFragment mapsFragment = new MapsFragment();
    private ListViewFragment listViewFragment= new ListViewFragment();
    private WorkmatesFragment workmatesFragment = new WorkmatesFragment();
    //FOR DATA
    private DisposableObserver<RestaurantPlace> disposable;
    final MutableLiveData<Boolean> hasPermissions = new MutableLiveData<>();
    private ActionBarDrawerToggle toggle;
    private User currentUser;
    public static UserViewModel userViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //configureMapsFragment();
        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomNavigationView();
        configureUserViewModel();

        //handleClickNavDrawer();
        setupNavDrawer();
//        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext());
        //AppEventsLogger.activateApp(getApplication());

        new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentid = v.findFocus().getId();


                System.out.println("--------------------id_____________________"+currentid);
//         RelativeLayout showme = (RelativeLayout)findViewById(R.id.startLayout);
//         showme.setVisibility(View.VISIBLE);
            }
        };


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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_main_menu_drawer, menu);
        return true;
    }

    /**
     * Navigation drawer setup
     */
    private void setupNavDrawer() {
        /*
        toggle = new ActionBarDrawerToggle(this, binding.activityMainDrawerLayout, R.string.Open_drawer_menu, R.string.Close_drawer_menu);
        binding.activityMainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

         */
        //handleClickNavDrawer();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }



    private void configureMapsFragment() {
        mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_layout);
        if(mapsFragment==null){
            mapsFragment = new MapsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout,mapsFragment)
                    .commit();
        }
    }

    private void configureListViewFragment() {
        ListViewFragment listViewFragment = (ListViewFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_layout);
        if(listViewFragment==null){
            listViewFragment = new ListViewFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout,listViewFragment)
                    .commit();
        }
    }
    private void configureWorkmatesFragment() {
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
                System.out.println("----------lunch button---------------");
                break;
            case R.id.activity_main_settings:
                break;

            case R.id.activity_main_logout:
                System.out.println("------------------------------");
                userViewModel.signOut(this);
                /*
                AuthUI.getInstance().signOut(this)
                        .addOnSuccessListener( aVoid ->{
                           finish();
                        });

                 */
                handler.postDelayed(this::checkIfUserIsSignedIn, 1000);
                break;

            case R.id.bottom_map_button:
                System.out.println("----------map button---------------");
                //configureMapsFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_frame_layout,mapsFragment)
                        .commit();
                break;
            case R.id.bottom_list_view_button:
                System.out.println("----------list button---------------");
                //configureListViewFragment();
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

    public void checkIfUserIsSignedIn() {
        if ( !userViewModel.isCurrentUserLogged()) {
            createSignInIntent();
        } else {
            observeUsers();
            System.out.println("---------------------check user--------------");
        }
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_naviation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // 3 - Configure NavigationView
    private void configureBottomNavigationView(){
        this.bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation_view);
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



    private void observeUsers() {
        userViewModel.getDataBaseInstanceUser();
        userViewModel.getCurrentUserDataFromFireStore(userViewModel.getCurrentUser().getUid());
       userViewModel.observeCurrentUser().observe(this, this::setupNavigationHeader);
    }

    private void setupNavigationHeader(User user) {
        currentUser = user;
        System.out.println("-------------current user--------"+currentUser);

        NavigationView navigationView = findViewById(R.id.activity_main_naviation_view);
        View headerView = navigationView.getHeaderView(0);
        //ImageView background = headerView.findViewById(R.id.iVBackgroundHeaderMenu);
        ImageView userAvatar = headerView.findViewById(R.id.activity_main_header_profile_image);
        TextView userName = headerView.findViewById(R.id.activity_main_nav_header_name_text_view);
        TextView userEmail = headerView.findViewById(R.id.activity_main_nav_header_email_text_view);

        if ( FirebaseAuth.getInstance().getCurrentUser()!=null) {
            if (currentUser.getUrlPicture() != null) {
                Glide.with(this)
                        .load(currentUser.getUrlPicture())
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

        //saveInSharePreferences();
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

        if (hasPermissions.getValue() ) {
            /*
            if(retrofitViewModel!=null) {
                System.out.println("----------viewmodel-------------" + retrofitViewModel);
                retrofitViewModel.getResults();
                System.out.println("--------------retrofit results--------" + retrofitViewModel.getResults());
            }
            System.out.println("-------------haspermission---------------");

             */
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