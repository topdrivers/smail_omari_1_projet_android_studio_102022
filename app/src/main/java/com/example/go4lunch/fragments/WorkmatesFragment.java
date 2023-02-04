package com.example.go4lunch.fragments;

import static com.example.go4lunch.activities.MainActivity.userViewModel;
import static com.example.go4lunch.fragments.MapsFragment.mLocation;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.User;
import com.example.go4lunch.viewModel.RetrofitViewModel;
import com.example.go4lunch.views.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    WorkmatesAdapter workmatesAdapter;
    RecyclerView recyclerView;
    List<User> userList;
    private RetrofitViewModel retrofitViewModel;
    private List<Result> resultList;


    public WorkmatesFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureViewModel();
        initRestaurantList();
        if(workmatesAdapter!=null) {
            initUserList();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onResume() {
        super.onResume();
        configureViewModel();
        initRestaurantList();
        initUserList();
    }

    private void initUserList()   {
        userViewModel.getAllUsersFromDataBase();
        userViewModel.getAllUsers().observe(this, this::userList);
    }

    private void configureViewModel(){
        this.resultList = new ArrayList<>();
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(requireActivity());
        retrofitViewModel = new ViewModelProvider(this, mViewModelFactory).get(RetrofitViewModel.class);
        retrofitViewModel.init(mLocation.getLatitude(), mLocation.getLongitude());
        retrofitViewModel.getResults(mLocation.getLatitude(), mLocation.getLongitude());
    }

    private void initRestaurantList()   {
        retrofitViewModel.getResults(mLocation.getLatitude(), mLocation.getLongitude()).observe(this,this::getListRestaurant);

    }

    private void getListRestaurant(RestaurantPlace restaurantPlace) {
        resultList.clear();
        resultList.addAll(restaurantPlace.getResults());
        workmatesAdapter.notifyDataSetChanged();
    }


    private void userList(List<User> users) {
        userList.clear();
        userList.addAll(users);
        workmatesAdapter = new WorkmatesAdapter(userList, resultList, requireContext());
        recyclerView.setAdapter(workmatesAdapter);
        workmatesAdapter.notifyDataSetChanged();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_workmates, container, false);

        configureRecyclerView(view);

        return view;
    }

    private void configureRecyclerView(View view) {
        this.userList = new ArrayList<>();
        Context context = getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.item_recycler_view);
        workmatesAdapter = new WorkmatesAdapter(userList, resultList, requireContext());
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

}