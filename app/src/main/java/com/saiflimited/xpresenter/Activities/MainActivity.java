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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;

import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.Fragments.ContentDetailDrillDownFragment;
import com.saiflimited.xpresenter.Fragments.ContentDrillDownFragment;
import com.saiflimited.xpresenter.Fragments.ContentListFragment;
import com.saiflimited.xpresenter.R;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p/>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends ActionBarActivity
        implements ContentListFragment.ContentCallBack,
        ContentDrillDownFragment.ContentDrillDownCallBack {


    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DatabaseHandler.getInstance(this);
        byte[] arrayOfByte = Base64.decode(db.getBackground(), 0);
        Bitmap background = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
        Drawable d = new BitmapDrawable(background);
        String appname = db.getAppName();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(appname);
        setSupportActionBar(toolbar);
        toolbar.setBackground(d);


        // Now retrieve the DrawerLayout so that we can set the status bar color.
        // This only takes effect on Lollipop, or when using translucentStatusBar
        // on KitKat.
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);

        Palette.generateAsync(background,
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        // do something with the colors
                        drawerLayout.setStatusBarBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
                    }
                });


        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ContentListFragment fragment = new ContentListFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onContentItemListClick(View view, int position, long contentId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ContentDrillDownFragment fragment = new ContentDrillDownFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", (int) contentId);
        fragment.setArguments(bundle);
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.addToBackStack(fragment.getTag());
        transaction.commit();
    }

    @Override
    public void onContentDrillDownItemClick(View view, int position, long seq) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ContentDetailDrillDownFragment fragment = new ContentDetailDrillDownFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sequence", (int) seq);
        fragment.setArguments(bundle);
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.addToBackStack(fragment.getTag());
        transaction.commit();
    }


}
