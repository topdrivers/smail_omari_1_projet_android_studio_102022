package com.example.go4lunch.fragments;

import static com.example.go4lunch.activities.MainActivity.userViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.go4lunch.activities.DetailsRestaurantActivity;
import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.viewModel.RetrofitViewModel;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.OptionalDouble;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MapsFragment extends Fragment  implements OnMapReadyCallback {
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 15;
    private float changingZoom = 15;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    //FOR DATA
    private Disposable disposable;
    private List<User> userList;
    private List<Result> resultClickMarker;
    FirebaseAuth firebaseAuth;
    public  RetrofitViewModel retrofitViewModel;
    List<Result> results;
    Double latValue;
    Double lngValue;
    Double latValueSum;
    Double lngValueSum;
    OptionalDouble latValueMin;
    OptionalDouble lngValueMin;
    OptionalDouble latValueMax;
    OptionalDouble lngValueMax;
    LatLngBounds.Builder builder ;
    LatLngBounds mLatLngBounds ;
    ArrayList<Marker> markers = new ArrayList<>();
    int j =0;

    public static MapFragment newInstance() {
        return (new MapFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userViewModel!=null){
            initUserList();
        }
        firebaseAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        configureViewModel();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

        MenuItem item = menu.findItem(R.id.option_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);


        SearchView searchView = (SearchView) item.getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchView.setQueryHint(getString(R.string.list_fragment_search_restaurant));
        searchView.setBackgroundColor(Color.WHITE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    disposable = retrofitViewModel.getAutoCompletePlaceResults("48.550720,7.763412", query).subscribeWith(createObserver());

                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.search_too_short), Toast.LENGTH_LONG).show();
                }
                return true;

            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() > 2) {
                    disposable = retrofitViewModel.getAutoCompletePlaceResults("48.550720,7.763412", query).subscribeWith(createObserver());
                }
                return false;
            }

        });

    }

    public void initList()   {
        retrofitViewModel.getResults().observe(this,this::getListRestaurant);

    }

    private void getListRestaurant(RestaurantPlace restaurantPlace) {
        results = restaurantPlace.getResults();
    }


    private <T> DisposableObserver<T> createObserver(){
        return new DisposableObserver<T>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(T t) {
                if (t instanceof ArrayList){
                    update((ArrayList) t);
                }
            }
            @Override
            public void onError(Throwable e) {
               // getActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
            }
            @Override
            public void onComplete() { }
        };
    }

    // -------------------
    // UPDATE UI
    // -------------------

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void update(ArrayList resultList) {
        System.out.println("------------resultlist size-----------"+resultList.size());
        System.out.println("------------results size-----------"+results.size());
        List<com.example.go4lunch.dataSource.models.PlaceDetails.Result> resultPlaceDetails = new ArrayList<>();
        resultPlaceDetails.addAll(resultList);
        j=0;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (com.example.go4lunch.dataSource.models.PlaceDetails.Result result : resultPlaceDetails) {
               // if (resultPlaceDetails.get(j).getPlaceId().equals(result.getPlaceId())) {
                    Double lat = result.getGeometry().getLocation().getLat();
                    Double lng = result.getGeometry().getLocation().getLng();
                builder.include(new LatLng(lat,lng));

                System.out.println("------------markers---------"+markers.size());
                    j=j+1;
                //}

            }

        LatLngBounds bounds;
            try {
                bounds = builder.build();
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }catch (IllegalStateException e){
                System.err.println(e.getMessage());
            }
            

        }




    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(requireActivity());
        retrofitViewModel = new ViewModelProvider(this, mViewModelFactory).get(RetrofitViewModel.class);
        retrofitViewModel.init();
        retrofitViewModel.getResults();
        retrofitViewModel.getResults().observe(requireActivity(),this::getResultList);
    }

    private void getResultList(RestaurantPlace restaurantPlace) {
        resultClickMarker = restaurantPlace.getResults();
        mMap.clear();
        for(Result result : restaurantPlace.getResults()){

                Double lat = result.getGeometry().getLocation().getLat();
                Double lng = result.getGeometry().getLocation().getLng();
                String title = result.getName();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lng));
                markerOptions.title(title);
                if(userViewModel.isCurrentUserLogged()) {
                    for (User user : userList) {
                        if (result.getPlaceId().equalsIgnoreCase(user.getRestaurantChoice())) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_place_booked));
                            Marker marker = mMap.addMarker(markerOptions);
                            marker.setTag(result.getPlaceId());
                            break;
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_place_unbook));
                            Marker marker = mMap.addMarker(markerOptions);
                            marker.setTag(result.getPlaceId());
                        }
                    }
                }
            configureClickIconMap();
        }

    }

    private void configureClickIconMap(){
        mMap.setOnMarkerClickListener(MapsFragment.this::onClickMarker);
    }


    private boolean onClickMarker(Marker marker) {

        for(Result result1 : resultClickMarker ) {

            if (marker.getTag().equals(result1.getPlaceId())) {
                Intent intent = new Intent(getActivity(), DetailsRestaurantActivity.class);
                intent.putExtra("restaurantSelected",result1);
                startActivity(intent);

            } else {

            }

        }
        return true;

    }


    private void getCurrentLocation() {
        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                mLocation = task.getResult();
                                if (mLocation != null) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), DEFAULT_ZOOM));
                                } else {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                    mMap.getUiSettings().setMyLocationButtonEnabled(false);

                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();

        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mLocation = task.getResult();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                View locationButton = ((View) getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlp.setMargins(0, 0, 30, 30);
            }

        });




    }
    private void disposeWhenDestroy () {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }


    private void initUserList()   {
        userViewModel.getDataBaseInstanceUser();
        userViewModel.getAllUsersFromDataBase();
        userViewModel.getAllUsers().observe(this, this::userList);
    }

    private void userList(List<User> users) {
        this.userList = users;
    }

}


