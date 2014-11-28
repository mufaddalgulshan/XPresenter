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

package com.saiflimited.xpresenter.Views.Fragments;

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
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.Models.ContentData.ContentDocument;
import com.saiflimited.xpresenter.Models.ContentData.ContentItemList;
import com.saiflimited.xpresenter.R;
import com.saiflimited.xpresenter.Views.Adapters.ContentDetailDrillDownAdapter;
import com.saiflimited.xpresenter.Views.Widgets.SlidingTabLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ContentDetailDrillDownFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private ContentDrillDownPagerAdapter contentDrillDownPagerAdapter;
    private ListView listView;
    private DatabaseHandler db;
    private int contentDetailItemId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DatabaseHandler.getInstance(getActivity());

        int count = db.getPublisherContentTypeCount();
        contentDetailItemId = getArguments().getInt("sequence");
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

        byte[] arrayOfByte = Base64.decode(db.getBackground(), 0);
        Bitmap localBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);

        Palette.generateAsync(localBitmap,
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
            String format = db.getFormat(getPageTitle(position));
            if (format.toUpperCase().equals("JSON")) {
                // Inflate a new layout from our resources
                View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_content_detail_drill_down_json,
                        container, false);
                // Add the newly created View to the ViewPager
                container.addView(view);

                ArrayList<ContentItemList> contentItemLists;

                String contentDoc = db.getContentDoc(1);
                ContentDocument contentDocument = new ContentDocument();
                if (contentDoc != null) {
                    String decodedContentDoc;
                    try {
                        decodedContentDoc = new String(Base64.decode(contentDoc, 0), "UTF-8");
                        Gson gson = new Gson();
                        contentDocument = gson.fromJson(decodedContentDoc, ContentDocument.class);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                contentItemLists = contentDocument.getContent().getContentDetailList().get(contentDetailItemId).getContentItemList();
                TextView lblContentDetailName = ((TextView) view.findViewById(R.id.lblContentDetailName));

                lblContentDetailName.setText(contentDocument.getContent().getContentDetailList().get(contentDetailItemId).getName());
                listView = (ListView) view.findViewById(R.id.listView);
                ContentDetailDrillDownAdapter contentDrillDownAdapter = new ContentDetailDrillDownAdapter(getActivity(), contentItemLists, format);
                listView.setAdapter(contentDrillDownAdapter);

                // Return the View
                return view;
            } else {
                // Inflate a new layout from our resources
                View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_content_detail_drill_down_html,
                        container, false);
                // Add the newly created View to the ViewPager
                container.addView(view);

                ArrayList<ContentItemList> contentItemLists;

                String contentDoc = db.getContentDoc(1);
                ContentDocument contentDocument = new ContentDocument();

                if (contentDoc != null) {
                    String decodedContentDoc;
                    try {
                        decodedContentDoc = new String(Base64.decode(contentDoc, 0), "UTF-8");
                        Gson gson = new Gson();
                        contentDocument = gson.fromJson(decodedContentDoc, ContentDocument.class);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                contentItemLists = contentDocument.getContent().getContentDetailList().get(contentDetailItemId).getContentItemList();
                listView = (ListView) view.findViewById(R.id.listView);
                ContentDetailDrillDownAdapter contentDrillDownAdapter = new ContentDetailDrillDownAdapter(getActivity(), contentItemLists, format);
                listView.setAdapter(contentDrillDownAdapter);

                // Return the View
                return view;
            }
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }
}
