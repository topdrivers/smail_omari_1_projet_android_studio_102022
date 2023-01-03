package com.example.go4lunch;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.view.menu.MenuView;

import com.example.go4lunch.dataSource.models.Photo;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.fragments.ListViewFragment;
import com.example.go4lunch.views.MyListViewRestaurantAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class check_printing_default_picture_InlistView {

    @Mock
    ListViewFragment listViewFragment;

    @Mock
    View view;

    @Mock
    MyListViewRestaurantAdapter myListViewRestaurantAdapter;

    @Mock
    MyListViewRestaurantAdapter.ViewHolder viewHolder;

    @Mock
    LayoutInflater layoutInflater;

    @Mock
    ViewGroup viewGroup;

    @Mock
    Bundle bundle;



    @Before
    public void setUp() throws InterruptedException {
        listViewFragment = new ListViewFragment();
        /*
        view = listViewFragment.onCreateView(layoutInflater, viewGroup, bundle);
        listViewFragment.configureRecyclerView(view);
        listViewFragment.initList();
         */
        List<Result> results = new ArrayList<>();
        Result result_with_picure = new Result();
        result_with_picure.setName("L'auberge");
        result_with_picure.setVicinity("Rue de la lamproie");
        Photo photo = new Photo();
        photo.setPhotoReference("C:\\Users\\smail\\Downloads\\photo_reference.jpg");
        //result_with_picure.getPhotos().set(0, photo);
        List<Photo> photoList = new ArrayList<>();
        photoList.add(photo);
        result_with_picure.setPhotos(photoList);

        Result result_without_picure = new Result();
        result_with_picure.setName("L'auberge");
        result_with_picure.setVicinity("Rue de la lamproie");
        results.add(result_with_picure);
        results.add(result_without_picure);
        view = new View(listViewFragment.getContext());
        viewHolder = new MyListViewRestaurantAdapter.ViewHolder(view);
       myListViewRestaurantAdapter = new MyListViewRestaurantAdapter(results);
        myListViewRestaurantAdapter.onBindViewHolder(viewHolder,1);

    }

    @Test
    public  void check_printing_default_picture_atPosition2(){
        //given
        //viewHolder.restaurantAvatar.getDrawable().equals(null);

        //when


        //then
        assertEquals(null, viewHolder.restaurantAvatar.getDrawable());
    }

}
