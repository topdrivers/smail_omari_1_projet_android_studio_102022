package com.example.go4lunch.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.go4lunch.Fragments.MapsFragment;
import com.example.go4lunch.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureMapsFragment();
    }

    private void configureMapsFragment() {
        MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_main);
        if(mapsFragment==null){
            mapsFragment = new MapsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_main,mapsFragment)
                    .commit();
        }
    }
}