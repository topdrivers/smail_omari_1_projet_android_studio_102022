package com.example.go4lunch.fragments;

import static com.example.go4lunch.activities.MainActivity.userViewModel;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.go4lunch.model.User;
import com.example.go4lunch.viewModel.RetrofitViewModel;
import com.example.go4lunch.views.MyListViewRestaurantAdapter;
import com.example.go4lunch.views.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkmatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkmatesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    WorkmatesAdapter workmatesAdapter;
    RecyclerView recyclerView;
    List<User> userList;
    private RetrofitViewModel retrofitViewModel;
    private List<Result> resultList;


    public WorkmatesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkmatesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkmatesFragment newInstance(String param1, String param2) {
        WorkmatesFragment fragment = new WorkmatesFragment();
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
        configureViewModel();
        initRestaurantList();
        if(workmatesAdapter!=null) {
            initUserList();
        }
        System.out.println("----------1--------------");




    }

    @Override
    public void onResume() {
        super.onResume();
        configureViewModel();

        initRestaurantList();
        initUserList();
        System.out.println("----------2--------------");



    }

    private void initUserList()   {
        //userViewModel.getUserList().observe(this,this::userList);
        userViewModel.getAllUsersFromDataBase();
        userViewModel.getAllUsers().observe(this, this::userList);
        System.out.println("----------------list user--------------"+userViewModel.getUserList());

    }

    private void configureViewModel(){
        this.resultList = new ArrayList<>();
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(requireActivity());
        retrofitViewModel = new ViewModelProvider(this, mViewModelFactory).get(RetrofitViewModel.class);
        retrofitViewModel.init();
        retrofitViewModel.getResults();
        //retrofitViewModel.getResults().observe(requireActivity(),this::getName);
    }

    private void initRestaurantList()   {
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
        System.out.println("--------------getlistrestaurant---------------"+restaurantPlace.getResults().get(0).getPlaceId());
        resultList.clear();
        resultList.addAll(restaurantPlace.getResults());
        System.out.println("-------------getlistresto resultlist---------"+resultList.get(3).getPlaceId());
        //configureRecyclerView(getView());
        workmatesAdapter.notifyDataSetChanged();

    }


    private void userList(List<User> users) {


        userList.clear();
        userList.addAll(users);
        workmatesAdapter = new WorkmatesAdapter(userList, resultList, requireContext());
        recyclerView.setAdapter(workmatesAdapter);
        workmatesAdapter.notifyDataSetChanged();

        System.out.println("--------------passage appel workmates adapter");

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("----------3--------------");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_workmates, container, false);

        configureRecyclerView(view);

        return view;
    }




    private void configureRecyclerView(View view) {
        this.userList = new ArrayList<>();
//        this.resultList = new ArrayList<>();
        Context context = getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.item_recycler_view);


        workmatesAdapter = new WorkmatesAdapter(userList, resultList, requireContext());
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
/*
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);


        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());

        recyclerView.addItemDecoration(mDividerItemDecoration);

 */

    }

}