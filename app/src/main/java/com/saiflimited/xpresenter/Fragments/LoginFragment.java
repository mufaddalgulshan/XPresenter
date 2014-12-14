package com.saiflimited.xpresenter.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.Models.Publisher.ContentType;
import com.saiflimited.xpresenter.Models.Publisher.GetPublisherList;
import com.saiflimited.xpresenter.Models.Publisher.Publisher;
import com.saiflimited.xpresenter.Models.User.User;
import com.saiflimited.xpresenter.R;
import com.saiflimited.xpresenter.Utils.GPSTracker;
import com.saiflimited.xpresenter.Utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.saiflimited.xpresenter.Fragments.LoginFragment.LoginCallback} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private static final int FIRST_ACCESS = 1;
    private static final int NOT_FIRST_ACCESS = 2;
    private static final String GET_PUBLISHER_LIST = "http://uatwebservices.ipremios.com/XPresenter/GetPublisherList";
    private static final String GET_USER_LIST = "http://uatwebservices.ipremios.com/XPresenter/GetUserList";
    private static double LATITUDE = 0.0D;
    private static double LONGITUDE = 0.0D;
    private static String IMEI;
    private static String LOGO;
    private static String BACKGROUND;
    private boolean firstAccess;
    private DatabaseHandler db;
    private GPSTracker GPS;
    private ProgressDialog progressDialog;
    private boolean userExists;
    private EditText txtUsername;
    private Button btnContinue;
    private LoginCallback mLoginCallback;
    private ScrollView layout;
    private ImageView imgLogo;

    public static String prepareRequestToGetPublisherList(String url) {

        InputStream inputStream;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json;

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("IMEI", IMEI);
            jsonObject.accumulate("LocationLatitude", LATITUDE);
            jsonObject.accumulate("LocationLongitude", LONGITUDE);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.i(TAG, "[GetPublisherList] JSON_REQUEST: " + json);

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
                result = Utils.convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (IOException e) {
            Log.i("IOException", e.getLocalizedMessage());
        } catch (Exception e) {
            Log.i("InputStream", e.getLocalizedMessage());
        }

        Log.i(TAG, "[GetPublisherList] " + result);
        // 11. return result
        return result;
    }

    public static String prepareRequestToGetUserList(String url) {
        InputStream inputStream;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json;

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("IMEI", IMEI);
            jsonObject.accumulate("LocationLatitude", LATITUDE);
            jsonObject.accumulate("LocationLongitude", LONGITUDE);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.i(TAG, "[GetUserList] JSON_REQUEST: " + json);

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
                result = Utils.convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.i("InputStream", e.getLocalizedMessage());
        }

        Log.i(TAG, "[GetUserList] " + result);
        // 11. return result
        return result;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.db = DatabaseHandler.getInstance(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        if (view != null) {

            btnContinue = (Button) view.findViewById(R.id.btnContinue);
            txtUsername = (EditText) view.findViewById(R.id.txtUsername);
            layout = (ScrollView) getActivity().findViewById(R.id.container);
            imgLogo = (ImageView) getActivity().findViewById(R.id.imageView1);

            IMEI = ((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

            GPS = new GPSTracker(getActivity());

            if (GPS.canGetLocation()) {

                LATITUDE = GPS.getLatitude();
                LONGITUDE = GPS.getLongitude();

                if (Utils.isConnected(getActivity())) {

                    new getPublisherList().execute(GET_PUBLISHER_LIST);
                    new getUserList().execute(GET_USER_LIST);

                }

            } else {

                showGPSSettingsAlert();

            }

            this.btnContinue.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    userExists = db.usernameExists(txtUsername.getText().toString());

                    firstAccess = isFirstAccess(txtUsername.getText().toString());

                    if (firstAccess) {

                        mLoginCallback.onContinue(txtUsername.getText().toString(),
                                userExists, FIRST_ACCESS);

                    } else {

                        mLoginCallback.onContinue(txtUsername.getText().toString(), userExists, NOT_FIRST_ACCESS);

                    }
                }

            });

            txtUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    userExists = db.usernameExists(txtUsername.getText().toString());
                    firstAccess = isFirstAccess(txtUsername.getText().toString());

                    if (firstAccess) {

                        mLoginCallback.onContinue(txtUsername.getText().toString(),
                                userExists, FIRST_ACCESS);

                    } else {

                        mLoginCallback.onContinue(txtUsername.getText().toString(), userExists, NOT_FIRST_ACCESS);

                    }
                    return true;
                }
            });

        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mLoginCallback = (LoginCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onContinue");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLoginCallback = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!GPS.canGetLocation()) {
            showGPSSettingsAlert();
        }
    }

    public void showGPSSettingsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("GPS Settings");
        builder.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent localIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                startActivity(localIntent);
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private boolean isFirstAccess(String username) {
        return db.isFirstAccess(username);
    }

    public static abstract interface LoginCallback {
        public abstract void onContinue(String username, boolean userExists, int access);

        public abstract void displayMessage(String message);

    }

    private class getPublisherList extends AsyncTask<String, Void, String> {
        private getPublisherList() {
        }

        protected String doInBackground(String... paramVarArgs) {
            return prepareRequestToGetPublisherList(paramVarArgs[0]);
        }

        protected void onPostExecute(String paramString) {

            Gson gson = new Gson();
            GetPublisherList getPublisherList = gson.fromJson(paramString, GetPublisherList.class);
            if (getPublisherList != null) {
                if (getPublisherList.getReturnCode() == 0 || getPublisherList.getReturnCode() == 1) {

                    if (getPublisherList.getReturnCode() == 1) {
                        mLoginCallback.displayMessage(getPublisherList.getMessage());
                    }

                    ArrayList<Publisher> publisherArrayList = getPublisherList.getPublisherList();
                    if (publisherArrayList.size() > 0) {
                        db.deleteContentType();
                        db.deletePublisher();
                    }

                    for (int i = 0; i < publisherArrayList.size(); i++) {
                        Publisher publisher = publisherArrayList.get(i);
                        db.addPublisher(publisher);
                        for (int j = 0; j < publisher.getContentTypeList().size(); j++) {
                            ContentType contentType = publisher.getContentTypeList().get(j);
                            contentType.setPublisherCode(publisher.getCode());
                            db.addPublisherContentType(contentType);
                        }
                    }

                    LOGO = db.getLogo();
                    BACKGROUND = db.getBackground();

                    if (LOGO != null) {
                        imgLogo.setImageBitmap(Utils.getBitmapFromBase64(LOGO));
                    }

                    if (BACKGROUND != null) {
                        Drawable background = new BitmapDrawable(Utils.getBitmapFromBase64(BACKGROUND));
                        layout.setBackground(background);
                    }

                } else if (getPublisherList.getReturnCode() == 2) {
                    mLoginCallback.displayMessage(getPublisherList.getMessage());
                }
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginFragment.this.getActivity());
            progressDialog.setMessage("Syncing Publisher and User List...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private class getUserList extends AsyncTask<String, Void, String> {
        private getUserList() {
        }

        protected String doInBackground(String... paramVarArgs) {
            return prepareRequestToGetUserList(paramVarArgs[0]);
        }

        protected void onPostExecute(String paramString) {

            try {
                JSONArray jResponse = new JSONArray(paramString);

                if (jResponse.getInt(0) == 0 || jResponse.getInt(0) == 1) {
                    if (jResponse.getInt(0) == 1) {
                        mLoginCallback.displayMessage(jResponse.getString(1));
                    }
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<User>>() {
                    }.getType();
                    ArrayList<User> userArrayList = gson.fromJson(jResponse.getJSONArray(2).toString(), listType);
                    if (userArrayList.size() > 0) {
                        db.deleteUserList(userArrayList);
                        for (int i = 0; i < userArrayList.size(); i++) {
                            if (!db.usernameExists(userArrayList.get(i).getUsername())) {
                                db.addUser(userArrayList.get(i));
                                db.updateUserLastUpdateDate(userArrayList.get(i));
                            }
                        }
                    }
                } else if (jResponse.getInt(0) == 2) {
                    mLoginCallback.displayMessage(jResponse.getString(1));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }
}
