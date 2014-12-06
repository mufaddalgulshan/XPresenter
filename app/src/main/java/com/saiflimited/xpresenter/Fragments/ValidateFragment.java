package com.saiflimited.xpresenter.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.saiflimited.xpresenter.DB.DatabaseHandler;
import com.saiflimited.xpresenter.R;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ValidateFragment.ValidateCallback} interface
 * to handle interaction events.
 */
public class ValidateFragment extends Fragment {

    private static final int FIRST_ACCESS = 1;
    private static final int NOT_FIRST_ACCESS = 2;
    private String username;
    private Button btnSendNewPIN;
    private Button btnValidate;
    private DatabaseHandler db;
    private Bundle mBundle;
    private boolean mFirstAccess;
    private String mPIN = "";
    private EditText txtPIN;
    private Toast mToast;

    private ValidateCallback mValidateCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseHandler.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_validate, container, false);

        if (view != null) {
            mBundle = getArguments();
            username = mBundle.getString("USERNAME");
            if (mBundle.getInt("ACCESS") == FIRST_ACCESS) {
                mFirstAccess = true;
            } else if (mBundle.getInt("ACCESS") == NOT_FIRST_ACCESS)
                mFirstAccess = false;


            btnValidate = (Button) view.findViewById(R.id.btnValidate);
            btnSendNewPIN = (Button) view.findViewById(R.id.btnSendNewPIN);
            txtPIN = ((EditText) view.findViewById(R.id.txtPIN));

            if (mFirstAccess) {
                sendSMS(String.valueOf(generatePIN()));
                btnSendNewPIN.setVisibility(View.VISIBLE);
            } else {
                btnSendNewPIN.setVisibility(View.GONE);
            }

            btnValidate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (validatePIN()) {
                        if (loginAllowed(username)) {
                            mValidateCallback.onValidate(username, true);
                        } else {
                            mValidateCallback.onLoginRestricted(username);
                        }
                    } else {
                        mValidateCallback.onValidate(username, false);
                    }
                }
            });
            txtPIN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (validatePIN()) {
                        if (loginAllowed(username)) {
                            mValidateCallback.onValidate(username, true);
                        } else {
                            mValidateCallback.onLoginRestricted(username);
                        }
                    } else {
                        mValidateCallback.onValidate(username, false);
                    }
                    return true;
                }
            });

            btnSendNewPIN.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramAnonymousView) {
                    sendSMS(generatePIN());
                }
            });

        }
        return view;
    }

    private boolean loginAllowed(String username) {
        String loginRestriction = db.getLoginRestriction(username);
        boolean valid = false;
        if (!loginRestriction.isEmpty()) {
            Date date = new Date();

            int lastMonth = Integer.parseInt(loginRestriction.substring(0, 2));
            int month = Integer.parseInt((String) android.text.format.DateFormat.format("MM", date)); //06

            if (month < lastMonth) {
                valid = true;
            } else if (month == lastMonth) {
                int lastDay = Integer.parseInt(loginRestriction.substring(3, 5));
                int day = Integer.parseInt((String) android.text.format.DateFormat.format("dd", date)); //20
                if (day <= lastDay) {
                    valid = true;
                }
            }

        } else {
            valid = true;
        }

        return valid;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mValidateCallback = (ValidateCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ValidateCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mValidateCallback = null;
    }

    public String generatePIN() {
        mPIN = String.valueOf(1000 + (int) (9000.0D * Math.random()));
        return mPIN;
    }

    protected boolean validatePIN() {

        if (mFirstAccess) {
            if (txtPIN.getText().toString().equals(mPIN)) {
                db.updatePIN(username, mPIN);
                Log.i("ValidateFragment", "PIN updated " + mPIN);
                return true;
            } else {
                return false;
            }
        } else {
            if (db.validatePIN(username, txtPIN.getText().toString())) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void sendSMS(String PIN) {

        String number = db.getMobileNumber(username);
        String message = "Your pin to login to " + getResources().getString(R.string.app_name) + " is " + PIN + ".";

        /** Getting an instance of SmsManager to sent sms message from the application*/
        SmsManager smsManager = SmsManager.getDefault();

        /** Sending the Sms message to the intended party */
        smsManager.sendTextMessage(number, null, message, null, null);

//        if (mToast == null) {
//            mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);
//        }
//        mToast.setText(PIN);
//        mToast.show();
    }

    public interface ValidateCallback {
        public void onValidate(String username, boolean valid);

        public void onLoginRestricted(String username);
    }

}
