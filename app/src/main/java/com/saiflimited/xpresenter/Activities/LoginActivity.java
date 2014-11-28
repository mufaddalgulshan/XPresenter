package com.saiflimited.xpresenter.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.R;
import com.saiflimited.xpresenter.Views.Fragments.LoginFragment;
import com.saiflimited.xpresenter.Views.Fragments.SyncFragment;
import com.saiflimited.xpresenter.Views.Fragments.ValidateFragment;


public class LoginActivity extends ActionBarActivity implements
        LoginFragment.UserCallback,
        ValidateFragment.ValidateCallback,
        SyncFragment.SyncCallback {

    DatabaseHandler db;
    TextView lblMessageBox;
    SyncFragment mSyncFragment;
    LoginFragment mLoginFragment;
    ValidateFragment mValidateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(appname);
        toolbar.setBackgroundColor(Color.GRAY);
        setSupportActionBar(toolbar);
//
//        // Now retrieve the DrawerLayout so that we can set the status bar color.
//        // This only takes effect on Lollipop, or when using translucentStatusBar
//        // on KitKat.
//        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
//        drawerLayout.setBackgroundColor(Color.GRAY);

        this.db = DatabaseHandler.getInstance(this);
        this.lblMessageBox = ((TextView) findViewById(R.id.lblMessageBox));
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
