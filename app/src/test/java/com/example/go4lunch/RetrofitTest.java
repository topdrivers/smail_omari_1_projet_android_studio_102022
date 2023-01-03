package com.example.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;


import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.Utils.LiveDataTestUtil;
import com.example.go4lunch.viewModel.RetrofitViewModel;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
public class RetrofitTest {



    private Context context;
    RetrofitViewModel retrofitViewModel;
    private static final String NAME_TEST = "Aldo Pizza Neudorf";
    private final String PLACE_ID = "ChIJv8ZNmp3JlkcRrNw8pvmbb4E";
    private final Double RATING = 4.4;
    private final String REFERENCE = "ChIJ3dsMIAzJlkcRavdegCsfEvs";
    private final String ADDRESS = "7 Rue Leitersperger, Strasbourg";


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {

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


    @After
    public void closeDb() throws Exception {

    }

    @Test
    public void check_second_item_name_retrofit_request_reception() throws InterruptedException {

        // TEST
        RestaurantPlace restaurantPlace = LiveDataTestUtil.getValue(this.retrofitViewModel.getResults());
        String name =  restaurantPlace.getResults().get(1).getName();
        assertEquals(name, NAME_TEST);

    }

    @Test
    public void check_third_item_id_retrofit_request_reception() throws InterruptedException {

        // TEST
        RestaurantPlace restaurantPlace = LiveDataTestUtil.getValue(this.retrofitViewModel.getResults());
        String placeId = restaurantPlace.getResults().get(2).getPlaceId();
        assertEquals(placeId, PLACE_ID);

    }

    @Test
    public void check_fourth_item_rating_retrofit_request_reception() throws InterruptedException {

        // TEST
        RestaurantPlace restaurantPlace = LiveDataTestUtil.getValue(this.retrofitViewModel.getResults());
        Double rating = restaurantPlace.getResults().get(3).getRating();
        assertEquals(rating, RATING);

    }


    @Test
    public void check_fifth_item_rating_retrofit_request_reception() throws InterruptedException{

        RestaurantPlace restaurantPlace = LiveDataTestUtil.getValue(this.retrofitViewModel.getResults());
        String address = restaurantPlace.getResults().get(4).getVicinity();
        assertEquals(address, ADDRESS);


    }



}
