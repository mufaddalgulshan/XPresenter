package com.saiflimited.xpresenter.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.Models.Content.Content;
import com.saiflimited.xpresenter.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class ContentDetailFragment extends RootFragment implements ExpandableListView.OnChildClickListener {

    // the fragment initialization parameters
    private static final String CONTENT_ID = "CONTENT_ID";
    /**
     * Database Handler to handle database operations
     */
    DatabaseHandler db;
    private long contentId;
    /**
     * The fragment's ListView/GridView.
     */
    private ExpandableListView mListView;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SimpleCursorTreeAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContentDetailFragment() {
    }

    public static ContentDetailFragment newInstance(long contentId) {
        ContentDetailFragment fragment = new ContentDetailFragment();
        Bundle args = new Bundle();
        args.putLong(CONTENT_ID, contentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            contentId = getArguments().getLong(CONTENT_ID);
        }

        db = DatabaseHandler.getInstance(getActivity());
        Cursor groupCursor = db.getContentDrilldown(contentId);

        mAdapter = new ContentsTreeAdapter(getActivity(), groupCursor);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_detail_list, container, false);

        // Set the adapter
        mListView = (ExpandableListView) view.findViewById(android.R.id.list);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        LinearLayout listHeaderView = (LinearLayout) layoutInflater.inflate(
                R.layout.content_header, null);

        Content content = db.getContent(contentId);

        TextView lblBrand = (TextView) listHeaderView.findViewById(R.id.lblBrand);
        TextView lblActivity = (TextView) listHeaderView.findViewById(R.id.lblActivity);
        TextView lblGoal = (TextView) listHeaderView.findViewById(R.id.lblGoal);
        TextView lblDateStart = (TextView) listHeaderView.findViewById(R.id.lblDateStart);
        TextView lblReachDesc = (TextView) listHeaderView.findViewById(R.id.lblReachDesc);
        TextView lblRule = (TextView) listHeaderView.findViewById(R.id.lblRule);
        ImageView imgHeader = (ImageView) listHeaderView.findViewById(R.id.imgHeader);

        lblBrand.setText(content.getBrand());
        lblActivity.setText(content.getActivity());
        lblGoal.setText(content.getGoal());
        lblDateStart.setText(content.getDateStart());
        lblReachDesc.setText(content.getReachDescription());
        lblRule.setText(content.getRule());
        imgHeader.setImageURI(Uri.parse(content.getImage()));
        Log.i("[Content Header]", content.getImage());

        mListView.addHeaderView(listHeaderView);


        mListView.setAdapter(mAdapter);

        // Set OnChildClickListener so we can be notified on item clicks
        mListView.setOnChildClickListener(this);

        return view;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Fragment fragment = ContentItemFragment.newInstance(id, "");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Log.i("[groupPosition]", String.valueOf(groupPosition));
        Log.i("[childPosition]", String.valueOf(childPosition));
        Log.i("[id]", String.valueOf(id));

        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.container, fragment).commit();
        return false;
    }

    private class ContentsTreeAdapter extends SimpleCursorTreeAdapter {

        public ContentsTreeAdapter(Context context, Cursor cursor) {
            super(
                    context,
                    cursor,
                    R.layout.list_item_content_detail_list,
                    new String[]{"name"},
                    new int[]{R.id.lblContentDetailName},
                    R.layout.list_item_content_item_list,
                    new String[]{"name", "icon"},
                    new int[]{R.id.lblContentDetailName, R.id.icon});
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            int seq = groupCursor.getInt(0);
            return db.getContentDetailsDrilldown(contentId, seq);
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
            super.bindChildView(view, context, cursor, isLastChild);

            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            Log.i("icon", cursor.getString(2));
            icon.setImageURI(Uri.parse(cursor.getString(2)));
        }

    }
}
