package com.example.go4lunch.fragments;

import static com.example.go4lunch.fragments.MapsFragment.mLocation;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.Utils.ItemClickSupport;
import com.example.go4lunch.activities.DetailsRestaurantActivity;

import com.example.go4lunch.dataSource.models.RestaurantPlace;

import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewModel.RetrofitViewModel;
import com.example.go4lunch.views.AutoCompleteListViewAdapter;
import com.example.go4lunch.views.MyListViewRestaurantAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class ListViewFragment extends Fragment {

    private MyListViewRestaurantAdapter myListViewRestaurantAdapter;
    private AutoCompleteListViewAdapter autoCompleteListViewAdapter;
    public static RetrofitViewModel retrofitViewModel;
    public  List<Result> results;
    public List<com.example.go4lunch.dataSource.models.PlaceDetails.Result> resultPlaceDetails;
    private Disposable disposable;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;


    public ListViewFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(myListViewRestaurantAdapter!=null){
           initList();

        }

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

    private <T> DisposableObserver<T> createObserver(){
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T t) {
                if (t instanceof ArrayList){
                    update((ArrayList) t);
                }
            }
            @Override
            public void onError(Throwable e) {
                getActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
            }
            @Override
            public void onComplete() { }
        };
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }

    // -------------------
    // UPDATE UI
    // -------------------

    private void update(ArrayList resultList){
        swipeRefreshLayout.setRefreshing(false);
        resultPlaceDetails = new ArrayList<>();
        if (resultList.size() > 0){
            resultPlaceDetails.addAll(resultList);
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.no_restaurant_error_message), Toast.LENGTH_SHORT).show();
        }
        autoCompleteListViewAdapter = new AutoCompleteListViewAdapter(resultPlaceDetails);
        recyclerView.setAdapter(autoCompleteListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        autoCompleteListViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
        configureViewModel();
        initList();
        configureOnClickRecyclerView();
    }

    public void initList()   {
        retrofitViewModel.getResults(mLocation.getLatitude(), mLocation.getLongitude()).observe(this,this::getListRestaurant);

    }

    public void getListRestaurant(RestaurantPlace restaurantPlace) {
        resultPlaceDetails = new ArrayList<>();
        resultPlaceDetails.clear();
        autoCompleteListViewAdapter = new AutoCompleteListViewAdapter(resultPlaceDetails);
        autoCompleteListViewAdapter.notifyDataSetChanged();
        results.clear();
        results.addAll(restaurantPlace.getResults());
        recyclerView.setAdapter(myListViewRestaurantAdapter);
        myListViewRestaurantAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void configureViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        retrofitViewModel = new ViewModelProvider(this, viewModelFactory).get(RetrofitViewModel.class);
        retrofitViewModel.init(mLocation.getLatitude(), mLocation.getLongitude());
        retrofitViewModel.getResults(mLocation.getLatitude(), mLocation.getLongitude());
    }

    //Action click item recycler view
    public void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_list_view)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Result restaurant = myListViewRestaurantAdapter.getRestaurant(position);

                    Intent myIntent = new Intent(getActivity(), DetailsRestaurantActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("restaurantSelected", restaurant);
                    myIntent.putExtras(bundle);



                    ListViewFragment.this.startActivity(myIntent);
                    Toast.makeText(getContext(), "You clicked on user : " + restaurant.getName(), Toast.LENGTH_SHORT).show();


                });
    }

    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        setHasOptionsMenu(true);
        configureRecyclerView(view);
        configureSwipeRefreshLayout();

        return view;
    }

    public void configureRecyclerView(View view) {
        this.resultPlaceDetails = new ArrayList<>();
        this.results = new ArrayList<>();
        Context context = view.getContext();

        recyclerView = view.findViewById(R.id.recyclerview_list_view);
        swipeRefreshLayout = view.findViewById(R.id.fragment_list_view_swipe_container);

        myListViewRestaurantAdapter = new MyListViewRestaurantAdapter(results);
        recyclerView.setAdapter(myListViewRestaurantAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
}