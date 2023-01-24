package com.example.go4lunch.fragments;

import static com.example.go4lunch.activities.MainActivity.userViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class MapsFragment extends Fragment  implements OnMapReadyCallback {
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    //FOR DATA
    private Disposable disposable;
    private List<User> userList;
    private List<Result> resultClickMarker;
    FirebaseAuth firebaseAuth;

    public static MapFragment newInstance() {
        return (new MapFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(requireActivity());
        RetrofitViewModel retrofitViewModel = new ViewModelProvider(this, mViewModelFactory).get(RetrofitViewModel.class);
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


