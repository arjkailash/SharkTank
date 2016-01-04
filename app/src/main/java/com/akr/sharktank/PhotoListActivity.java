package com.akr.sharktank;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class PhotoListActivity extends ActionBarActivity {
    private final static String PHOTOLISTFRAGMENT = "PhotoListFragment";
    private final static String PHOTOFRAGMENT = "PhotoFragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(PHOTOLISTFRAGMENT) == null &&
                fragmentManager.findFragmentByTag(PHOTOFRAGMENT) == null) {
            PhotoListFragment photoListFragment = new PhotoListFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.photo_list_holder, photoListFragment, PHOTOLISTFRAGMENT).commit();

        }
    }
}
