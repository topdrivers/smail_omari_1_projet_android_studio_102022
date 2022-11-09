package com.example.go4lunch.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.DataSource.Models.RestaurantPlace;
import com.example.go4lunch.DataSource.Models.Result;
import com.example.go4lunch.R;
import com.example.go4lunch.DataSource.RemoteData.RetrofitStreams;
import com.example.go4lunch.ViewModel.RetrofitViewModel;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MapsFragment extends Fragment  implements OnMapReadyCallback {
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private RetrofitViewModel retrofitViewModel;
    //private final RetrofitRepository mRetrofitRepository = new RetrofitRepository(APIClient.getGoogleMapAPI());
    int radius = 1000;
    private static final float DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    //FOR DATA
    private Disposable disposable;

    public static MapFragment newInstance() {
        return (new MapFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        configureViewModel();
        //FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_location);
        //floatingActionButton.setOnClickListener(v -> getCurrentLocation());
    }

    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(requireActivity());
        retrofitViewModel = new ViewModelProvider(this, mViewModelFactory).get(RetrofitViewModel.class);
        retrofitViewModel.init();
        retrofitViewModel.getResults();
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
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.



        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mLocation = task.getResult();

                // -------------------
                // HTTP (RxJAVA)
                // -------------------


                this.disposable = RetrofitStreams.getPlaceResultsLiveData(mLocation.getLongitude()+","+mLocation.getLatitude()).subscribeWith(new DisposableObserver<RestaurantPlace>() {
                    @Override
                    public void onNext(RestaurantPlace restaurantPlace) {
                        System.out.println("----------------------onnext-----------------");
                        System.out.println("----------------------restaurant status-----------------"+restaurantPlace.getStatus());
                        System.out.println("----------------------restaurant next-----------------"+restaurantPlace.getNextPageToken());
                        updateUI(restaurantPlace);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("----------------------onerror-----------------");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("----------------------oncomplete-----------------");
                    }
                });


            }

        });




    }
    private void disposeWhenDestroy () {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // -------------------
    // UPDATE UI
    // -------------------

    private void updateUI(RestaurantPlace restaurantPlace) {
        System.out.println("----------------updateui-------------------------");
        try {
            mMap.clear();
            System.out.println("-------------------size results-----------"+restaurantPlace.getResults().size());
            for (Result r : restaurantPlace.getResults()) {
                System.out.println("------------------for-------------------------");
                double lat = r.getGeometry().getLocation().getLat();
                double lng = r.getGeometry().getLocation().getLng();
                String name = r.getName();
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(name);
                mMap.addMarker(markerOptions);
            }
        } catch (Exception e) {
            Log.d("onResponse", "There is an error");
            e.printStackTrace();
        }
         /* results.clear();
            githubUsers.addAll(users);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

            */
    }
}
/*
        private void disposeWhenDestroy () {
            if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
        }

        // -------------------
        // UPDATE UI
        // -------------------

        private void updateUI (PlaceResults users){
            // placeResults.clear();
            //placeResults.addAll(users);
            // adapter.notifyDataSetChanged();
            //swipeRefreshLayout.setRefreshing(false);
        }

 */


