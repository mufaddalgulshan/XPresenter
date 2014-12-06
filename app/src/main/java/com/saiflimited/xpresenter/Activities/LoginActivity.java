package com.saiflimited.xpresenter.Activities;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.Fragments.LoginFragment;
import com.saiflimited.xpresenter.Fragments.SyncFragment;
import com.saiflimited.xpresenter.Fragments.ValidateFragment;
import com.saiflimited.xpresenter.R;
import com.saiflimited.xpresenter.Utils.Utils;


public class LoginActivity extends ActionBarActivity implements
        LoginFragment.LoginCallback,
        ValidateFragment.ValidateCallback,
        SyncFragment.SyncCallback {

    private static String LOGO;
    private static String BACKGROUND;
    DatabaseHandler db;
    TextView lblMessageBox;
    SyncFragment mSyncFragment;
    LoginFragment mLoginFragment;
    ValidateFragment mValidateFragment;
    private ScrollView container;
    private ImageView imgLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        container = (ScrollView) findViewById(R.id.container);
        imgLogo = (ImageView) findViewById(R.id.imageView1);

        this.db = DatabaseHandler.getInstance(this);
        this.lblMessageBox = ((TextView) findViewById(R.id.lblMessageBox));

        LOGO = db.getLogo();
        BACKGROUND = db.getBackground();

        if (LOGO != null) {
            imgLogo.setImageBitmap(Utils.getBitmapFromBase64(LOGO));
        }

        if (BACKGROUND != null) {
            Drawable background = new BitmapDrawable(Utils.getBitmapFromBase64(BACKGROUND));
            container.setBackground(background);
        }


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        this.mLoginFragment = new LoginFragment();
        transaction.replace(R.id.sample_content_fragment, this.mLoginFragment);
        transaction.commit();
    }

    @Override
    public void onContinue(String username, boolean userExists, int access) {
        if (userExists) {
            showMessage(getResources().getString(R.string.info_login));
            showValidateFragment(username, access);
            return;
        }
        showMessage(getResources().getString(R.string.error_username));
    }

    @Override
    public void displayMessage(String message) {
        showMessage(message);
    }

    @Override
    public void onValidate(String username, boolean valid) {
        if (valid) {
            showMessage("");
            db.updateUserLastLoginDate(username);
            showSyncFragment(username);
        } else {
            showMessage(getResources().getString(R.string.error_pin));
        }
    }

    @Override
    public void onLoginRestricted(String username) {
        showMessage(getResources().getString(R.string.error_login_restriction) + " " + username);
    }

    private void showMessage(String paramString) {
        this.lblMessageBox.setText(paramString);
    }

    private void showValidateFragment(String username, int access) {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mValidateFragment = new ValidateFragment();
        Bundle localBundle = new Bundle();
        localBundle.putString("USERNAME", username);
        localBundle.putInt("ACCESS", access);
        mValidateFragment.setArguments(localBundle);
        localFragmentTransaction.replace(R.id.sample_content_fragment, this.mValidateFragment);
        localFragmentTransaction.commit();
    }

    private void showSyncFragment(String username) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        this.mSyncFragment = new SyncFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        this.mSyncFragment.setArguments(bundle);
        transaction.replace(R.id.sample_content_fragment, this.mSyncFragment);
        transaction.commit();
    }

    @Override
    public void onNewContentAvailable() {
        showMessage(getResources().getString(R.string.new_content_available));
    }

    @Override
    public void onSyncMandatory() {
        showMessage(getResources().getString(R.string.sync_mandatory));
    }

    @Override
    public void onSynced() {
        showMessage("");
    }
}
