package com.saiflimited.xpresenter.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class ContentsFragment extends RootFragment implements AbsListView.OnItemClickListener {

    private static final String TAB_NAME = "TAB_NAME";
    private static final String FORMAT = "FORMAT";
    private String tabName;
    private String format;

    private AbsListView mListView;

    private ListAdapter mAdapter;

    private DatabaseHandler db;

    public ContentsFragment() {
    }

    public static ContentsFragment newInstance(String tabName, String format) {
        ContentsFragment fragment = new ContentsFragment();

        Bundle args = new Bundle();
        args.putString(TAB_NAME, tabName);
        args.putString(FORMAT, format);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DatabaseHandler.getInstance(getActivity());

        if (getArguments() != null) {
            tabName = getArguments().getString(TAB_NAME);
            format = getArguments().getString(FORMAT);
        }

        if (format.toUpperCase().equals("JSON")) {

            Cursor contentList = db.getContentList(tabName);
//            Log.i("[Icon URI]", contentList.getString(4));
            String fromColumns[] = {"brand", "activity", "goal", "icon"};
            int toViews[] = {R.id.lblBrand, R.id.lblActivity, R.id.lblDesc, R.id.icon};
            mAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.list_item_content_list, contentList, fromColumns, toViews);

        } else if (format.toUpperCase().equals("HTML") && tabName.toUpperCase().equals("MENSAGENS")) {

            Cursor messageList = db.getMessageList();
            String fromColumns[] = {"from", "fromDate", "name"};
            int toViews[] = {R.id.lblFrom, R.id.lblFromDate, R.id.lblDesc};
            mAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.list_item_content_list_msg, messageList, fromColumns, toViews);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        if (format.toUpperCase().equals("JSON") || tabName.toUpperCase().equals("MENSAGENS")) {

            view = inflater.inflate(R.layout.fragment_content_list, container, false);

            // Set the adapter
            mListView = (AbsListView) view.findViewById(R.id.listView);
            mListView.setAdapter(mAdapter);

            // Set OnItemClickListener so we can be notified on item clicks
            mListView.setOnItemClickListener(this);
            mListView.setEmptyView(view.findViewById(android.R.id.empty));

        } else {

            view = inflater.inflate(R.layout.fragment_webview, container, false);
            WebView webView = (WebView) view.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.loadData(db.getContentDoc(tabName), "text/html; charset=UTF-8", "base64");

        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment;
        if (tabName.toUpperCase().equals("MENSAGENS")) {
            fragment = ContentItemFragment.newInstance(id, tabName);
        } else {
            fragment = ContentDetailFragment.newInstance(id);
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.container, fragment).commit();
    }

}
