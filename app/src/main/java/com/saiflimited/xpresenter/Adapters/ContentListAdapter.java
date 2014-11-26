package com.saiflimited.xpresenter.Adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.saiflimited.xpresenter.Models.ContentData.Content;
import com.saiflimited.xpresenter.Models.ContentData.ContentDocument;
import com.saiflimited.xpresenter.R;

import java.util.ArrayList;

public class ContentListAdapter extends ArrayAdapter<ContentDocument> {
    private final Activity context;
    private ArrayList<ContentDocument> contentDocuments;

    public ContentListAdapter(Activity activity, ArrayList<ContentDocument> contentDocuments) {
        super(activity, R.layout.list_item_content, contentDocuments);
        this.context = activity;
        this.contentDocuments = contentDocuments;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = view;

        if (v == null) {
            v = this.context.getLayoutInflater().inflate(R.layout.list_item_content, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.lblBrand = ((TextView) v.findViewById(R.id.lblBrand));
            viewHolder.lblActivity = ((TextView) v.findViewById(R.id.lblActivity));
            viewHolder.lblDesc = ((TextView) v.findViewById(R.id.lblDesc));
            viewHolder.imageView = ((ImageView) v.findViewById(R.id.icon));
            v.setTag(viewHolder);
        }

        try {

            ViewHolder viewHolder = (ViewHolder) v.getTag();
            ContentDocument contentDocument = contentDocuments.get(position);
            Content content = contentDocument.getContent();
            if (content != null) {
                viewHolder.lblBrand.setText(content.getBrand());
                viewHolder.lblActivity.setText(content.getActivity());
                viewHolder.lblDesc.setText(content.getGoal());
                //TODO set Brand Image from content
//              viewHolder.imageView.setImageBitmap(content.getThumb());
            }
            return v;
        } catch (Exception localJSONException) {
            localJSONException.printStackTrace();
        }
        return v;
    }

    @Override
    public long getItemId(int pos) {
        return pos + 1;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView lblActivity;
        public TextView lblBrand;
        public TextView lblDesc;
    }

}