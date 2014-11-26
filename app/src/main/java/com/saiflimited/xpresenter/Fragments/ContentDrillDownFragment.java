/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.saiflimited.xpresenter.Fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.saiflimited.xpresenter.Adapters.ContentDrillDownAdapter;
import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.Models.ContentData.Content;
import com.saiflimited.xpresenter.Models.ContentData.ContentDetail;
import com.saiflimited.xpresenter.Models.ContentData.ContentDocument;
import com.saiflimited.xpresenter.R;
import com.saiflimited.xpresenter.UI.SlidingTabLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ContentDrillDownFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private ContentDrillDownPagerAdapter contentDrillDownPagerAdapter;
    private ListView listView;
    private DatabaseHandler db;
    private ContentDrillDownCallBack contentDrillDownCallBack;
    private ArrayList<ContentDetail> contentDetails;
    private int contentId;
    private Content content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentId = getArguments().getInt("position");

        db = DatabaseHandler.getInstance(getActivity());

        //Get Content Type Count (No of Tabs)
        //and Conteny Type Names (Tab Titles)

        //Tab Count
        int count = db.getPublisherContentTypeCount();

        //Tab Names
        String[] strings = new String[count];
        for (int i = 1; i <= count; i++) {
            strings[i - 1] = db.getPublisherContentTypeName(i);
        }
        contentDrillDownPagerAdapter = new ContentDrillDownPagerAdapter();
        contentDrillDownPagerAdapter.setCount(count);
        contentDrillDownPagerAdapter.setStrings(strings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        mViewPager.setAdapter(contentDrillDownPagerAdapter);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        //Get Publisher background
        byte[] bytes = Base64.decode(db.getBackground(), 0);
        Bitmap background = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Palette.generateAsync(background,
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        // do something with the colors
                        mSlidingTabLayout.setBackgroundColor(palette.getVibrantColor(Color.GRAY));
                        mSlidingTabLayout.setSelectedIndicatorColors(palette.getDarkVibrantColor(Color.GRAY));
                    }
                });

        mSlidingTabLayout.setDividerColors(Color.TRANSPARENT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            contentDrillDownCallBack = (ContentDrillDownCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onContinue");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contentDrillDownCallBack = null;
    }

    public static abstract interface ContentDrillDownCallBack {
        public abstract void onContentDrillDownItemClick(View view, int position, long id);

    }

    class ContentDrillDownPagerAdapter extends PagerAdapter {

        private int tabsCount = 0;
        private String[] strings;

        public void setStrings(String[] strings) {
            this.strings = strings;
        }

        @Override
        public int getCount() {
            return tabsCount;
        }

        public void setCount(int i) {
            tabsCount = i;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return strings[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_content_drill_down,
                    container, false);

            // Add the newly created View to the ViewPager
            container.addView(view);

            // Initialize contentDocuments
            contentDetails = new ArrayList<ContentDetail>();

            // Get Content Document for clicked Content ID
            String contentDoc = db.getContentDoc(contentId);

            if (contentDoc != null) {

                try {
                    // Decode Content Document
                    String decodedContentDoc = new String(Base64.decode(contentDoc, 0), "UTF-8");

                    // Parse JSON to Object
                    Gson gson = new Gson();
                    ContentDocument contentDocument = gson.fromJson(decodedContentDoc, ContentDocument.class);

                    // Get Content
                    content = contentDocument.getContent();

                    // Get Content Detail List
                    contentDetails = content.getContentDetailList();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            ImageView imgHeader = ((ImageView) view.findViewById(R.id.imgHeader));
            TextView lblBrand = ((TextView) view.findViewById(R.id.lblBrand));
            TextView lblActivity = ((TextView) view.findViewById(R.id.lblActivity));
            TextView lblGoal = ((TextView) view.findViewById(R.id.lblGoal));
            TextView lblDateStart = ((TextView) view.findViewById(R.id.lblDateStart));
            TextView lblReachDesc = ((TextView) view.findViewById(R.id.lblReachDesc));
            TextView lblRule = ((TextView) view.findViewById(R.id.lblRule));

            //TODO set content.HeaderImg. Use sample for now
            imgHeader.setImageResource(R.drawable.pic1);
            lblBrand.setText(content.getBrand());
            lblActivity.setText(content.getActivity());
            lblGoal.setText(content.getGoal());
            lblDateStart.setText(content.getDateStart());
            lblReachDesc.setText(content.getReachDescription());
            lblRule.setText(content.getRule());

            // Get ListView
            listView = (ListView) view.findViewById(R.id.listView);

            // Get Adapter to populate ListView
            ContentDrillDownAdapter contentDrillDownAdapter = new ContentDrillDownAdapter(getActivity(), contentDetails);

            // Set ListView Adapter
            listView.setAdapter(contentDrillDownAdapter);

            // Set setOnItemClickListener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long seq) {
                    contentDrillDownCallBack.onContentDrillDownItemClick(view, position, seq);
                }
            });

            // Return the View
            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }

}
