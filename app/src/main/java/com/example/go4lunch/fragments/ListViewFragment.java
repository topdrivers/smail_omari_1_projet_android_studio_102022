package com.example.go4lunch.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewModel.RetrofitViewModel;
import com.example.go4lunch.views.MyListViewRestaurantAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private RetrofitViewModel retrofitViewModel;
    private List<Result> results;

     //@BindView(R.id.recyclerview_list_view) RecyclerView recyclerView;
     //@BindView(R.id.fragment_list_view_swipe_container) SwipeRefreshLayout swipeRefreshLayout;
     RecyclerView recyclerView;
     SwipeRefreshLayout swipeRefreshLayout;

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
    public void onResume() {
        super.onResume();
        configureViewModel();

        initList();
    }

    private void initList()   {
        retrofitViewModel.getResults().observe(this,this::getListRestaurant);

    }

    private void getListRestaurant(RestaurantPlace restaurantPlace) {
        /*
        results = restaurantPlace.getResults();
        myListViewRestaurantAdapter = new MyListViewRestaurantAdapter(results);
        System.out.println("----------recycler view-----------"+recyclerView);
        recyclerView.setAdapter(myListViewRestaurantAdapter);
        myListViewRestaurantAdapter.notifyDataSetChanged();

         */
        results.clear();
        results.addAll(restaurantPlace.getResults());
        myListViewRestaurantAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(requireActivity());
        retrofitViewModel = new ViewModelProvider(this, mViewModelFactory).get(RetrofitViewModel.class);
        retrofitViewModel.init();
        retrofitViewModel.getResults();
        //retrofitViewModel.getResults().observe(requireActivity(),this::getName);
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

        configureRecyclerView(view);

        configureSwipeRefreshLayout();

        return view;
    }

    private void configureRecyclerView(View view) {
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