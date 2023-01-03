package com.example.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.go4lunch.Utils.LiveDataTestUtil;
import com.example.go4lunch.activities.DetailsRestaurantActivity;
import com.example.go4lunch.activities.MainActivity;
import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.fragments.ListViewFragment;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewModel.RetrofitViewModel;
import com.example.go4lunch.views.MyListViewRestaurantAdapter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DetailsRestaurantTests {
    public static final String CURRENT_USER_ID = "currentUserId";
    private final String firstRestaurantId = "First_Restaurant_Id";
    Result restaurantDetails;

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();



    private final Application application =
            mock(Application.class);

    private final DetailsRestaurantActivity detailsRestaurantActivity =
            mock(DetailsRestaurantActivity.class);

    private MenuItem menuItem = mock(MenuItem.class);
    private MenuItem.OnMenuItemClickListener onMenuItemClickListener = mock(MenuItem.OnMenuItemClickListener.class);
    private Injection injection = mock(Injection.class);
    private ViewModelFactory mViewModelFactory = mock(ViewModelFactory.class);

    @Mock
    private Intent intent;


    private final MainActivity mainActivity =
            mock(MainActivity.class);


   // MainActivity mainActivity = new MainActivity();
    @Mock
    ListViewFragment listViewFragment;
    //MainActivity mainActivity = new MainActivity();

    private final RecyclerView rcv = mock(RecyclerView.class);

    @Mock
    MyListViewRestaurantAdapter myListViewRestaurantAdapter;

    @Mock
    Bundle bundle;


    private final MutableLiveData<RestaurantPlace> restaurantLiveData =
            new MutableLiveData<>();

    private RetrofitViewModel retrofitViewModel;


    @Before
    public void setUp() throws InterruptedException{


        Context context = mock(Context.class);

        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(context);
        retrofitViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return new ViewModelStore();
            }
        }.getViewModelStore(), mViewModelFactory).get(RetrofitViewModel.class);





    }

    @Test
    public void check_details_restaurant_info() {


    }

    @Test
    public  void check_click_recycler_view(){

    }
}
