package com.saiflimited.xpresenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saiflimited.xpresenter.Models.Content;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SyncFragment extends Fragment {

    private static final String GET_CONTENT = "http://uatwebservices.ipremios.com/XPresenter/GetContent?contentId=";
    private static final String GET_CONTENT_LIST = "http://uatwebservices.ipremios.com/XPresenter/GetContentList";
    private static String IMEI;
    private static final String TAG = "SyncFragment";
    private static String USERNAME;
    private static DatabaseHandler db;
    private static Cursor CONTENT_ID_CURSOR;
    private Button btnAccessApp;
    private Button btnSync;
    private Bundle mBundle;
    private int mPIN;
    private ProgressDialog progressDialog;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private SyncCallback syncCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseHandler.getInstance(getActivity());
        IMEI = ((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sync, container, false);

        if (view != null) {
            mBundle = getArguments();
            USERNAME = mBundle.getString("USERNAME");
            btnSync = ((Button) view.findViewById(R.id.btnSync));
            btnAccessApp = ((Button) view.findViewById(R.id.btnAccessApp));
            btnSync.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() != 0) {
                        event.getAction();
                    }
                    return false;
                }
            });
            btnAccessApp.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() != 0) {
                        event.getAction();
                    }
                    return false;
                }
            });
            btnSync.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (isConnected()) {
                        new getContentList().execute(new String[]{GET_CONTENT_LIST});
                    } else {
                        showSettingsAlert();
                    }
                }
            });
            btnAccessApp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("USERNAME", USERNAME);
                    startActivity(intent);
                }
            });
        }

        if (!maxSyncDelta()) {
            syncCallback.onSyncMandatory();
            btnAccessApp.setVisibility(View.GONE);
        } else {
            if (!db.contentExists()) {
                new checkForNewContent().execute(new String[]{GET_CONTENT_LIST});
            }
        }

        return view;
    }

    private boolean maxSyncDelta() {
        boolean valid = false;
        String strLastSyncDate = db.getLastSyncDate(USERNAME);

        Date today = new Date();
        try {
            if (!strLastSyncDate.isEmpty()) {

                Date lastSyncDate = simpleDateFormat.parse(strLastSyncDate);
                int maxSyncDelta = db.getMaxSyncDelta(USERNAME);
                int diffInDays = (int) ((today.getTime() - lastSyncDate.getTime()) / (1000 * 60 * 60 * 24));
                if (diffInDays <= maxSyncDelta) {
                    valid = true;
                }

                Log.i(TAG, "[MaxSyncDelta] " + maxSyncDelta);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return valid;
    }

    public boolean isConnected() {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (localNetworkInfo != null) && (localNetworkInfo.isConnected());
    }

    private class checkForNewContent extends AsyncTask<String, Void, String> {
        private checkForNewContent() {
        }

        protected String doInBackground(String... url) {
            return prepareRequestToGetContentList(url[0]);
        }

        protected void onPostExecute(String paramString) {

            JSONArray jResponse = null;
            try {
                jResponse = new JSONArray(paramString);
                if (jResponse.getInt(0) == 0) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>() {
                    }.getType();
                    ArrayList<Content> contentArrayList = gson.fromJson(jResponse.getJSONArray(2).toString(), listType);
                    if (contentArrayList.size() > 0) {
                        for (int i = 0; i < contentArrayList.size(); i++) {
                            if (contentUpdateAvailable(contentArrayList.get(i).getId(), contentArrayList.get(i).getLastUpdated())) {
                                syncCallback.onNewContentAvailable();
                                break;
                            }
                            contentArrayList.get(i).getLastUpdated();
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Checking for updated content...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private boolean contentUpdateAvailable(int contentId, String strNewContentUpdateDate) {
        boolean valid = false;
        String strLastUpdateDate = db.getLastContentUpdateDate(contentId);
        try {
            if (!strLastUpdateDate.isEmpty()) {
                Date lastUpdateDate = simpleDateFormat.parse(strLastUpdateDate);
                Date newContentUpdateDate = simpleDateFormat.parse(strNewContentUpdateDate);
                if (newContentUpdateDate.after(lastUpdateDate)) {
                    valid = true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return valid;
    }

    private class getContentList extends AsyncTask<String, Void, String> {
        private getContentList() {
        }

        protected String doInBackground(String... url) {
            return prepareRequestToGetContentList(url[0]);
        }

        protected void onPostExecute(String paramString) {

            JSONArray jResponse = null;
            try {
                jResponse = new JSONArray(paramString);
                if (jResponse.getInt(0) == 0) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Content>>() {
                    }.getType();
                    ArrayList<Content> contentArrayList = gson.fromJson(jResponse.getJSONArray(2).toString(), listType);
                    if (contentArrayList.size() > 0) {
                        db.deleteContent();
                        for (int i = 0; i < contentArrayList.size(); i++) {
                            db.addContent(contentArrayList.get(i));
                        }
                    }
                }

                CONTENT_ID_CURSOR = db.getAllContentId();
                new getContent().execute(new String[]{
                        GET_CONTENT + String.valueOf(CONTENT_ID_CURSOR.getInt(0))
                        , String.valueOf(CONTENT_ID_CURSOR.getInt(0))});

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Syncing Content List...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public static String prepareRequestToGetContentList(String url) {

        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("IMEI", IMEI);
            jsonObject.accumulate("username", USERNAME);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.i(TAG, "[getContentList] JSON_REQUEST: " + json);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "*/*");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (IOException e) {
            Log.i("IOException", e.getLocalizedMessage());
        } catch (Exception e) {
            Log.i("InputStream", e.getLocalizedMessage());
        }

        Log.i(TAG, "[getContentList] " + result);
        // 11. return result
        return result;
    }

    private class getContent extends AsyncTask<String, Void, String> {
        private String contentId;

        private getContent() {
        }

        protected String doInBackground(String... params) {
            contentId = params[1];
            return prepareRequestToGetContent(params[0], contentId);
        }

        protected void onPostExecute(String response) {

            try {
                JSONArray jResponse = new JSONArray(response);
                if (jResponse.getInt(0) == 0) {
                    String contentDoc = jResponse.getString(4);
                    db.updateContentData(contentId, contentDoc);
                    Log.i("[Sync]", contentId);
                }

                if (CONTENT_ID_CURSOR.moveToNext()) {
                    new getContent().execute(new String[]{
                            GET_CONTENT + String.valueOf(CONTENT_ID_CURSOR.getInt(0)),
                            String.valueOf(CONTENT_ID_CURSOR.getInt(0))});
                } else {
                    db.updateUserLastSyncDate(USERNAME);
                    btnAccessApp.setVisibility(View.VISIBLE);
                    syncCallback.onSynced();
                    progressDialog.dismiss();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String prepareRequestToGetContent(String url, String contentId) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("IMEI", IMEI);
            jsonObject.accumulate("username", USERNAME);
            jsonObject.accumulate("contentId", contentId);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.i(TAG, "[GetContent] JSON_REQUEST: " + json);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "*/*");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.i("InputStream", e.getLocalizedMessage());
        }

        Log.i(TAG, "[GetContent] " + result);
        // 11. return result
        return result;

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isConnected()) {
            showSettingsAlert();
        }
    }

    public void showSettingsAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Internet Settings");
        builder.setMessage("Can't connect to internet. Do you want to go to settings menu?");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent localIntent = new Intent("android.settings.SETTINGS");
                startActivity(localIntent);
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static abstract interface SyncCallback {
        public abstract void onNewContentAvailable();

        public abstract void onSyncMandatory();

        public abstract void onSynced();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            syncCallback = (SyncCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onNewContentAvailable");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        syncCallback = null;
    }
}
