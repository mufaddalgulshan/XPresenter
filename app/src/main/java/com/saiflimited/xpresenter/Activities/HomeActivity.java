/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.saiflimited.xpresenter.Activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.Fragments.HomeFragment;
import com.saiflimited.xpresenter.R;
import com.saiflimited.xpresenter.Utils.Utils;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p/>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class HomeActivity extends ActionBarActivity {

    private DatabaseHandler db;

    private Bitmap background;
    private HomeFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Get DatabaseHandler instance
        db = DatabaseHandler.getInstance(this);

        //Get Publisher background Bitmap
        background = Utils.getBitmapFromBase64(db.getBackground());

        //Set toolbar title and background
        setupToolbar();


        if (savedInstanceState == null) {
            // withholding the previously created fragment from being created again
            // On orientation change, it will prevent fragment recreation
            // its necessary to reserving the fragment stack inside each tab
            initScreen();

        } else {
            // restoring the previously created fragment
            // and getting the reference
            fragment = (HomeFragment) getSupportFragmentManager().getFragments().get(0);
        }
    }

    private void initScreen() {
        // Creating the ViewPager container fragment once
        fragment = new HomeFragment();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.home_fragment, fragment)
                .commit();
    }

    private void setupToolbar() {

        //Get app name from db
//        String appname = db.getAppName();

        Drawable toolbarBackground = new BitmapDrawable(background);

        //Set toolbar properties
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(appname);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setBackground(toolbarBackground);

        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageBitmap(Utils.getBitmapFromBase64(db.getLogo()));

        //Set status bar color based on background
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        Palette.generateAsync(background,
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        drawerLayout.setStatusBarBackgroundColor(palette.getDarkVibrantColor(Color.DKGRAY));
                        toolbar.setTitleTextColor(palette.getDarkVibrantColor(Color.DKGRAY));
                        toolbar.setBackgroundColor(palette.getVibrantColor(Color.DKGRAY));
                    }
                }
        );
    }

    /**
     * Only Activity has this special callback method
     * Fragment doesn't have any onBackPressed callback
     * <p/>
     * Logic:
     * Each time when the back button is pressed, this Activity will propagate the call to the
     * container Fragment and that Fragment will propagate the call to its each tab Fragment
     * those Fragments will propagate this method call to their child Fragments and
     * eventually all the propagated calls will get back to this initial method
     * <p/>
     * If the container Fragment or any of its Tab Fragments and/or Tab child Fragments couldn't
     * handle the onBackPressed propagated call then this Activity will handle the callback itself
     */
    @Override
    public void onBackPressed() {

        if (!fragment.onBackPressed()) {
            // container Fragment or its associates couldn't handle the back pressed task
            // delegating the task to super class
            super.onBackPressed();

        }
    }

}
