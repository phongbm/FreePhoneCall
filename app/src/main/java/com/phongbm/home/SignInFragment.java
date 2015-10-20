package com.phongbm.home;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.phongbm.common.CommonValue;
import com.phongbm.freephonecall.MainActivity;
import com.phongbm.freephonecall.R;

public class SignInFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SignInFragment.class.getSimpleName();
    private static final int REQUEST_COUNTRY_CODE = 0;
    private static final int MIN_LENGTH_PHONE_NUMBER = 10;
    private static final int MAX_LENGTH_PHONE_NUMBER = 11;
    private static final int MIN_LENGTH_PASSWORD = 8;

    private View view;
    private EditText edtPhoneNumber, edtPassword, edtCountryCode;
    private Button btnSignIn;
    private String countryCode;
    private boolean isFillPhoneNumber, isFillPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView...");
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        this.initializeToolbar();
        this.initializeComponent();
        return view;
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (MainFragment) this.getActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.sign_in);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeComponent() {
        btnSignIn = (Button) view.findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);
        edtPhoneNumber = (EditText) view.findViewById(R.id.edt_phone_number);
        edtPassword = (EditText) view.findViewById(R.id.edt_password);
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() >= MIN_LENGTH_PHONE_NUMBER
                        && s.length() <= MAX_LENGTH_PHONE_NUMBER) {
                    isFillPhoneNumber = true;
                    SignInFragment.this.enabledButtonLogin();
                } else {
                    isFillPhoneNumber = false;
                    btnSignIn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() >= MIN_LENGTH_PASSWORD) {
                    isFillPassword = true;
                    SignInFragment.this.enabledButtonLogin();
                } else {
                    isFillPassword = false;
                    btnSignIn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtCountryCode = (EditText) view.findViewById(R.id.edt_country_code);
        edtCountryCode.setOnClickListener(this);
        edtCountryCode.setText("United States (+1)");
        countryCode = "(+1)";
    }

    private void enabledButtonLogin() {
        if (isFillPhoneNumber && isFillPassword) {
            btnSignIn.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainFragment) this.getActivity()).showHomeFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.edt_country_code:
                Intent intent = new Intent(this.getActivity(), CountryCodeActivity.class);
                this.startActivityForResult(intent, REQUEST_COUNTRY_CODE);
                break;
            case R.id.btn_sign_in:
                final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
                progressDialog.setTitle("Signing in");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                if (phoneNumber.charAt(0) == '0') {
                    phoneNumber = phoneNumber.substring(1);
                }
                final String phoneNumberStandard = countryCode + " " + phoneNumber;
                final String password = edtPassword.getText().toString().trim();
                ParseUser.logInInBackground(phoneNumberStandard, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Snackbar.make(view, "There was an error logging in", Snackbar.LENGTH_LONG)
                                    .setAction("ACTION", null)
                                    .show();
                            return;
                        }
                        if (parseUser != null) {
                            parseUser.put("online", true);
                            parseUser.saveInBackground();
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(view, "Logged successfully", Snackbar.LENGTH_LONG)
                                    .setAction("ACTION", null)
                                    .setCallback(new Snackbar.Callback() {
                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {
                                            super.onDismissed(snackbar, event);
                                            Intent intent = new Intent(SignInFragment.this.getActivity(), MainActivity.class);
                                            SignInFragment.this.getActivity().startActivity(intent);
                                            SignInFragment.this.getActivity().finish();
                                        }
                                    });
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.parseColor("#4caf50"));
                            snackbar.show();
                        } else {
                            progressDialog.dismiss();
                            Snackbar.make(view, "That account does not exist", Snackbar.LENGTH_LONG)
                                    .setAction("ACTION", null)
                                    .show();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COUNTRY_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String content = data.getStringExtra(CommonValue.COUNTRY_CODE);
            countryCode = content.substring(content.indexOf("(+"));
            edtCountryCode.setText(content);
        }
    }

}