package com.saiflimited.xpresenter.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentItemFragment extends RootFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONTENT_ITEM_ID = "CONTENT_ITEM_ID";
    private static final String TAB_NAME = "TAB_NAME";

    private long contentItemId;
    private String tabName;
    private DatabaseHandler db;

    public ContentItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contentItemId Parameter 1.
     * @return A new instance of fragment ContentItemFragment.
     */
    public static ContentItemFragment newInstance(long contentItemId, String tabName) {
        ContentItemFragment fragment = new ContentItemFragment();
        Bundle args = new Bundle();
        args.putLong(CONTENT_ITEM_ID, contentItemId);
        args.putString(TAB_NAME, tabName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contentItemId = getArguments().getLong(CONTENT_ITEM_ID);
            tabName = getArguments().getString(TAB_NAME);
        }
        db = DatabaseHandler.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (tabName.toUpperCase().equals("MENSAGENS")) {
            webView.loadData(db.getMessage(contentItemId), "text/html; charset=UTF-8", "base64");
        } else {
            webView.loadData(db.getHtmlBase64(contentItemId), "text/html; charset=UTF-8", "base64");
        }
        return view;
    }
}
