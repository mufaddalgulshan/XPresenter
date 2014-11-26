package com.saiflimited.xpresenter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.saiflimited.xpresenter.Models.ContentData.ContentDetail;

import java.util.ArrayList;

public class ContentDrillDownAdapter extends ArrayAdapter<ContentDetail> {
    private static final String TAG = "ContentListAdapter";
    private final Activity context;
    private final ArrayList<ContentDetail> contentDetails;

    public ContentDrillDownAdapter(Activity activity, ArrayList<ContentDetail> contentDetails) {
        super(activity, R.layout.list_item_content_drill_down, contentDetails);
        this.context = activity;
        this.contentDetails = contentDetails;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            v = this.context.getLayoutInflater().inflate(R.layout.list_item_content_drill_down, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.lblContentDetailName = ((TextView) v.findViewById(R.id.lblContentDetailName));
            v.setTag(viewHolder);
        }
        try {

            ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (contentDetails != null) {
                viewHolder.lblContentDetailName.setText(contentDetails.get(position).getName());

            }
            return v;
        } catch (Exception localJSONException) {
            localJSONException.printStackTrace();
        }
        return v;
    }

    static class ViewHolder {
        public TextView lblContentDetailName;
    }
}