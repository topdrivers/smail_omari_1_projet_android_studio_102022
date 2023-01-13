package com.example.go4lunch.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.go4lunch.activities.MainActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListViewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MyListViewRestaurantAdapter myListViewRestaurantAdapter;
    private AutoCompleteListViewAdapter autoCompleteListViewAdapter;

    public static RetrofitViewModel retrofitViewModel;
    public  List<Result> results;
    public List<com.example.go4lunch.dataSource.models.PlaceDetails.Result> resultPlaceDetails;
    public DisposableObserver<RestaurantPlace> mDisposable;
    private Disposable disposable;

    //@BindView(R.id.recyclerview_list_view) RecyclerView recyclerView;
    //@BindView(R.id.fragment_list_view_swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ViewModelFactory mViewModelFactory;

    public ListViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListViewFragment newInstance(String param1, String param2) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        if(myListViewRestaurantAdapter!=null){
           initList();

        }


    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        /*
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem item = menu.findItem(R.id.option_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
              //      executeHttpRequestWithRetrofit();
                }
                //executeHttpRequestWithRetrofitAutocomplete(newText);
                return true;
            }
        });

 */
            super.onCreateOptionsMenu(menu, inflater);
    menu.clear();

    inflater.inflate(R.menu.options_menu, menu);

    SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

    MenuItem item = menu.findItem(R.id.option_menu_search);
    //SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
    SearchView searchView = (SearchView) item.getActionView();
    //item.setActionView(searchView);
    //searchView.setQueryHint(getResources().getString(R.string.toolbar_search_hint));
    //searchView.setSearchableInfo(searchManager.getSearchableInfo(((MainActivity) getContext()).getComponentName()));


    //searchView.setIconifiedByDefault(false);// Do not iconify the widget; expand it by default
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (query.length() > 2) {
                //disposable = PlacesStreams.streamFetchAutoCompleteInfo(query, mViewModel.getCurrentUserPositionFormatted(), mViewModel.getCurrentUserRadius(), MapFragment.API_KEY).subscribeWith(createObserver());
                System.out.println("--------------query--------------"+query);
                disposable = retrofitViewModel.getAutoCompletePlaceResults("48.550720,7.763412", query).subscribeWith(createObserver());
                //disposable = retrofitViewModel.streamFetchAutoComplete("48.550720,7.763412", query).subscribeWith(createObserver());

            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.search_too_short), Toast.LENGTH_LONG).show();
            }
            return true;

        }

        @Override
        public boolean onQueryTextChange(String query) {
            if (query.length() > 2) {
                //disposable = PlacesStreams.streamFetchAutoCompleteInfo(query, mViewModel.getCurrentUserPositionFormatted(), mViewModel.getCurrentUserRadius(), MapFragment.API_KEY).subscribeWith(createObserver());
                System.out.println("--------------query--------------"+query);
                disposable = retrofitViewModel.getAutoCompletePlaceResults("48.550720,7.763412", query).subscribeWith(createObserver());
                //disposable = retrofitViewModel.streamFetchAutoComplete("48.550720,7.763412", query).subscribeWith(createObserver());
            }
            return false;
        }
    });

    }

    private <T> DisposableObserver<T> createObserver(){
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T t) {
                System.out.println("-----------T-------------"+t.getClass().isArray());
                //update((ArrayList) t);
                if (t instanceof ArrayList){
                    System.out.println("-----------tT-------------"+t);
                    update((ArrayList) t);
                }
            }
            @Override
            public void onError(Throwable e) {
                getActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                //handleError(e)
                 ;}
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
        System.out.println("-------------------on vient dan sla boucke--------");
        swipeRefreshLayout.setRefreshing(false);
        //results.clear();
        resultPlaceDetails = new ArrayList<>();
        //myListViewRestaurantAdapter.notifyDataSetChanged();
        if (resultList.size() > 0){
            resultPlaceDetails.addAll(resultList);
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.no_restaurant_error_message), Toast.LENGTH_SHORT).show();
        }
        System.out.println("-----------------resltplacedetails-------"+resultPlaceDetails.size());
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
        retrofitViewModel.getResults().observe(this,this::getListRestaurant);

    }
/*
    public void autoCompleteRestaurantList(String newText){
        //retrofitViewModel.getAutoCompletePlaceResults("48.550720,7.763412", newText).observe(this, this::updateUi);
        this.mDisposable = retrofitViewModel.getAutoCompletePlaceResults("48.550720,7.763412", newText)
                .subscribeWith(new DisposableObserver<RestaurantPlace>() {


                    @Override
                    public void onNext(RestaurantPlace restaurantPlace) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSuccess(RestaurantPlace restaurantPlace) {
                        updateUi(restaurantPlace);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }


                });

    }

 */




    private void updateUi(RestaurantPlace restaurantPlace) {

        results.clear();
        results.addAll(restaurantPlace.getResults());
        myListViewRestaurantAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

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
        retrofitViewModel.init();
        retrofitViewModel.getResults();
        //retrofitViewModel.getResults().observe(requireActivity(),this::getName);
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
/*
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);


        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());

        recyclerView.addItemDecoration(mDividerItemDecoration);

 */

    }
}