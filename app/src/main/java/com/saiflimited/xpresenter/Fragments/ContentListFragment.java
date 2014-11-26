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
import android.widget.ListView;

import com.google.gson.Gson;
import com.saiflimited.xpresenter.Adapters.ContentListAdapter;
import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.Models.ContentData.ContentDocument;
import com.saiflimited.xpresenter.R;
import com.saiflimited.xpresenter.UI.SlidingTabLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ContentListFragment extends Fragment {

    static final String LOG_TAG = "ContentListFragment";
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private ContentListPagerAdapter mContentListPagerAdapter;
    private ListView listView;
    private DatabaseHandler db;
    private ContentCallBack mContentCallBack;
    private ArrayList<ContentDocument> contentDocuments;
    private int mVibrantColor;
    private int mDarkVibrantColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        mContentListPagerAdapter = new ContentListPagerAdapter();
        mContentListPagerAdapter.setCount(count);
        mContentListPagerAdapter.setStrings(strings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        mViewPager.setAdapter(mContentListPagerAdapter);
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
                        mVibrantColor = palette.getVibrantColor(Color.GRAY);
                        mDarkVibrantColor = palette.getDarkVibrantColor(Color.GRAY);
                        mSlidingTabLayout.setBackgroundColor(mVibrantColor);
                        mSlidingTabLayout.setSelectedIndicatorColors(mDarkVibrantColor);
                    }
                });

        mSlidingTabLayout.setDividerColors(Color.TRANSPARENT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mContentCallBack = (ContentCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onContinue");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContentCallBack = null;
    }

    public static abstract interface ContentCallBack {
        public abstract void onContentItemListClick(View view, int position, long id);

    }

    class ContentListPagerAdapter extends PagerAdapter {

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

        /**
         * @return true if the value returned from {@link #instantiateItem(android.view.ViewGroup, int)} is the
         * same object as the {@link android.view.View} added to the {@link android.support.v4.view.ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return strings[position];
        }

        /**
         * Instantiate the {@link android.view.View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_content,
                    container, false);

            // Add the newly created View to the ViewPager
            container.addView(view);

            // Initialize contentDocuments
            contentDocuments = new ArrayList<ContentDocument>();

            // Get Content Count for Publisher
            int iContentCount = db.getContentCount();

            for (int i = 0; i < iContentCount; i++) {

                // Get Content Document
                String contentDoc = db.getContentDoc(1);

                if (contentDoc != null) {

                    try {
                        // Decode Content Document
                        String decodedContentDoc = new String(Base64.decode(contentDoc, 0), "UTF-8");
                        Log.i("ContentListAdapter", "[ContentDocText] " + decodedContentDoc);

                        // Parse JSON to Object
                        Gson gson = new Gson();
                        ContentDocument contentDocument = gson.fromJson(decodedContentDoc, ContentDocument.class);
                        contentDocuments.add(contentDocument);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }

            // Get ListView
            listView = (ListView) view.findViewById(R.id.listView);

            // Get Adapter to populate ListView
            ContentListAdapter contentListAdapter = new ContentListAdapter(getActivity(), contentDocuments);

            // Set ListView Adapter
            listView.setAdapter(contentListAdapter);

            // Set setOnItemClickListener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long contentId) {
                    mContentCallBack.onContentItemListClick(view, position, contentId);
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
