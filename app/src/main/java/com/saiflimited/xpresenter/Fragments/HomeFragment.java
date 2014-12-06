package com.saiflimited.xpresenter.Fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.R;
import com.saiflimited.xpresenter.Views.Widgets.SlidingTabLayout;

public class HomeFragment extends Fragment {

    static final String LOG_TAG = "ContentListFragment";
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private String[] TITLES;


    private ContentListPagerAdapter mContentListPagerAdapter;
    private DatabaseHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DatabaseHandler.getInstance(getActivity());
    }

    private void setupSlidingTabs(View view) {

        //Get Content Type Count (No of Tabs) and Content Type Names (Tab Titles)
        TITLES = getTabNames();

        //Get Publisher background
        byte[] bytes = Base64.decode(db.getBackground(), 0);
        Bitmap background = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Palette.generateAsync(background,
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        mSlidingTabLayout.setBackgroundColor(palette.getVibrantColor(Color.GRAY));
                        mSlidingTabLayout.setSelectedIndicatorColors(palette.getDarkVibrantColor(Color.GRAY));
                    }
                });

        mContentListPagerAdapter = new ContentListPagerAdapter(getResources(), getChildFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mContentListPagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setDividerColors(Color.TRANSPARENT);
    }

    private String[] getTabNames() {
        //Tab Count
        int count = db.getPublisherContentTypeCount();

        //Tab Names
        String[] strings = new String[count];
        for (int i = 1; i <= count; i++) {
            strings[i - 1] = db.getPublisherContentTypeName(i);
        }

        return strings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_tabs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Setup slidings tabs
        setupSlidingTabs(view);
    }

    /**
     * Retrieve the currently visible Tab Fragment and propagate the onBackPressed callback
     *
     * @return true = if this fragment and/or one of its associates Fragment can handle the backPress
     */
    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) mContentListPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            return currentFragment.onBackPressed();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }

    class ContentListPagerAdapter extends FragmentPagerAdapter {

        private final Resources resources;

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public ContentListPagerAdapter(final Resources resources, FragmentManager fm) {
            super(fm);
            this.resources = resources;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int i) {

            String tabName = TITLES[i];
            Fragment fragment = ContentsFragment.newInstance(tabName);

            return fragment;
        }

        /**
         * On each Fragment instantiation we are saving the reference of that Fragment in a Map
         * It will help us to retrieve the Fragment by position
         *
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */
        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}
